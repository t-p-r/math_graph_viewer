package model;

import org.json.JSONObject;

// Edges in graph implementation.
// Instantiated edges must have a non-zero label. Uninstantiated edges have a label of -1.
public class Edge {
    private Vertex beginVertex;
    private Vertex endVertex;
    // private int label = -1;
    private int length = 0; // will lay dormant for now

    // EFFECTS: creates a new edge with a begin point/Vertex and an end point
    public Edge(Vertex beginPoint, Vertex endPoint) {
        this.beginVertex = beginPoint;
        this.endVertex = endPoint;
    }

    // // REQUIRES: label >= 0
    // // EFFECTS: creates a new edge with a begin point/Vertex and an end point,
    // and a
    // // label
    // public Edge(Vertex beginPoint, Vertex endPoint, int label) {
    // this.beginVertex = beginPoint;
    // this.endVertex = endPoint;
    // this.label = label;
    // }

    // // REQUIRES: label >= 0
    // // MODIFIES: this
    // // EFFECTS: sets the label for the edge
    // public void setLabel(int label) {
    // this.label = label;
    // }

    // public int getLabel() {
    // return this.label;
    // }

    public Vertex getBeginVertex() {
        return this.beginVertex;
    }

    public Vertex getEndVertex() {
        return this.endVertex;
    }

    // EFFECTS: return a JSON object in the form
    // {
    //      "beginLabel": beginVertex.label,
    //      "endLabel" : endVertex.label
    // },
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("beginLabel", getBeginVertex().getLabel());
        json.put("endLabel", getEndVertex().getLabel());
        return json;
    }
}
