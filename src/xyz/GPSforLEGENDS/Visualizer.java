package xyz.GPSforLEGENDS;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Visualizer {

    /**
     * safes the grid as an image representation
     * @param grid, not null
     * @param file the PNG file to safe the image, not null
     */
    public static void safeNodeGridImageToFile(NodeGrid grid, File file){
        if(grid == null || file == null) throw new NullPointerException();
        BufferedImage gridImage = gridToImage(grid);

        try {
            ImageIO.write(gridImage,"png",file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * converts the grid to an BufferedImage
     * @param grid, not null
     * @return the grid as an BufferedImage
     */
    private static BufferedImage gridToImage(NodeGrid grid){
        if(grid == null) throw new NullPointerException();
        BufferedImage gridImage = new BufferedImage(grid.getWidth(),grid.getHeight(),BufferedImage.TYPE_INT_RGB);

        for(int height = 0; height < grid.getHeight(); height++){
            for(int width = 0; width < grid.getWidth(); width++){
                Node n = grid.getNode(width,height);
                Color color = Color.white;

                if(!n.isTraversable()) color = Color.BLACK;

                if(n.getStatus() == 1) color = Color.RED;

                if(n.getStatus() == 2) color = Color.BLUE;

                gridImage.setRGB(width,height,color.getRGB());
            }
        }

        return gridImage;
    }
}
