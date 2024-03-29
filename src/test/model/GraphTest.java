package test.model;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Paths;

import org.json.JSONObject;
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

    // REQUIRES: label >= 0, label must exist
    // EFFECT: bypasses repetitive try-catch blocks
    public void safeAddVertex(int label) {
        try {
            g.addVertex(label);
        } catch (GraphException ge) {
            fail("should not reach this point");
        }
        assertEquals(g.withLabel(label).getLabel(), label);
    }

    // REQUIRES: beginLabel >= 0, endLabel >=0, either labels must exist
    // EFFECT: bypasses repetitive try-catch blocks
    public void safeAddEdge(int beginLabel, int endLabel) {
        try {
            g.addEdge(beginLabel, endLabel);
        } catch (GraphException ge) {
            fail("should not reach this point");
        }
    }

    @Test
    public void testCreateGraph() {
        assertTrue(g.getVertices().isEmpty());
        assertTrue(g.getEdges().isEmpty());
    }

    @Test
    public void compositeTest() {
        // same as ./data/graph_sample.json; to check if Jacoco is on ketamine
        // update: Jacoco IS on ketamine
        for (int i = 1; i <= 10; i++) {
            safeAddVertex(i);
        }

        safeAddEdge(1, 5);
        safeAddEdge(2, 7);
        safeAddEdge(6, 4);
        safeAddEdge(3, 3);
        safeAddEdge(4, 5);
        safeAddEdge(9, 1);
        safeAddEdge(10, 3);
        safeAddEdge(7, 5);

        assertEquals(g.getVertices().size(), 10);
        assertEquals(g.getEdges().size(), 8);
    }

    @Test
    public void jsonTest() {
        try {
            g = new Graph(Paths.get("./data/graph_samplesmall.json"));
        } catch (IOException ioe) {
            fail("should not reach this point");
        }
        JSONObject json = g.toJson();
        assertEquals(json.toString(),
                "{\"numOfEdges\":1,\"numOfVertices\":2,\"vertices\":[{\"label\":1},"
                        + "{\"label\":2}],\"edges\":[{\"endLabel\":2,\"beginLabel\":1}]}");
    }

    @Test
    public void testCreateGraphFromFile() {
        try {
            g = new Graph(Paths.get("./data/graph_sample.json"));
        } catch (IOException ioe) {
            fail("should not reach this point");
        }

        assertEquals(g.getVertices().size(), 10);
        assertEquals(g.getEdges().size(), 8);
    }

    @Test
    public void testCreateGraphFromNonexistantFile() {
        try {
            g = new Graph(Paths.get("./data/graph_not_like_i_am_real.json"));
        } catch (IOException ioe) {
            assertEquals(ioe.getMessage(), ".\\data\\graph_not_like_i_am_real.json");
        }
    }

    @Test
    public void testCreateGraphFromCorruptedFile() {
        try {
            g = new Graph(Paths.get("./data/graph_corrupted.json"));
        } catch (Exception ioe) {
            assertEquals(ioe.getMessage(), "Graph file is corrupted or probably deleted.");
        }
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

        try {
            g.addEdge(10, 20);
            fail("should not reach this point");
        } catch (GraphException ge) {
            assertEquals(ge.getMessage(), "No vertex with this label currently exists in the graph.");
        }

        safeAddVertex(20);
        safeAddEdge(10, 20);

        assertEquals(g.withLabel(10).getAdjacent().get(0).getfirstVertex().getLabel(), 10);
        assertEquals(g.withLabel(10).getAdjacent().get(0).getsecondVertex().getLabel(), 20);
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
            assertFalse(g.removeEdge(1, 5));
        } catch (GraphException ge) {
            fail("should not reach this point");
        }

        try {
            assertTrue(g.removeEdge(1, 4));
        } catch (GraphException ge) {
            fail("should not reach this point");
        }
    }
}