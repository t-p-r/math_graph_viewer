package model;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import model.exception.*;

// Directed graph implementation. Assumes that all vertices has distinct labels, and all labels are non-negative.

public class Graph {
    private List<Vertex> vertices;
    private Map<Integer, Vertex> labelToVertex;

    // EFFECT: creates an empty graph
    public Graph() {
        vertices = new ArrayList<>();
        labelToVertex = new HashMap<>();
    }

    // EFFECT: creates a graph by reading from a file in the format:
    // <number of vertices> <number of edges>
    // <label of first vertex>
    // ...
    // <label of last vertex>
    // <label of first vertex of first edge> <label of second vertex of first edge>
    // ...
    // <label of first vertex of last edge> <label of second vertex of last edge>
    public Graph(File f) throws IOException, FileNotFoundException {
        vertices = new ArrayList<>();
        labelToVertex = new HashMap<>();
        Scanner getInput = new Scanner(f);

        try {
            int n = getInput.nextInt(); // number of vertices
            int m = getInput.nextInt(); // number of edges

            for (int i = 1; i <= n; i++) {
                addVertex(getInput.nextInt());
            }

            for (int i = 1; i <= m; i++) {
                addEdge(getInput.nextInt(), getInput.nextInt());
            }

        } catch (RuntimeException | GraphException ge) {
            getInput.close();
            throw new IOException("Graph file is corrupted or probably deleted.");
        }

        getInput.close();
    }

    // // EFFECT: creates a graph with a predefined size. Label vertices according
    // to
    // // styles defined in the LabelScheme class.
    // public Graph(int size, int labelScheme) {
    // vertices = new ArrayList<Vertex>(size);

    // if (labelScheme == LabelScheme.zeroBasedIndex) {
    // for (int i = 0; i < size; i++) {
    // vertices.get(i).setLabel(i);
    // }
    // } else if (labelScheme == LabelScheme.oneBasedIndex) {
    // for (int i = 1; i <= size; i++) {
    // vertices.get(i - 1).setLabel(i);
    // }
    // }
    // }

    public int getSize() {
        return vertices.size();
    }

    public Vertex withLabel(int label) {
        return labelToVertex.get(label);
    }

    public boolean containsLabel(int label) {
        return withLabel(label) != null;
    }

    public List<Vertex> getVertices() {
        return vertices;
    }

    public List<Edge> getEdges() {
        List<Edge> edges = new ArrayList<>();
        for (Vertex v : vertices) {
            for (Edge e : v.getAdjacent()) {
                edges.add(e);
            }
        }
        return edges;
    }

    // MODIFIES: this
    // EFFECT: attempts to add an empty vertex with a label to the graph.
    // If label is negative, throw NegativeLabelException.
    // If label already existed, throw UsedLabelException.
    public void addVertex(int label) throws GraphException {
        if (label < 0) {
            throw new NegativeLabelException();
        }
        if (containsLabel(label)) {
            throw new UsedLabelException();
        }

        Vertex toBeAdded = new Vertex(label);
        vertices.add(toBeAdded);
        labelToVertex.put(label, toBeAdded);
    }

    // MODIFIES: this
    // EFFECT: attempts to add an empty vertex with a label to the graph.
    // If label is negative, throw NegativeLabelException.
    // If label doesn't exist, throw MissingLabelException
    public void removeVertex(int label) throws GraphException {
        if (label < 0) {
            throw new NegativeLabelException();
        }
        Vertex toBeRemoved = withLabel(label);
        if (toBeRemoved == null) {
            throw new MissingLabelException();
        }
        vertices.remove(toBeRemoved);
        labelToVertex.remove(label);
    }

    // MODIFIES: this
    // EFFECT: attempts to add an edge connecting two label to the graph.
    // If either labels is negative, throw NegativeLabelException.
    // If either labels hasn't already existed, throw MissingLabelException.
    public void addEdge(int beginLabel, int endLabel) throws GraphException {
        if (beginLabel < 0 || endLabel < 0) {
            throw new NegativeLabelException();
        }
        if (!containsLabel(beginLabel) || !containsLabel(endLabel)) {
            throw new MissingLabelException();
        }

        Vertex begin = withLabel(beginLabel);
        Vertex end = withLabel(endLabel);
        begin.addEdge(end);
    }

    // MODIFIES: this
    // EFFECT: attempts to add an edge connecting two label to the graph.
    // If either labels is negative, throw NegativeLabelException.
    // If the label of the begin vertex hasn't already existed, throw
    // MissingLabelException.
    public boolean removeEdge(int beginLabel, int endLabel) throws GraphException {
        if (beginLabel < 0 || endLabel < 0) {
            throw new NegativeLabelException();
        }
        if (!containsLabel(beginLabel)) {
            throw new MissingLabelException();
        }

        Vertex begin = withLabel(beginLabel);
        Vertex end = withLabel(endLabel);
        return begin.removeEdge(end);
    }
}
