package model;

import java.util.List;

import org.json.JSONObject;

import persistence.Writable;

import java.util.ArrayList;

// Vertices in graph implementation. Uses an adjacent list which connects to edges and not other vertices directly.
// Instantiated vertices MUST have a non-zero label. Uninstantiated vertices have a label of -1.
public class Vertex implements Writable {
    private int label = -1;
    private List<Edge> adjacent;

    // REQUIRES: label >= 0
    // EFFECTS: creates a new vertex with said label and no adjacent vertices
    public Vertex(int label) {
        this.label = label;
        this.adjacent = new ArrayList<>();
    }

    // REQUIRES: label >= 0
    // MODIFIES: this
    // EFFECTS: sets the label for the vertex
    public void setLabel(int label) {
        this.label = label;
    }

    // MODIFIES: this
    // EFFECTS: adds a directed edge from the current vertex to <other>.
    public void addEdge(Vertex other) {
        adjacent.add(new Edge(this, other));
    }

    // // MODIFIES: this
    // // EFFECTS: adds a directed edge from the current vertex to <other> with a
    // // specified label
    // public void addEdge(Vertex other, int label) {
    // adjacent.add(new Edge(this, other, label));
    // }

    // MODIFIES: this
    // EFFECTS: Attempts to remove a directed edge from the current vertex to
    // <other>.
    // If there are many such edges, removes the first one found.
    // Returns whether an edge was succesfully removed.
    public boolean removeEdge(Vertex other) {
        for (Edge e : adjacent) {
            if (e.getEndVertex() == other) {
                adjacent.remove(e);
                return true;
            }
        }
        return false;
    }

    // EFFECTS: return a JSON object in the form {"label", this.label}
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("label", this.label);
        return json;
    }

    public int getLabel() {
        return this.label;
    }

    public List<Edge> getAdjacent() {
        return this.adjacent;
    }
}
