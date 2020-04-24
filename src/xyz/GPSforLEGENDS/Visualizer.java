package xyz.GPSforLEGENDS;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class Visualizer {

    /**
     * safes the grid as an image representation
     * @param grid, not null
     * @param file the png file to safe the image, not null
     */
    public static void safeNodeGridImageToFile(NodeGrid grid, File file){
        if(grid == null || file == null) throw new NullPointerException();
        BufferedImage gridImage = gridToImage(grid, null);

        try {
            ImageIO.write(gridImage, "png", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * safes the grid as an image representation
     * @param grid, not null
     * @param file the png file to safe the image, not null
     * @param path the list containing the nodes from start to finish
     */
    public static void safeNodeGridImageToFile(NodeGrid grid, File file, List<Node> path){
        if(grid == null || file == null) throw new NullPointerException();
        BufferedImage gridImage = gridToImage(grid, path);

        try {
            ImageIO.write(gridImage, "png", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * converts the grid to an BufferedImage
     * @param grid, not null
     * @param path nullable
     * @return the grid as an BufferedImage
     */
    private static BufferedImage gridToImage(NodeGrid grid, List<Node> path){
        if(grid == null) throw new NullPointerException();
        BufferedImage gridImage = new BufferedImage(grid.getWidth(),grid.getHeight(),BufferedImage.TYPE_INT_RGB);
        Color color;

        for(int height = 0; height < grid.getHeight(); height++){
            for(int width = 0; width < grid.getWidth(); width++){
                Node n = grid.getNode(width,height);
                color = Color.white;

                if(!n.isTraversable()) color = Color.BLACK;

                if(n.getStatus() == 1) color = Color.RED;

                if(n.getStatus() == 2) color = Color.BLUE;

                gridImage.setRGB(width,height,color.getRGB());
            }
        }

        if (path != null) {
            color = Color.GREEN;
            for (Node pathNode : path) {
                gridImage.setRGB(pathNode.getX(),pathNode.getY(),color.getRGB());
            }
        }

        return gridImage;
    }
}
