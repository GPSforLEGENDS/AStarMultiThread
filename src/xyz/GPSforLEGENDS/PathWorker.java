package xyz.GPSforLEGENDS;

import java.util.*;
import java.util.concurrent.Callable;

class PathWorker implements Callable<Void> {

    private NodeGrid grid;

    private Node startNode, endNode;

    //sorted by the estimated lowest cost.
    //for more infos look at inner class SortByDistance
    private PriorityQueue<Node> openList;

    //the unique thread id
    private long id;

    private boolean fromStart;


    /**
     * Constructor
     *
     * @param grid   the nodegrid to run the search on
     * @param startX
     * @param startY
     * @param endX
     * @param endY
     */
    PathWorker(NodeGrid grid, int startX, int startY, int endX, int endY, boolean fromStart) {
        this(grid, grid.getNode(startX, startY), grid.getNode(endX, endY), fromStart);
    }

    /**
     * COnstructor
     *
     * @param grid
     * @param start
     * @param end
     */
    PathWorker(NodeGrid grid, Node start, Node end, boolean fromStart) {
        this.grid = grid;
        this.startNode = start;
        this.endNode = end;
        openList = new PriorityQueue<>(new SortByDistance());
        this.fromStart = fromStart;
    }

    /**
     * function that runs the AStar Pathfinding algorithm.
     * Stops when either the end node is found OR if the pathfinder finds a node that is already closed by the other pathfinder (only if parallel is true in AStar)
     */
    private void aStarPathfinding() {
        startNode.setCostToReach(0, fromStart);
        openList.add(startNode);

        do {
            if(grid.isSolved()){
                return;
            }
            Node currentNode = openList.poll();
                if (currentNode.equals(endNode)) {
                    if(!grid.getAndSetSolved(true)) {
                        synchronized (grid) {
                            setupPredecessors(currentNode);
                            currentNode.setStatus(id);
                        }
                    }
                    return;
                }
                //found a node that has been marked by another pathworker
                if(currentNode.getStatus() != 0 && currentNode.getStatus() != id){
                        if(!grid.getAndSetSolved(true)) {
                            //findAndRepairPredecessors(currentNode);
                            synchronized (grid) {
                                setupPredecessors(currentNode);
                            }
                        }
                        return;
                }

                //setting the status
                currentNode.setStatus(id);

            //explore neighbours
            expandNode(currentNode);

        } while (!openList.isEmpty());
        noPathFound();
        return;
    }

    /**
     * expands the node and returns true if the other pathfinder was found during the expandation
     *
     * @param currentNode
     * @return
     */
    private void expandNode(Node currentNode) {
        for (Node neighbour : currentNode.getNeighbours()) {
            synchronized (neighbour) {
                if(!grid.isSolved()) {
                    //this node was already worked on
                    if (neighbour.getStatus() == id) continue;

                    double costToReach = currentNode.getCostToReach(fromStart) + calculateCostToReach(currentNode, neighbour);

                    if (openList.contains(neighbour) && costToReach >= neighbour.getCostToReach(fromStart)) continue;

                    neighbour.setCostToReach(costToReach, fromStart);
                    neighbour.setPredecessor(currentNode, fromStart);

                    //remove and add since the order might have changed
                    openList.remove(neighbour);
                    openList.add(neighbour);
                }
            }
        }
    }

    /**
     * sets the predecessors of neighbour to currentnode and reverses the predecessor order of all predecessors of neighbour
     * but only if the predecessor of currentNode != neightbour && predecessor of neightbour != currentNode
     * @param currentNode
     */
    private void setupPredecessors(Node currentNode) {

        Node neighbour = currentNode.getPredecessor(false);

        while(neighbour != null){
            synchronized (neighbour){
                neighbour.setPredecessor(currentNode, true);
                currentNode = neighbour;
                neighbour = currentNode.getPredecessor(false);
            }
        }
    }


    /**
     * calculates the cost to reach the neighbour Node from Node n
     *
     * @param n
     * @param neighbour
     * @return
     */
    private double calculateCostToReach(Node n, Node neighbour) {
        int x = Math.abs(n.getX() - neighbour.getX());
        int y = Math.abs(n.getY() - neighbour.getY());

        return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
    }

    /**
     * function to reset the successor of start and end node and set the costs to NAN to indicate that no path was found
     */
    private void noPathFound() {
        synchronized (grid) {
            if(!grid.isSolved()) {
                startNode.setPredecessor(null, true);
                startNode.setPredecessor(null, false);
                endNode.setPredecessor(null, true);
                endNode.setPredecessor(null, false);
                startNode.setCostToReach(Double.NaN, true);
                startNode.setCostToReach(Double.NaN, false);
                endNode.setCostToReach(Double.NaN, true);
                endNode.setCostToReach(Double.NaN, false);
            }
        }
    }

    @Override
    public Void call() throws Exception {
        id = Thread.currentThread().getId();
        aStarPathfinding();
        return null;
    }

    /**
     * Comparator for node distance
     * compares the distance of the nodes to the end node
     * distance = current cost to reach node + heuristic cost to reach the end
     * heuristic cost = air distance between the node and the end
     */
    private class SortByDistance implements Comparator<Node> {

        @Override
        public int compare(Node a, Node b) {

            double distanceA = a.getCostToReach(fromStart) + calculateHeuristicCost(a);
            double distanceB = b.getCostToReach(fromStart) + calculateHeuristicCost(b);

            double compared = distanceA - distanceB;

            if (compared > 0) return 1;
            else return -1;
        }

    }
    /**
     * calculates the heurstic (air distance) costs to reach the end node from node n
     *
     * @param n
     * @return
     */
    private double calculateHeuristicCost(Node n) {
        int x = Math.abs(n.getX() - endNode.getX());
        int y = Math.abs(n.getY() - endNode.getY());

        return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
    }

    public Node getStartNode() {
        return this.startNode;
    }

    public Node getEndNode() {
        return this.endNode;
    }
}
