package xyz.GPSforLEGENDS;

import java.util.ArrayList;
import java.util.List;

public class Node {

    private boolean traversable;

    private int x;
    private int y;

    private Node succcessor;

    private int status;

    private List<Node> neighbours;

    public Node(boolean traversable, int x, int y){
        this.traversable = traversable;
        this.x = x;
        this.y = y;
        status = 0;
        //a pixel can be surrounded by a maximum of 8 others
        neighbours = new ArrayList<>(8);
    }

    public boolean isTraversable() {
        return traversable;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Node getSucccessor(){
        return succcessor;
    }

    public void setSucccessor(Node n){
        this.succcessor = n;
    }

    /**
     * getter for the status
     * 0 = not explored
     * 1 = explored from start point
     * 2 = explored from endpoint
     * @return
     */
    public synchronized int getStatus() {
        return status;
    }

    /**
     * setter for the status
     * 0 = not explored
     * 1 = explored from start point
     * 2 = explored from endpoint
     * @return
     */
    public synchronized void setStatus(int status) {
        this.status = status;
    }

    public List<Node> getNeighbours() {
        return this.neighbours;
    }

    public void setNeighbours(List<Node> neighbours) {
        this.neighbours = neighbours;
    }

    public void addNeighbour(Node node){
        neighbours.add(node);
    }

    public void removeNeighbour(Node node){
        neighbours.remove(node);
    }
}
