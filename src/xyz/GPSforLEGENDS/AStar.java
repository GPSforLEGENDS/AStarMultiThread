package xyz.GPSforLEGENDS;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Implementation of the AStar pathfinding algorithm with up to 2 threads searching
 * https://de.wikipedia.org/wiki/A*-Algorithmus
 * parallelization relies on the assumption that if i want to get from a to c and start searching from a to c and c to a
 * then we can end searching at the point of execution when both meet each other and trackback
 */
public class AStar {

    private boolean parallel;

    private NodeGrid grid;

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
     * If you want to call this function a second time, make sure to reset the grid to its original state by calling NodeGrid.reset();
     * @see NodeGrid#reset()
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
            PathWorker workerFromStart = new PathWorker(grid,start,end, true);
            PathWorker workerFromEnd = new PathWorker(grid,end,start, false);

            ExecutorService pool = Executors.newFixedThreadPool(2);
            try {
                pool.invokeAll(Arrays.asList(workerFromStart, workerFromEnd), 60, TimeUnit.MINUTES);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            pool.shutdown();

            path = reconstructPath(start, end);

            return path;
        }
        //one thread
        else{
            //setting up the predecessors
            PathWorker worker = new PathWorker(grid,start,end, true);
            //just call call, since it is one thread anyways;
            try {
                worker.call();
                return reconstructPath(start, end);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return path;
    }

    /**
     * reconstructs the path by following the predecessors. returns null if no path was found
     * @param start
     * @param end
     * @return list with the nodes from start to end. null if no path was found
     */
    public List<Node> reconstructPath(Node start, Node end) {

        //check if path exist
        if(Double.isNaN(start.getCostToReach(true)) || Double.isNaN(end.getCostToReach(true))){
            return null;
        }

        List<Node> path = new ArrayList<>();
        Node current = start;
        if(current.getPredecessor(true) == null) current = end;

        while(current != null){
            path.add(current);
            current = current.getPredecessor(true);
        }

        //reverse list if we tracked back from end to start
        if(start.getPredecessor(true) == null){
            Collections.reverse(path);
        }

        return path;
    }
}
