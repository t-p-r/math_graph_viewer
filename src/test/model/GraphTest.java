package test.model;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.io.File;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import model.Graph;
import model.exception.GraphException;

class GraphTest {
    private Graph g;

    @BeforeEach
    public void createGraph() {
        g = new Graph();
    }

    // REQUIRES: label< 0, label doesn't exist yet
    // EFFECT: bypasses repetitive try-catch blocks when the tester knows that the
    // label to add WILL be added
    public void safeAddVertex(int label) {
        try {
            g.addVertex(label);
        } catch (GraphException ge) {
            fail("should not reach this point");
        }
        assertEquals(g.withLabel(label).getLabel(), label);
    }

    @Test
    public void testCreateGraph() {
        assertTrue(g.getVertices().isEmpty());
        assertTrue(g.getEdges().isEmpty());
        assertEquals(g.getSize(), 0);
    }

    @Test
    public void testCreateGraphFromFile() {
        try {
            g = new Graph(new File("sample_graph.gssf"));
        } catch (IOException ioe) {
            fail("should not reach this point");
        } catch (GraphException ge) {
            fail("should not reach this point");
        }

        assertEquals(g.getVertices().size(),3);
        assertEquals(g.getEdges().size(),5);
        assertEquals(g.getSize(),3);
    }

    @Test
    public void testAddNegativeVertex() {
        try {
            g.addVertex(-10);
            fail("should not reach this point");
        } catch (GraphException ge) {
            assertEquals(ge.getMessage(), "Label number is negative.");
        }
    }

    @Test
    public void testAddUsedVertex() {
        safeAddVertex(69420);

        try {
            g.addVertex(69420);
            fail("should not reach this point");
        } catch (GraphException ge) {
            assertEquals(ge.getMessage(), "Label number has already existed in the graph.");
        }

        g.withLabel(69420).setLabel(1);
        try {
            g.addVertex(69420);
            fail("should not reach this point");
        } catch (GraphException ge) {
            assertEquals(ge.getMessage(), "Label number has already existed in the graph.");
        }
    }

    @Test
    public void RemoveAddNegativeVertex() {
        try {
            g.removeVertex(-10);
            fail("should not reach this point");
        } catch (GraphException ge) {
            assertEquals(ge.getMessage(), "Label number is negative.");
        }
    }

    @Test
    public void removeVertexTwice() {
        safeAddVertex(69420);

        try {
            g.removeVertex(69420);
        } catch (GraphException ge) {
            fail("should not reach this point");
        }

        try {
            g.removeVertex(69420);
            fail("should not reach this point");
        } catch (GraphException ge) {
            assertEquals(ge.getMessage(), "No vertex with this label currently exists in the graph.");
        }
    }

    @Test
    public void addEdges() {
        try {
            g.addEdge(-10, 10);
            fail("should not reach this point");
        } catch (GraphException ge) {
            assertEquals(ge.getMessage(), "Label number is negative.");
        }

        try {
            g.addEdge(10, -10);
            fail("should not reach this point");
        } catch (GraphException ge) {
            assertEquals(ge.getMessage(), "Label number is negative.");
        }

        try {
            g.addEdge(10, 20);
            fail("should not reach this point");
        } catch (GraphException ge) {
            assertEquals(ge.getMessage(), "No vertex with this label currently exists in the graph.");
        }

        safeAddVertex(10);
        safeAddVertex(20);

        try {
            g.addEdge(10, 20);
        } catch (GraphException ge) {
            fail("should not reach this point");
        }

        assertEquals(g.withLabel(10).getAdjacent().get(0).getBeginVertex().getLabel(), 10);
        assertEquals(g.withLabel(10).getAdjacent().get(0).getEndVertex().getLabel(), 20);
    }

    @Test
    public void removeEdges() {
        safeAddVertex(1);
        safeAddVertex(2);
        safeAddVertex(3);
        safeAddVertex(4);

        try {
            g.addEdge(1, 2);
        } catch (GraphException ge) {
            fail("should not reach this point");
        }

        try {
            g.addEdge(1, 3);
        } catch (GraphException ge) {
            fail("should not reach this point");
        }

        try {
            g.addEdge(1, 4);
        } catch (GraphException ge) {
            fail("should not reach this point");
        }

        assertEquals(g.withLabel(1).getAdjacent().size(), 3);
        try {
            g.removeEdge(-10, 10);
            fail("should not reach this point");
        } catch (GraphException ge) {
            assertEquals(ge.getMessage(), "Label number is negative.");
        }

        try {
            g.removeEdge(10, -10);
            fail("should not reach this point");
        } catch (GraphException ge) {
            assertEquals(ge.getMessage(), "Label number is negative.");
        }

        try {
            g.removeEdge(10, 20);
            fail("should not reach this point");
        } catch (GraphException ge) {
            assertEquals(ge.getMessage(), "No vertex with this label currently exists in the graph.");
        }

        try {
            g.removeEdge(1, 2);
        } catch (GraphException ge) {
            fail("should not reach this point");
        }
    }
}