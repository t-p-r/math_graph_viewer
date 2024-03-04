package model;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import model.exception.*;

import org.json.JSONArray;
import org.json.JSONObject;

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
    public Graph(Path path) throws IOException, FileNotFoundException {
        vertices = new ArrayList<>();
        labelToVertex = new HashMap<>();
        try {
            JSONObject json = new JSONObject(Files.readString(path));
            JSONArray jsonArray = json.getJSONArray("vertices");
            for (Object obj : jsonArray) {
                addVertex(((JSONObject) obj).getInt("label"));
            }

            jsonArray = json.getJSONArray("edges");
            for (Object obj : jsonArray) {
                int beginVertex = ((JSONObject) obj).getInt("beginLabel");
                int endVertex = ((JSONObject) obj).getInt("endLabel");
                addEdge(beginVertex, endVertex);
            }

        } catch (RuntimeException | GraphException ge) {
            throw new IOException("Graph file is corrupted or probably deleted.");
        }
    }

    // // EFFECT: creates a graph with a predefined size. Label vertices according
    // // to
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

    // deprecated; use getVertices().size() directly instead.
    // public int getSize() {
    // return vertices.size();
    // }

    // EFFECTS: returns the Vertex having this label number; null otherwise
    public Vertex withLabel(int label) {
        return labelToVertex.get(label);
    }

    // EFFECTS: returns whether the graph has a vertex with this label number
    public boolean containsLabel(int label) {
        return withLabel(label) != null;
    }

    public List<Vertex> getVertices() {
        return vertices;
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

        for (Vertex other : vertices) {
            other.removeEdge(toBeRemoved);
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
    // EFFECT: attempts to remove the first edge found connecting two label to the
    // graph.
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

    // EFFECT: return a JSONArray consisting of JSONObject-s converted from items of
    // getVertices()
    public JSONArray verticesToJSON() {
        JSONArray jsonArray = new JSONArray();
        for (Vertex v : vertices) {
            jsonArray.put(v.toJSON());
        }
        return jsonArray;
    }

    // EFFECT: return a JSONArray consisting of JSONObject-s converted from items of
    // getEdges()
    public JSONArray edgesToJSON() {
        JSONArray jsonArray = new JSONArray();
        for (Edge e : getEdges()) {
            jsonArray.put(e.toJSON());
        }
        return jsonArray;
    }
    // EFFECT: Convert the current graph into a JSONObject with the form:
    // ```{
    // "numOfVertices": <n>,
    // "numofEdges": <m>,
    // "vertices": [
    // {
    // "label": <v_1>
    // },
    // ...,
    // {
    // "label": <v_n>
    // }
    // ],

    // "edges": [
    // {
    // "beginLabel": <u_1>,
    // "endLabel" : <v_1>
    // },
    // ...,
    // {
    // "beginLabel": <u_m>,
    // "endLabel" : <v_m>
    // }
    // ]
    // }```
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("numOfVertices", getVertices().size());
        json.put("numOfEdges", getEdges().size());
        json.put("vertices", verticesToJSON());
        json.put("edges", edgesToJSON());
        return json;
    }
}
