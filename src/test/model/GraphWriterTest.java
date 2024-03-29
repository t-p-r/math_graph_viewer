package test.model;

import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.nio.file.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import model.Graph;
import persistence.GraphWriter;

public class GraphWriterTest {
    private Graph g;
    private GraphWriter gw;

    @BeforeEach
    public void createGraph() {
        gw = new GraphWriter("./data/small_graph_2.json");
    }

    @Test
    public void testSaveGraph() {
        try {
            g = new Graph(new File("./data/small_graph.json"));
            gw.open();
            gw.write(g);
            gw.close();
            assertEquals(Files.readString(Path.of("./data/small_graph_2.json")),
                    Files.readString(Path.of("./data/small_graph.json")));
        } catch (Exception e) {
            fail("");
        }
    }
}
