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

// Undirected graph implementation, where for every edge A->B there is also an edge B->A.
public class Graph implements Writable {
    private List<Vertex> vertices;

    // EFFECTS: creates an empty graph
    public Graph() {
        vertices = new ArrayList<>();
    }

    // EFFECTS: creates a graph by reading from a file created by GraphWriter.
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

    // EFFECTS: returns whether the graph has a Vertex with this label number
    public boolean containsLabel(int label) {
        return vertexWithLabel(label) != null;
    }

    // EFFECTS: returns a list of edges currently in the graph.
    public List<Edge> getEdges() {
        List<Edge> edges = new ArrayList<>();
        for (Vertex v : vertices) {
            edges.addAll(v.getAdjacent());
        }
        return edges;
    }

    // EFFECTS: return true if there is an edge connecting firstVertex and
    // secondVertex
    public boolean hasEdge(Vertex firstVertex, Vertex secondVertex) {
        for (Edge e : firstVertex.getAdjacent()) {
            if (e.getFirstVertex() == firstVertex && e.getSecondVertex() == secondVertex) {
                return true;
            }
        }
        return false;
    }

    // EFFECTS: return true if there is an edge connecting any vertices with labels
    // firstLabel and secondLabel.
    public boolean hasEdge(int firstLabel, int secondLabel) {
        Vertex firstVertex = vertexWithLabel(firstLabel);
        Vertex secondVertex = vertexWithLabel(secondLabel);
        if (firstVertex == null || secondVertex == null) {
            return false;
        }
        return hasEdge(firstVertex, secondVertex);
    }

    // REQUIRES: Vertex doesn't exist before and its label is positive.
    // MODIFIES: this
    // EFFECTS: Add a Vertex to the graph.
    public void addVertex(Vertex v) throws GraphException {
        vertices.add(v);
        EventLog.getInstance().logEvent(new Event("added vertex " + v.getLabel()));
    }

    // EFFECTS: attempts to add an empty Vertex with a label to the graph.
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

    // REQUIRES: Vertex exists
    // MODIFIES: this
    // EFFECTS: removes a Vertex from the Graph.
    public void removeVertex(Vertex v) {
        for (Vertex other : vertices) {
            v.removeEdge(other);
            other.removeEdge(v);
        }
        // labelToVertex.remove(v.getLabel());
        vertices.remove(v);
        EventLog.getInstance().logEvent(new Event("removed vertex "
                + v.getLabel()));
    }

    // MODIFIES: this
    // EFFECTS: attempts to remove any Vertex having this label number from the
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
    // EFFECTS: attempts to add an edge connecting two vertices in the graph.
    public void addEdge(Vertex firstVertex, Vertex secondVertex) throws GraphException {
        Vertex begin = vertexWithLabel(firstVertex.getLabel());
        Vertex end = vertexWithLabel(secondVertex.getLabel());
        begin.addEdge(end);
        end.addEdge(begin);
        EventLog.getInstance().logEvent(new Event("added an edge connecting vertices "
                + firstVertex.getLabel() + " and " + secondVertex.getLabel()));
    }

    // MODIFIES: this
    // EFFECTS: attempts to add an edge connecting two labels in the graph.
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
    // EFFECTS: attempts to remove the first edge found connecting two vertices in
    // the graph.
    // If the either vertices hasn't already existed, throw
    // MissingLabelException.
    public boolean removeEdge(Vertex firstVertex, Vertex secondVertex) throws GraphException {
        if (firstVertex.removeEdge(secondVertex)
                && secondVertex.removeEdge(firstVertex)) {
            EventLog.getInstance().logEvent(new Event("removed the edge connecting vertices "
                    + firstVertex.getLabel() + " and " + secondVertex.getLabel()));
            return true;
        }
        return false;
    }

    // MODIFIES: this
    // EFFECTS: attempts to remove the first edge found connecting two label to the
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

    // EFFECTS: return a JSONArray consisting of JSONObject-s converted from items
    // of
    // getVertices()
    public JSONArray verticesToJson() {
        JSONArray jsonArray = new JSONArray();
        for (Vertex v : vertices) {
            jsonArray.put(v.toJson());
        }
        return jsonArray;
    }

    // EFFECTS: return a JSONArray consisting of JSONObject-s converted from items
    // of
    // getEdges()
    public JSONArray edgesToJson() {
        JSONArray jsonArray = new JSONArray();
        for (Edge e : getEdges()) {
            if (e.getFirstVertex().getLabel() < e.getSecondVertex().getLabel()) { // prevents duplicating
                jsonArray.put(e.toJson());
            }
        }
        return jsonArray;
    }

    // EFFECTS: Convert the current graph into a JSONObject.
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("numOfVertices", getVertices().size());
        json.put("numOfEdges", getEdges().size());
        json.put("vertices", verticesToJson());
        json.put("edges", edgesToJson());
        return json;
    }

    public List<Vertex> getVertices() {
        return vertices;
    }

    // EFFECTS: get the first positive number not currently being a label of any
    // Vertex. Used when adding new vertices in GUI.
    public int firstUnusedLabel() {
        int i = 1;
        while (true) {
            if (!containsLabel(i)) {
                return i;
            }
            i++;
        }
    }

    // EFFECTS: returns the first Vertex found contaning pos, or null if there is
    // none.
    public Vertex vertexAtPos(Point pos) {
        for (Vertex v : getVertices()) {
            if (v.contains(pos)) {
                return v;
            }
        }
        return null;
    }

    // EFFECTS: returns the number of Vertex found contaning pos.
    public int numOfVertexAtPos(Point pos) {
        int res = 0;
        for (Vertex v : getVertices()) {
            if (v.contains(pos)) {
                res++;
            }
        }
        return res;
    }
}
