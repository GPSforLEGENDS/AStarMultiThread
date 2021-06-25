package xyz.GPSforLEGENDS;

import java.util.ArrayList;
import java.util.List;

public class Node {

    private boolean traversable;

    private int x;
    private int y;
    private double[] costToReach = {Double.MAX_VALUE, Double.MAX_VALUE};

    private Node[] predecessor = new Node[2];

    private long status;

    private List<Node> neighbours;

    public Node(boolean traversable, int x, int y){
        this.traversable = traversable;
        this.x = x;
        this.y = y;
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

    public Node getPredecessor(boolean fromStart){
        return predecessor[fromStart ? 0 : 1];
    }

    public void setPredecessor(Node n, boolean fromStart){
        this.predecessor[fromStart ? 0 : 1] = n;
    }

    /**
     * getter for the status
     * 0 = not explored
     * 1 = explored from start point
     * 2 = explored from endpoint
     * @return
     */
    public long getStatus() {
        return status;
    }

    /**
     * setter for the status<br>
     * 0 = not explored<br>
     * 1 = explored from start point<br>
     * 2 = explored from endpoint<br>
     * @return
     */
    public void setStatus(long status) {
        this.status = status;
    }

    public List<Node> getNeighbours() {
        return this.neighbours;
    }

    public void setNeighbours(List<Node> neighbours) {
        this.neighbours = neighbours;
    }

    /**
     * adds the Node to the neighbour list, BUT only if the node is traversable!!!
     * @param node
     */
    public void addNeighbour(Node node){
        if(node.isTraversable()) neighbours.add(node);
    }

    public void removeNeighbour(Node node){
        neighbours.remove(node);
    }

    public void setCostToReach(double costToReach, boolean fromStart) {
        this.costToReach[fromStart ? 0 : 1] = costToReach;
    }

    public double getCostToReach(boolean fromStart) {
        return costToReach[fromStart ? 0 : 1];
    }

    @Override
    public boolean equals(Object o){

        if(o == this){
            return true;
        }

        if(!(o instanceof  Node)){
            return false;
        }

        Node n = (Node) o;

        return (n.getX() == x && n.getY() == y);
    }
}
