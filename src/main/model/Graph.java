package model;

import java.util.List;
import java.util.ArrayList;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.*;

import model.exception.*;
import persistence.Writable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;

// Directed graph implementation. Assumes that all vertices has distinct labels, and all labels are non-negative.
public class Graph implements Writable {
    private List<Vertex> vertices;
    // private Map<Integer, Vertex> labelToVertex; // redundant since graph size is
    // relatively small

    // EFFECT: creates an empty graph
    public Graph() {
        vertices = new ArrayList<>();
        // labelToVertex = new HashMap<>();
    }

    // EFFECT: creates a graph by reading from a file in the format:
    // <number of vertices> <number of edges>
    // <label of first vertex>
    // ...
    // <label of last vertex>
    // <label of first vertex of first edge> <label of second vertex of first edge>
    // ...
    // <label of first vertex of last edge> <label of second vertex of last edge>
    public Graph(File file) throws IOException, FileNotFoundException {
        vertices = new ArrayList<>();
        // labelToVertex = new HashMap<>();
        try {
            JSONObject json = new JSONObject(Files.readString(Path.of(file.toString()), Charset.defaultCharset()));
            JSONArray jsonArray = json.getJSONArray("vertices");
            for (Object obj : jsonArray) {
                JSONObject v = (JSONObject) obj;
                int label = v.getInt("label");
                int x = v.getInt("x");
                int y = v.getInt("y");
                addVertex(new Vertex(label, x, y));
            }

            jsonArray = json.getJSONArray("edges");
            for (Object obj : jsonArray) {
                int firstVertex = ((JSONObject) obj).getInt("firstLabel");
                int secondVertex = ((JSONObject) obj).getInt("secondLabel");
                addEdge(firstVertex, secondVertex);
            }

        } catch (IOException | GraphException ge) {
            throw new IOException("Graph file is corrupted or probably deleted.");
        }
    }

    // EFFECTS: returns the Vertex having this label number; null otherwise
    public Vertex vertexWithLabel(int label) {
        for (Vertex v : getVertices()) {
            if (v.getLabel() == label) {
                return v;
            }
        }
        return null;
    }

    // EFFECTS: returns whether the graph has a vertex with this label number
    public boolean containsLabel(int label) {
        return vertexWithLabel(label) != null;
    }

    public Vertex vertexAtPos(Point pos) {
        for (Vertex v : getVertices()) {
            if (v.contains(pos)) {
                return v;
            }
        }
        return null;
    }

    public int numOfVertexAtPos(Point pos) {
        int res = 0;
        for (Vertex v : getVertices()) {
            if (v.contains(pos)) {
                res++;
            }
        }
        return res;
    }

    // EFFECTS: returns a list of edges currently in the graph.
    public List<Edge> getEdges() {
        List<Edge> edges = new ArrayList<>();
        for (Vertex v : vertices) {
            for (Edge e : v.getAdjacent()) {
                edges.add(e);
            }
        }
        return edges;
    }

    // EFFECT: return true if there is an edge connecting firstVertex and
    // secondVertex
    public boolean hasEdge(Vertex firstVertex, Vertex secondVertex) {
        for (Edge e : firstVertex.getAdjacent()) {
            if (e.getfirstVertex() == firstVertex && e.getsecondVertex() == secondVertex) {
                return true;
            }
        }
        return false;
    }

    // REQUIRES: Vertex doesn't exist before and its label is positive.
    // MODIFIES: this
    // EFFECT: Add a Vertex to the graph.
    public void addVertex(Vertex v) throws GraphException {
        vertices.add(v);
    }

    // EFFECT: attempts to add an empty vertex with a label to the graph.
    // If label is negative, throw NegativeLabelException().
    // If label already existed, throw UsedLabelException().
    public void addVertex(int label) throws GraphException {
        if (label <= 0) {
            throw new NegativeLabelException();
        }
        if (containsLabel(label)) {
            throw new UsedLabelException();
        }
        addVertex(new Vertex(label));
    }

    // REQUIRES: vertex exists
    // MODIFIES: this
    // EFFECT: removes a Vertex from the Graph.
    public void removeVertex(Vertex v) {
        for (Vertex other : vertices) {
            v.removeEdge(other);
            other.removeEdge(v);
        }
        // labelToVertex.remove(v.getLabel());
        vertices.remove(v);
    }

    // MODIFIES: this
    // EFFECT: attempts to remove any Vertex having this label number from the
    // Graph.
    // If label is negative, throws NegativeLabelException().
    // If Vertex doesn't exists, throws MissingLabelException().
    public void removeVertex(int label) throws GraphException {
        if (label <= 0) {
            throw new NegativeLabelException();
        }
        Vertex toBeRemoved = vertexWithLabel(label);
        if (toBeRemoved == null) {
            throw new MissingLabelException();
        }
        removeVertex(toBeRemoved);
    }

    // MODIFIES: this
    // EFFECT: attempts to add an edge connecting two vertices in the graph.
    public void addEdge(Vertex firstVertex, Vertex secondVertex) throws GraphException {

        Vertex begin = vertexWithLabel(firstVertex.getLabel());
        Vertex end = vertexWithLabel(secondVertex.getLabel());
        begin.addEdge(end);
        end.addEdge(begin);
    }

    // MODIFIES: this
    // EFFECT: attempts to add an edge connecting two labels in the graph.
    // If either labels is negative, throw NegativeLabelException.
    // If either labels hasn't already existed, throw MissingLabelException.
    public void addEdge(int firstLabel, int secondLabel) throws GraphException {
        if (firstLabel <= 0 || secondLabel <= 0) {
            throw new NegativeLabelException();
        }
        if (!containsLabel(firstLabel) || !containsLabel(secondLabel)) {
            throw new MissingLabelException();
        }
        Vertex firstVertex = vertexWithLabel(firstLabel);
        Vertex secondVertex = vertexWithLabel(secondLabel);
        addEdge(firstVertex, secondVertex);
    }

    // MODIFIES: this
    // EFFECT: attempts to remove the first edge found connecting two vertices in
    // the graph.
    // If the either vertices hasn't already existed, throw
    // MissingLabelException.
    public boolean removeEdge(Vertex firstVertex, Vertex secondVertex) throws GraphException {
        return firstVertex.removeEdge(secondVertex)
                && secondVertex.removeEdge(firstVertex);
    }

    // MODIFIES: this
    // EFFECT: attempts to remove the first edge found connecting two label to the
    // graph.
    // If either labels is negative, throw NegativeLabelException.
    public boolean removeEdge(int firstLabel, int secondLabel) throws GraphException {
        if (firstLabel <= 0 || secondLabel <= 0) {
            throw new NegativeLabelException();
        }
        if (!containsLabel(firstLabel) || !containsLabel(secondLabel)) {
            throw new MissingLabelException();
        }
        Vertex firstVertex = vertexWithLabel(firstLabel);
        Vertex secondVertex = vertexWithLabel(secondLabel);
        return removeEdge(firstVertex, secondVertex);
    }

    // EFFECT: return a JSONArray consisting of JSONObject-s converted from items of
    // getVertices()
    public JSONArray verticesToJson() {
        JSONArray jsonArray = new JSONArray();
        for (Vertex v : vertices) {
            jsonArray.put(v.toJson());
        }
        return jsonArray;
    }

    // EFFECT: return a JSONArray consisting of JSONObject-s converted from items of
    // getEdges()
    public JSONArray edgesToJson() {
        JSONArray jsonArray = new JSONArray();
        for (Edge e : getEdges()) {
            if (e.getfirstVertex().getLabel() < e.getsecondVertex().getLabel()) { // prevents duplicating
                jsonArray.put(e.toJson());
            }
        }
        return jsonArray;
    }

    // EFFECT: Convert the current graph into a JSONObject.
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("numOfVertices", getVertices().size());
        json.put("numOfEdges", getEdges().size());
        json.put("vertices", verticesToJson());
        json.put("edges", edgesToJson());
        return json;
    }

    // EFFECT: get the first positive number not currently being a label of any
    // vertex. Used when adding new vertices in GUI.
    public int firstUnusedLabel() {
        int i = 1;
        while (true) {
            if (!containsLabel(i)) {
                return i;
            }
            i++;
        }
    }

    public List<Vertex> getVertices() {
        return vertices;
    }
}
