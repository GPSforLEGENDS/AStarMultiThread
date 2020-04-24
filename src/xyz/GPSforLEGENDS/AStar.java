package xyz.GPSforLEGENDS;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Implementation of the AStar pathfinding algorithm with up to 2 threads searching
 * https://de.wikipedia.org/wiki/A*-Algorithmus
 * parallelisation relies on the assumption that if i want to get from a to c and start searching from a to c and c to a
 * then we can end searching at the point of execution when both meet each other and trackback
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
            AtomicBoolean foundFlag = new AtomicBoolean(false);
            PathWorker workerFromStart = new PathWorker(grid,start,end,true, foundFlag);
            PathWorker workerFromEnd = new PathWorker(grid,end,start,false, foundFlag);

            ExecutorService pool = Executors.newFixedThreadPool(2);

            try {
                pool.invokeAll(Arrays.asList(workerFromStart, workerFromEnd), 2, TimeUnit.MINUTES);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Node found = null;

            //loop to wait for results
            found = getResultFromPathWorkers(pool, workerFromStart, workerFromEnd);

            reconstructPath(path, found);
        }
        //run in main thread
        else{
            //setting up the predecessors
            PathWorker worker = new PathWorker(grid,start,end,true, new AtomicBoolean(false));
            //just call call, since it is one thread anyways;
            try {
                worker.call();
                reconstructPath(path, end);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return path;
    }

    /**
     * function that waits for a result from one of the pathfinders
     * terminates as soon as one of the following statements are true
     * 1. one pathfinder has found all its reachable nodes and didnt find the other patfinder nor the endnode. This indicates that the endNode cant be reached from the startpoint
     * 2. one pathfinder found the other pathfinder or the endnode
     * 3. timeout (2 minutes)
     * @param workerFromStart
     * @param workerFromEnd
     * @return
     */
    private Node getResultFromPathWorkers(ExecutorService pool, PathWorker workerFromStart, PathWorker workerFromEnd) {
        Node found = null;

        found = workerFromStart.getFound();
        //if(!workerFromEnd.isAlive() && workerFromEnd.getFound() == null && found == null) break;
        if (Double.isNaN(workerFromStart.getStartNode().getCostToReach(true))) return found;
        if (found != null) return found;
        found = workerFromEnd.getFound();
        //if(!workerFromStart.isAlive() && workerFromStart.getFound() == null && found == null) break;
        if (Double.isNaN(workerFromEnd.getStartNode().getCostToReach(true))) return found;
        if (found != null) return found;

        return found;
    }

    /**
     * reconstructs the path by following the predecessors
     * @param path the list to save the result in
     * @param found the node where backtracking should start
     */
    private void reconstructPath(List<Node> path, Node found) {
        if(found != null && !Double.isNaN(found.getCostToReach(true))){
            path.add(found);
            Node current = found.getPredecessor(true);

            while(current != null){
                path.add(current);
                current = current.getPredecessor(true);
            }

            Collections.reverse(path);
            current = found.getPredecessor(false);
            while(current != null){
                path.add(current);
                current = current.getPredecessor(false);
            }
        }
    }
}
