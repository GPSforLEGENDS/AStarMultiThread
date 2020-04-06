package xyz.GPSforLEGENDS;

public class PathWorker extends Thread{

    private NodeGrid grid;

    private int startX, startY, endX, endY;

    // Acts as a bridge to find where both finder met
    private Node from, to;

    public PathWorker(NodeGrid grid, int startX, int startY, int endX, int endY){
        this.grid = grid;
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
    }

    public void run(){

    }
}
