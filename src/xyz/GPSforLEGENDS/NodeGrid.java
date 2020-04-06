package xyz.GPSforLEGENDS;

import java.awt.image.BufferedImage;

public class NodeGrid {

    // x and y
    private Node[][] nodeGrid;

    /**
     * constructor
     * initialize the nodegrid and fills it with the nodes
     * all pixel != white are considered to be not traversable
     * @param image the image that is used to create the grid from. The dimension has to be atleast 2x2. Not null
     * @exception IllegalArgumentException if the height or width of the image are not atleast 2
     * @exception NullPointerException if the image is null
     */
    public NodeGrid(BufferedImage image){

        if(image == null) throw new NullPointerException("BufferedImage image cant be null");

        if(image.getWidth() < 2 || image.getHeight() < 2) throw new IllegalArgumentException("The Dimension of the image has to be atleast 2x2");

        nodeGrid = new Node[image.getWidth()][image.getHeight()];

        //loop to create the nodes
        for(int row = 0; row < image.getHeight(); row++){
            for(int column = 0; column < image.getWidth(); column++){
                //weiß == -1
                boolean traversable = (image.getRGB(column,row) == -1);
                Node node = new Node(traversable, column, row);
                nodeGrid[column][row] = node;
            }
        }

        setupNeighbours();
    }

    /**
     * function to setup the neighbours of all nodes in the grid
     */
    private void setupNeighbours(){
        //loop to set neighbours of the nodes
        //corners are treated outside of the loop
        for(int row = 0; row < getHeight(); row++){
            for(int column = 0; column < getWidth(); column++){

                //if top border && NOT corner
                if(row == 0 && (column != 0 && column < getWidth() - 1)){
                    Node n = getNode(column, row);

                    n.addNeighbour(getNode(column - 1, row));
                    n.addNeighbour(getNode(column + 1, row));

                    n.addNeighbour(getNode(column - 1, row + 1));
                    n.addNeighbour(getNode(column, row + 1));
                    n.addNeighbour(getNode(column + 1, row + 1));
                }

                //if bottom border && NOT CORNER
                else if(row == getHeight() - 1 && (column != 0 && column < getWidth() - 1)){
                    Node n = getNode(column, row);

                    n.addNeighbour(getNode(column - 1, row - 1));
                    n.addNeighbour(getNode(column, row - 1));
                    n.addNeighbour(getNode(column + 1, row - 1));

                    n.addNeighbour(getNode(column - 1, row));
                    n.addNeighbour(getNode(column + 1, row));
                }

                //if left border && NOT corner
                else if(column == 0 && (row != 0 && row < getHeight() - 1)){
                    Node n = getNode(column, row);

                    n.addNeighbour(getNode(column, row - 1));
                    n.addNeighbour(getNode(column + 1, row - 1));

                    n.addNeighbour(getNode(column + 1, row));

                    n.addNeighbour(getNode(column, row + 1));
                    n.addNeighbour(getNode(column + 1, row + 1));
                }

                //if rigth border && NOT corner
                else if(column == getWidth() - 1 && (row != 0 && row < getHeight() - 1)){
                    Node n = getNode(column, row);
                    n.addNeighbour(getNode(column, row - 1));
                    n.addNeighbour(getNode(column - 1, row - 1));

                    n.addNeighbour(getNode(column - 1, row));

                    n.addNeighbour(getNode(column, row + 1));
                    n.addNeighbour(getNode(column - 1, row + 1));
                }

                //all none border nodes
                else{
                    if(column != 0 && row != 0 && column != getWidth() - 1 && row != getHeight() - 1) {
                        Node n = getNode(column, row);

                        n.addNeighbour(getNode(column - 1, row - 1));
                        n.addNeighbour(getNode(column, row - 1));
                        n.addNeighbour(getNode(column + 1, row - 1));

                        n.addNeighbour(getNode(column - 1, row));
                        n.addNeighbour(getNode(column + 1, row));

                        n.addNeighbour(getNode(column - 1, row + 1));
                        n.addNeighbour(getNode(column, row + 1));
                        n.addNeighbour(getNode(column + 1, row + 1));
                    }
                }
            }
        }
        //setting neighbours for the corners

        //top left
        Node n = getNode(0,0);
        n.addNeighbour(getNode(1,0));
        n.addNeighbour(getNode(1,1));
        n.addNeighbour(getNode(0, 1));

        //top right
        n = getNode(getWidth() - 1,0);
        n.addNeighbour(getNode(getWidth() - 2,  0 ));
        n.addNeighbour(getNode(getWidth() - 2,  1 ));
        n.addNeighbour(getNode(getWidth() - 1,  1 ));

        //bottom left
        n = getNode(0, getHeight() - 1);
        n.addNeighbour(getNode(0, getHeight() - 2));
        n.addNeighbour(getNode(1, getHeight() - 2));
        n.addNeighbour(getNode(1, getHeight() - 1));

        //bottom right
        n = getNode(getWidth() - 1, getHeight() - 1);
        n.addNeighbour(getNode(getWidth() - 1,getHeight() - 2));
        n.addNeighbour(getNode(getWidth() - 2,getHeight() - 2));
        n.addNeighbour(getNode(getWidth() - 2,getHeight() - 1));
    }

    /**
     * returns the node on the grid
     * @param x x coordinate
     * @param y y coordinate
     * @return the Node with the corresponding position on the grid, null if the coordinates are outside of the grid
     */
    public Node getNode(int x, int y){

        if(x < 0 || x > nodeGrid.length || y < 0 || y > nodeGrid[0].length){
            return null;
        }

        return nodeGrid[x][y];
    }

    /**
     *
     * @return the width of the nodegrid
     */
    public int getWidth(){
        return nodeGrid.length;
    }

    /**
     * @return the length of the nodegrid
     */
    public int getHeight(){
        return nodeGrid[0].length;
    }


}
