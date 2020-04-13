package xyz.GPSforLEGENDS;

import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
    }

    @Test
    void findPathOnMidLineGrid() throws IOException {
        NodeGrid nodeGrid = new NodeGrid(ImageIO.read(NodeGridTest.class.getResourceAsStream("resources/midLine.png")));

        AStar aStar = new AStar(nodeGrid);

        List<Node> path = aStar.findPath(0,0,19,19);

        assertEquals(38,path.size());
    }
}