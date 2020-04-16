package xyz.GPSforLEGENDS;

import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AStarTest {

    @Test
    void findPathOnClearGrid() throws IOException {
        NodeGrid nodeGrid = new NodeGrid(ImageIO.read(NodeGridTest.class.getResourceAsStream("resources/empty.png")));

        AStar aStar = new AStar(nodeGrid);

        List<Node> path = aStar.findPath(0,0,99,99);

        assertEquals(100,path.size());
        for(int i = 1; i < 100; i++){
            assertEquals(nodeGrid.getNode(i-1, i-1), path.get(i-1));
        }
    }

    @Test
    void findPathOnOnePathGrid() throws IOException {
        NodeGrid nodeGrid = new NodeGrid(ImageIO.read(NodeGridTest.class.getResourceAsStream("resources/onePath.png")));

        AStar aStar = new AStar(nodeGrid);

        List<Node> path = aStar.findPath(0,0,19,19);

        assertEquals(38,path.size());

        int width = 0;
        int height = 0;

        for(width = 0; width < 19; width++){
            assertEquals(nodeGrid.getNode(width,height), path.get(width));
        }

        for(height = 1; height < 20; height++){
            assertEquals(nodeGrid.getNode(width,height), path.get(18 + height));
        }
    }

    @Test
    void findPathOnMidLineGrid() throws IOException {
        NodeGrid nodeGrid = new NodeGrid(ImageIO.read(NodeGridTest.class.getResourceAsStream("resources/midLine.png")));

        AStar aStar = new AStar(nodeGrid);

        List<Node> path = aStar.findPath(0,0,19,19);

        assertEquals(38,path.size());

        int width = 0;
        int height = 0;

        if(path.get(1).getX() == 1) {
            for (width = 0; width < 19; width++) {
                assertEquals(nodeGrid.getNode(width, height), path.get(width));
            }

            for (height = 1; height < 20; height++) {
                assertEquals(nodeGrid.getNode(width, height), path.get(18 + height));
            }
        }
        else{
            for (height = 0; height < 19; height++) {
                assertEquals(nodeGrid.getNode(width, height), path.get(height));
            }

            for (width = 1; width < 20; width++) {
                assertEquals(nodeGrid.getNode(width, height), path.get(18 + width));
            }
        }
    }

    @Test
    void findPathOnPrisonGrid() throws IOException {
        NodeGrid nodeGrid = new NodeGrid(ImageIO.read(NodeGridTest.class.getResourceAsStream("resources/prison.png")));

        AStar aStar = new AStar(nodeGrid);

        List<Node> path = aStar.findPath(0,0,19,19);

        assertTrue(path.isEmpty());
    }

    @Test
    void findPathMultiThreadOnClearGrid() throws IOException {
        NodeGrid nodeGrid = new NodeGrid(ImageIO.read(NodeGridTest.class.getResourceAsStream("resources/empty.png")));

        AStar aStar = new AStar(nodeGrid, true);

        List<Node> path = aStar.findPath(0,0,99,99);

        assertEquals(100,path.size());
        for(int i = 1; i < 100; i++){
            assertEquals(nodeGrid.getNode(i-1, i-1), path.get(i-1));
        }
    }

    @Test
    void findPathMultiThreadOnOnePathGrid() throws IOException {
        NodeGrid nodeGrid = new NodeGrid(ImageIO.read(NodeGridTest.class.getResourceAsStream("resources/onePath.png")));

        AStar aStar = new AStar(nodeGrid, true);

        List<Node> path = aStar.findPath(0,0,19,19);

        assertEquals(38,path.size());

        int width = 0;
        int height = 0;

        for(width = 0; width < 19; width++){
            assertEquals(nodeGrid.getNode(width,height), path.get(width));
        }

        for(height = 1; height < 20; height++){
            assertEquals(nodeGrid.getNode(width,height), path.get(18 + height));
        }
    }

    @Test
    void findPathMultiThreadOnMidLineGrid() throws IOException {
        NodeGrid nodeGrid = new NodeGrid(ImageIO.read(NodeGridTest.class.getResourceAsStream("resources/midLine.png")));

        AStar aStar = new AStar(nodeGrid, true);

        List<Node> path = aStar.findPath(0,0,19,19);

        assertEquals(38,path.size());

        int width = 0;
        int height = 0;

        if(path.get(1).getX() == 1) {
            for (width = 0; width < 19; width++) {
                assertEquals(nodeGrid.getNode(width, height), path.get(width));
            }

            for (height = 1; height < 20; height++) {
                assertEquals(nodeGrid.getNode(width, height), path.get(18 + height));
            }
        }
        else{
            for (height = 0; height < 19; height++) {
                assertEquals(nodeGrid.getNode(width, height), path.get(height));
            }

            for (width = 1; width < 20; width++) {
                assertEquals(nodeGrid.getNode(width, height), path.get(18 + width));
            }
        }
    }

    @Test
    void findPathMultiThreadOnPrisonGrid() throws IOException {
        NodeGrid nodeGrid = new NodeGrid(ImageIO.read(NodeGridTest.class.getResourceAsStream("resources/prison.png")));

        AStar aStar = new AStar(nodeGrid, true);

        List<Node> path = aStar.findPath(0,0,19,19);

        assertTrue(path.isEmpty());
    }
}