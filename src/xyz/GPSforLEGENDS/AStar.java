package xyz.GPSforLEGENDS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Implementation of the AStar pathfinding algorithm with up to 2 threads searching
 * https://de.wikipedia.org/wiki/A*-Algorithmus
 * parallelisation relies on the assumption that if i want to get from a to c and start searching from a to c and c to a
 * then we can end searching at the point of execution when both meet each other and traceback
 */
public class AStar {

    private boolean parallel;

    private NodeGrid grid;

    private int startX, startY, endX, endY;

    /**
     * Constructor
     * @param grid the grid to run the pathfinding search on
     */
    public AStar(NodeGrid grid){
        this(grid, false);
    }

    /**
     * Constructor
     * @param grid the grid to run the pathfinding search on
     * @param parallel if true then the pathfinding Algorithm will run with 2 threads
     */
    public AStar(NodeGrid grid, boolean parallel){
        this.parallel = parallel;
        this.grid = grid;
    }

    /**
     * finds the shortest path from start to end.
     * @param startX
     * @param startY
     * @param endX
     * @param endY
     * @return A list containing the Nodes ordered from start to end. empty if no path was found or the points are outside of the grid
     * @exception IllegalArgumentException if coordinates are outside of the grid or are not traversable
     */
    public List<Node> findPath(int startX, int startY, int endX, int endY){

        Node start = grid.getNode(startX,startY);
        Node end = grid.getNode(endX,endY);

        List<Node> path = new ArrayList<>();

        if(start == null || end == null) throw new IllegalArgumentException("The start or end coordinates are outside of the grid");

        if(!start.isTraversable() || !end.isTraversable()) throw new IllegalArgumentException("The start or end node is not traversable");

        //2 threads
        if(parallel){
            PathWorker workerFromStart = new PathWorker(grid,start,end,true);
            PathWorker workerFromEnd = new PathWorker(grid,end,start,false);

            workerFromStart.start();
            workerFromEnd.start();

            Node bridgeA = null;
            Node bridgeB = null;

            //loop to wait for results
            while(true){
                if(!workerFromStart.isAlive()){
                    bridgeA = workerFromStart.getBridgeFrom();
                    bridgeB = workerFromStart.getBridgeTo();
                    if(!workerFromEnd.isAlive() && workerFromEnd.getBridgeFrom() == null && bridgeA == null && bridgeB == null) break;
                    if(bridgeA != null && bridgeB != null) break;
                }
                if(!workerFromEnd.isAlive()){
                    bridgeA = workerFromEnd.getBridgeTo();
                    bridgeB = workerFromEnd.getBridgeFrom();
                    if(!workerFromStart.isAlive() && workerFromStart.getBridgeFrom() == null && bridgeA == null && bridgeB == null) break;
                    if(bridgeA != null && bridgeB != null) break;
                }
            }

            //is there a path?
            if(bridgeA != null){
                path.add(bridgeA);
                Node current = bridgeA;
                do{
                    current = current.getPredecessor();
                    path.add(current);
                } while(current.getPredecessor() != null);

                Collections.reverse(path);

                path.add(bridgeB);
                current = bridgeB;

                do{
                    current = current.getPredecessor();
                    path.add(current);
                } while(current.getPredecessor() != null);
            }
            else if(end.getPredecessor() != null){
                //normal backtracking from the end
                path.add(end);
                Node current = end;
                do{
                    current = current.getPredecessor();
                    path.add(current);
                } while(current.getPredecessor() != null);

                Collections.reverse(path);
            }
        }
        //run in main thread
        else{
            //setting up the predecessors
            PathWorker worker = new PathWorker(grid,start,end,true);
            //just call run, since it is one thread anyways;
            worker.run();

            //backtracking from end node
            if(end.getPredecessor() != null){
                path.add(end);
                Node current = end;
                do{
                    current = current.getPredecessor();
                    path.add(current);
                } while(current.getPredecessor() != null);
            }
            Collections.reverse(path);
        }

        return path;
    }

}
