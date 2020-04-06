package xyz.GPSforLEGENDS;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class NodeGridTest {

    private static NodeGrid nodeGrid;

    @BeforeAll
    public static void initialize() throws IOException {
        nodeGrid = new NodeGrid(ImageIO.read(NodeGridTest.class.getResourceAsStream("resources/basic.png")));
    }

    @Test
    public void testNodeGridCreation() throws Exception {
        assertEquals(271,nodeGrid.getWidth());
        assertEquals(192,nodeGrid.getHeight());

        //all nodes initialized
        for(int row = 0; row < nodeGrid.getHeight(); row++){
            for(int column = 0; column < nodeGrid.getWidth(); column++){
                Node n = nodeGrid.getNode(column,row);

                assertNotNull(n);
            }
        }
    }

    @Test
    public void testNodeTraversable(){
        assertTrue(nodeGrid.getNode(0,0).isTraversable());
        assertTrue(nodeGrid.getNode(270,191).isTraversable());

        assertFalse(nodeGrid.getNode(100,100).isTraversable());
        assertFalse(nodeGrid.getNode(200,90).isTraversable());
        assertTrue(nodeGrid.getNode(200,100).isTraversable());
    }

    @Test
    public void testNodeNeighbours(){
        //top left (corner) test
        Node n = nodeGrid.getNode(0,0);

        Node topLeft, top, topRight, left, right, botLeft, bot, botRight;

        right = nodeGrid.getNode(1,0);
        bot = nodeGrid.getNode(0,1);
        botRight = nodeGrid.getNode(1,1);

        assertEquals(3,n.getNeighbours().size());
        assertTrue(n.getNeighbours().contains(right));
        assertTrue(n.getNeighbours().contains(bot));
        assertTrue(n.getNeighbours().contains(botRight));
        ////////////////////////////////////////////////////

        //top mid test
        n = nodeGrid.getNode(100,0);

        left = nodeGrid.getNode(99,0);
        right = nodeGrid.getNode(101,0);
        botLeft = nodeGrid.getNode(99,1);
        bot = nodeGrid.getNode(100,1);
        botRight = nodeGrid.getNode(101,1);

        assertEquals(5,n.getNeighbours().size());
        assertTrue(n.getNeighbours().contains(left));
        assertTrue(n.getNeighbours().contains(right));
        assertTrue(n.getNeighbours().contains(botLeft));
        assertTrue(n.getNeighbours().contains(bot));
        assertTrue(n.getNeighbours().contains(botRight));
        ////////////////////////////////////////////////////

        //somewhere in the mid test
        n = nodeGrid.getNode(100,100);

        topLeft = nodeGrid.getNode(99,99);
        top = nodeGrid.getNode(100,99);
        topRight = nodeGrid.getNode(101,99);
        left = nodeGrid.getNode(99,100);
        right = nodeGrid.getNode(101,100);
        botLeft = nodeGrid.getNode(99,101);
        bot = nodeGrid.getNode(100,101);
        botRight = nodeGrid.getNode(100,101);

        assertEquals(8,n.getNeighbours().size());
        assertTrue(n.getNeighbours().contains(topLeft));
        assertTrue(n.getNeighbours().contains(top));
        assertTrue(n.getNeighbours().contains(topRight));
        assertTrue(n.getNeighbours().contains(left));
        assertTrue(n.getNeighbours().contains(right));
        assertTrue(n.getNeighbours().contains(botLeft));
        assertTrue(n.getNeighbours().contains(bot));
        assertTrue(n.getNeighbours().contains(botRight));
        ////////////////////////////////////////////////////

        //bot mid test
        n = nodeGrid.getNode(100,191);

        left = nodeGrid.getNode(99,191);
        right = nodeGrid.getNode(101,191);
        topLeft = nodeGrid.getNode(99,190);
        top = nodeGrid.getNode(100,190);
        topRight = nodeGrid.getNode(101,190);

        assertEquals(5,n.getNeighbours().size());
        assertTrue(n.getNeighbours().contains(left));
        assertTrue(n.getNeighbours().contains(right));
        assertTrue(n.getNeighbours().contains(topLeft));
        assertTrue(n.getNeighbours().contains(top));
        assertTrue(n.getNeighbours().contains(topRight));

        ////////////////////////////////////////////////////

        //left mid test
        n = nodeGrid.getNode(0,100);

        top = nodeGrid.getNode(0,99);
        topRight = nodeGrid.getNode(1,99);
        right = nodeGrid.getNode(1,100);
        botRight = nodeGrid.getNode(1,101);
        bot = nodeGrid.getNode(0,101);

        assertEquals(5,n.getNeighbours().size());
        assertTrue(n.getNeighbours().contains(top));
        assertTrue(n.getNeighbours().contains(topRight));
        assertTrue(n.getNeighbours().contains(right));
        assertTrue(n.getNeighbours().contains(botRight));
        assertTrue(n.getNeighbours().contains(bot));

        ////////////////////////////////////////////////////

        //right mid test
        n = nodeGrid.getNode(270,100);

        top = nodeGrid.getNode(270,99);
        topLeft = nodeGrid.getNode(269,99);
        left = nodeGrid.getNode(269,100);
        botLeft = nodeGrid.getNode(269,101);
        bot = nodeGrid.getNode(270,101);

        assertEquals(5,n.getNeighbours().size());
        assertTrue(n.getNeighbours().contains(top));
        assertTrue(n.getNeighbours().contains(topLeft));
        assertTrue(n.getNeighbours().contains(left));
        assertTrue(n.getNeighbours().contains(botLeft));
        assertTrue(n.getNeighbours().contains(bot));
    }

}