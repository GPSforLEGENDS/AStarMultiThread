package xyz.GPSforLEGENDS;

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
     * @return A list containing the Nodes ordered from start to end. Null if no path was found or the points are outside of the grid
     * @exception IllegalArgumentException if coordinates are outside of the grid or are not traversable
     */
    public List<Node> findPath(int startX, int startY, int endX, int endY){

        Node start = grid.getNode(startX,startY);
        Node end = grid.getNode(endX,endY);

        if(start == null || end == null) throw new IllegalArgumentException("The start or end coordinates are outside of the grid");

        if(!start.isTraversable() || !end.isTraversable()) throw new IllegalArgumentException("The start or end node is not traversable");



        return null;
    }

}
