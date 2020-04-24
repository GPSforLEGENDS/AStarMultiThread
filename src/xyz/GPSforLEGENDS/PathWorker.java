package xyz.GPSforLEGENDS;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;

class PathWorker implements Callable<Node> {

    private NodeGrid grid;

    private Node startNode, endNode;

    private boolean isStart;

    // stores the node where both pathfinder met
    private Node found;

    //sorted by the estimated lowest cost.
    //for more infos look at inner class SortByDistance
    Queue<Node> openList;

    //flag to indicate if one pathfinder found the other
    private AtomicBoolean foundFlag;

    /**
     * Constructor
     * @param grid the nodegrid to run the search on
     * @param startX
     * @param startY
     * @param endX
     * @param endY
     * @param isStart true if this is the 'real' (which means not the endpoint that is used as the start point for the second pathfinder) start point
     * @param foundFlag
     */
    PathWorker(NodeGrid grid, int startX, int startY, int endX, int endY, boolean isStart, AtomicBoolean foundFlag){
        this.grid = grid;
        this.startNode = grid.getNode(startX,startY);
        this.endNode = grid.getNode(endX,endY);
        this.isStart = isStart;
        this.foundFlag = foundFlag;
        openList = new PriorityQueue<>(new SortByDistance());
    }

    /**
     * COnstructor
     * @param grid
     * @param start
     * @param end
     * @param isStart true if this is the 'real' (which means not the endpoint that is used as the start point for the second pathfinder) start point
     * @param foundFlag
     */
    PathWorker(NodeGrid grid, Node start, Node end, boolean isStart, AtomicBoolean foundFlag){
        this.grid = grid;
        this.startNode = start;
        this.endNode = end;
        this.isStart = isStart;
        this.foundFlag = foundFlag;
        openList = new PriorityQueue<>(new SortByDistance());
    }

    /**
     * function that runs the AStar Pathfinding algorithm.
     * Stops when either the end node is found OR if the pathfinder finds a node that is already closed by the other pathfinder (only if parallel is true in AStar)
     */
    private void aStarPathfinding() {
        startNode.setCostToReach(0, isStart);
        openList.add(startNode);

        do {
            //check if the other pathfinder already found the other or if no path can be found
            if (!foundFlag.get() && !Double.isNaN(startNode.getCostToReach(true))) {
                Node currentNode = openList.poll();
                if (currentNode.equals(endNode)) {
                    if (foundFlag.compareAndSet(false, true)) {
                        found = currentNode;
                    }
                    return;
                }

                if (currentNode.getStatus() == 1 || currentNode.getStatus() == 2) {
                    if (foundFlag.compareAndSet(false, true)) {
                        found = currentNode;
                    }
                    return;
                }

                //setting the status
                if (isStart) currentNode.setStatus(1);
                else currentNode.setStatus(2);

                //explore neighbours and stop if the pathfinder met each other
                expandNode(currentNode);
            } else {
                return;
            }
        } while (!openList.isEmpty());
        if(!foundFlag.get())
        noPathFound();
    }

    /**
     * expands the node and returns true if the other pathfinder was found during the expandation
     * @param currentNode
     * @return
     */
    private void expandNode(Node currentNode) {
        for (Node neighbour : currentNode.getNeighbours()) {
            if (!foundFlag.get()) {
                if (isStart) {
                    if (neighbour.getStatus() == 1) continue;
                } else {
                    if (neighbour.getStatus() == 2) continue;
                }

                double costToReach = currentNode.getCostToReach(isStart) + calculateCostToReach(currentNode, neighbour);

                if (openList.contains(neighbour) && costToReach >= neighbour.getCostToReach(isStart)) continue;

                neighbour.setCostToReach(costToReach, isStart);
                neighbour.setPredecessor(currentNode, isStart);

                //remove and add since the order might have changed
                openList.remove(neighbour);
                openList.add(neighbour);
            }
        }
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
     * function to reset the successor of start and end node and set the costs to NAN to indicate that no path was found
     */
    private void noPathFound(){
        startNode.setPredecessor(null, true);
        endNode.setPredecessor(null, true);
        startNode.setPredecessor(null, false);
        endNode.setPredecessor(null, false);
        startNode.setCostToReach(Double.NaN, true);
        startNode.setCostToReach(Double.NaN, false);
        endNode.setCostToReach(Double.NaN, true);
        endNode.setCostToReach(Double.NaN, false);
    }

    @Override
    public Node call() throws Exception {
        aStarPathfinding();
        return found;
    }

    /**
     * Comparator for node distance
     * compares the distance of the nodes to the end node
     * distance = current cost to reach node + heuristic cost to reach the end
     * heuristic cost = air distance between the node and the end
     */
    private class SortByDistance implements Comparator<Node>{

        @Override
        public int compare(Node a, Node b) {

            double distanceA = a.getCostToReach(isStart) + calculateHeuristicCost(a);
            double distanceB = b.getCostToReach(isStart) + calculateHeuristicCost(b);

            double compared = distanceA - distanceB;

            if(compared > 0) return 1;
            else return -1;
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

    Node getFound(){
        return this.found;
    }

    public Node getStartNode(){
        return this.startNode;
    }

    public Node getEndNode(){
        return this.endNode;
    }
}
