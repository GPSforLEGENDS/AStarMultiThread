package xyz.GPSforLEGENDS;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

class PathWorker extends Thread{

    private NodeGrid grid;

    private Node startNode, endNode;

    private boolean isStart;

    // Acts as a bridge to find where both finder met
    private Node from, to;

    //sorted by the estimated lowest cost.
    //for more infos look at inner class SortByDistance
    Queue<Node> openList = new PriorityQueue<>(new SortByDistance());

    //flag to indicate if one pathfinder found the other
    private static AtomicBoolean foundFlag = new AtomicBoolean(false);

    /**
     * Constructor
     * @param grid the nodegrid to run the search on
     * @param startX
     * @param startY
     * @param endX
     * @param endY
     * @param isStart true if this is the 'real' (which means not the endpoint that is used as the start point for the second pathfinder) start point
     */
    PathWorker(NodeGrid grid, int startX, int startY, int endX, int endY, boolean isStart){
        this.grid = grid;
        this.startNode = grid.getNode(startX,startY);
        this.endNode = grid.getNode(endX,endY);
        this.isStart = isStart;
    }

    /**
     * COnstructor
     * @param grid
     * @param start
     * @param end
     * @param isStart true if this is the 'real' (which means not the endpoint that is used as the start point for the second pathfinder) start point
     */
    PathWorker(NodeGrid grid, Node start, Node end, boolean isStart){
        this.grid = grid;
        this.startNode = start;
        this.endNode = end;
        this.isStart = isStart;
    }

    public void run(){
        aStarPathfinding();
    }

    /**
     * function that runs the AStar Pathfinding algorithm.
     * Stops when either the end node is found OR if the pathfinder finds a node that is already closed by the other pathfinder (only if parallel is true in AStar)
     */
    private void aStarPathfinding(){
        startNode.setCostToReach(0, isStart);
        openList.add(startNode);

        do {
            if (foundFlag.compareAndSet(false, false)) {
                Node currentNode = openList.poll();
                synchronized (currentNode) {
                    if (currentNode.equals(endNode)) return;
                    //check if the other pathfinder already worked on this node
                    if (currentNode.getStatus() == 1 || currentNode.getStatus() == 2) {
                        if (foundFlag.compareAndSet(false, true)) {
                            findBridge(currentNode);
                        }
                        return;
                    }

                    //setting the status
                    if (isStart) currentNode.setStatus(1);
                    else currentNode.setStatus(2);
                }
                //explore neighbours and stop if the pathfinder met each other
                expandNode(currentNode);
            }
            //stop working if the foundFlag is set
            else return;
        } while (!openList.isEmpty()) ;

        noPathFound();
    }

    /**
     * expands the node and returns true if the other pathfinder was found during the expandation
     * @param currentNode
     * @return
     */
    private void expandNode(Node currentNode) {
        for(Node neighbour : currentNode.getNeighbours()){
            //stop working as soon as they found each other
            if(foundFlag.compareAndSet(false,false)) {
                synchronized (neighbour) {
                    if (isStart) {
                        if (neighbour.getStatus() == 1) continue;
                    } else {
                        if (neighbour.getStatus() == 2) continue;
                    }

                    double costToReach = currentNode.getCostToReach(isStart) + calculateCostToReach(currentNode, neighbour);

                    if (openList.contains(neighbour) && costToReach >= neighbour.getCostToReach(isStart)) continue;

                    double oldCost = neighbour.getCostToReach(isStart);

                    neighbour.setCostToReach(costToReach, isStart);

                    //remove and add since the order might have changed
                    openList.remove(neighbour);
                    openList.add(neighbour);

                    if(neighbour.getStatus() == 1 || neighbour.getStatus() == 2) neighbour.setCostToReach(oldCost,isStart);
                    else neighbour.setPredecessor(currentNode);
                }
            }
            //stop working if the foundFlag is set
            else return;
        }
    }

    /**
     * finds the bridge from the currentNode to the neighbour node of the opposite status AND the shortest costToReach
     * @param currentNode
     */
    private void findBridge(Node currentNode) {
        Node shortest = null;
        if(currentNode.getStatus() == 1){
            for(Node neighbour : currentNode.getNeighbours()){
                if(neighbour.getStatus() == 2){
                    if(shortest == null || shortest.getCostToReach(isStart) > neighbour.getCostToReach(isStart)){
                        shortest = neighbour;
                    }
                }
            }
        }

        else if(currentNode.getStatus() == 2){
            for(Node neighbour : currentNode.getNeighbours()){
                if(neighbour.getStatus() == 1){
                    if(shortest == null || shortest.getCostToReach(isStart) > neighbour.getCostToReach(isStart)){
                        shortest = neighbour;
                    }
                }
            }
        }

        from = shortest;
        to = currentNode;

        repairBridge();
    }

    /**
     * repairs the bridge in case that the predecessors are wrong because of multithreading
     */
    private void repairBridge() {
        Node shortest = null;
        for(Node neighbour : from.getNeighbours()){
            if(neighbour.getStatus() == from.getStatus()){
                if(shortest == null || shortest.getCostToReach(isStart) > neighbour.getCostToReach(isStart)){
                    shortest = neighbour;
                }
            }
        }
        from.setPredecessor(shortest);
        shortest = null;

        for(Node neighbour : to.getNeighbours()){
            if(neighbour.getStatus() == to.getStatus()){
                if(shortest == null || shortest.getCostToReach(!isStart) > neighbour.getCostToReach(!isStart)){
                    shortest = neighbour;
                }
            }
        }
        to.setPredecessor(shortest);
        double x = to.getCostToReach(!isStart);
    }

    /**
     * calculates the cost to reach the neighbour Node from Node n
     * @param n
     * @param neighbour
     * @return
     */
    private double calculateCostToReach(Node n, Node neighbour){
        int x = Math.abs(n.getX() - neighbour.getX());
        int y = Math.abs(n.getY() - neighbour.getY());

        return Math.sqrt(Math.pow(x,2) + Math.pow(y,2));
    }

    /**
     * function to reset the successor of start and end node to indicate that no path was found
     */
    private void noPathFound(){
        startNode.setPredecessor(null);
        endNode.setPredecessor(null);
    }

    /**
     * Comparator for node distance
     * compares the distance of the nodes to the end node
     * distance = current cost to reach node + heuristic cost to reach the end
     * heuristic cost = air distance between the node end the end
     */
    private class SortByDistance implements Comparator<Node>{

        @Override
        public int compare(Node a, Node b) {

            double distanceA = a.getCostToReach(isStart) + calculateHeuristicCost(a);
            double distanceB = b.getCostToReach(isStart) + calculateHeuristicCost(b);

            double compared = distanceA - distanceB;

            if(compared < 0) return -1;
            if(compared > 0) return 1;
            return 0;
        }
    }

    /**
     * calculates the heurstic (air distance) costs to reach the end node from node n
     * @param n
     * @return
     */
    private double calculateHeuristicCost(Node n){
        int x = Math.abs(n.getX() - endNode.getX());
        int y = Math.abs(n.getY() - endNode.getY());

        return Math.sqrt(Math.pow(x,2) + Math.pow(y,2));
    }

    Node getBridgeFrom(){
        return from;
    }

    Node getBridgeTo(){
        return to;
    }
}
