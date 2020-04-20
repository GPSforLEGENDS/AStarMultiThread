package xyz.GPSforLEGENDS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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

            workerFromStart.start();
            workerFromEnd.start();

            Node found = null;

            //loop to wait for results
            found = getResultFromPathWorkers(workerFromStart, workerFromEnd);


            reconstructParallelPath(path, found);

        }
        //run in main thread
        else{
            //setting up the predecessors
            PathWorker worker = new PathWorker(grid,start,end,true, new AtomicBoolean(false));
            //just call run, since it is one thread anyways;
            worker.run();

            reconstructPath(path, end);

        }
        return path;
    }

    private Node getResultFromPathWorkers(PathWorker workerFromStart, PathWorker workerFromEnd) {
        Node found = null;
        while(true){
            if(!workerFromStart.isAlive()){
                found = workerFromStart.getFound();
                if(!workerFromEnd.isAlive() && workerFromEnd.getFound() == null && found == null) break;
                if(Double.isNaN(workerFromStart.getStartNode().getCostToReach(true))) break;
                if(found != null) break;
            }
            if(!workerFromEnd.isAlive()){
                found = workerFromEnd.getFound();
                if(!workerFromStart.isAlive() && workerFromStart.getFound() == null && found == null) break;
                if(Double.isNaN(workerFromEnd.getStartNode().getCostToReach(true))) break;
                if(found != null) break;
            }
        }

        return found;
    }

    /**
     * reconstructs the path by following the predecessors
     * @param path
     * @param found
     */
    private void reconstructParallelPath(List<Node> path, Node found) {
        if(found != null){
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

    /**
     * basic reconstruction of the path by following the chain of predecessors
     * @param path the list to save the result in
     * @param end
     */
    private void reconstructPath(List<Node> path, Node end) {
        //backtracking from end node
        if(end.getPredecessor(true) != null){
            path.add(end);
            Node current = end;
            do{
                current = current.getPredecessor(true);
                path.add(current);
            } while(current.getPredecessor(true) != null);
        }
        Collections.reverse(path);
    }

}
