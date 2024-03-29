package model;

import java.awt.*;

import org.json.JSONObject;

import persistence.Writable;

// Edges in Graph.
public class Edge implements Writable, Shape {
    private Vertex firstVertex;
    private Vertex secondVertex;
    private static final Color EDGE_COLOR = Color.green;

    // EFFECTS: creates a new edge with a begin point/Vertex and an end point
    public Edge(Vertex beginPoint, Vertex endPoint) {
        this.firstVertex = beginPoint;
        this.secondVertex = endPoint;
    }

    public Vertex getFirstVertex() {
        return this.firstVertex;
    }

    public Vertex getSecondVertex() {
        return this.secondVertex;
    }

    // EFFECTS: return a JSON object in the form
    // {
    // "beginLabel": firstVertex.label,
    // "endLabel" : secondVertex.label
    // },
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("firstLabel", getFirstVertex().getLabel());
        json.put("secondLabel", getSecondVertex().getLabel());
        return json;
    }

    // MODIFIES: g
    // EFFECTS: draw a line connecting the two endpoints.
    public void draw(Graphics g) {
        Color initialColor = g.getColor();
        g.setColor(EDGE_COLOR);
        g.drawLine(firstVertex.getXpos(), firstVertex.getYpos(),
                secondVertex.getXpos(), secondVertex.getYpos());
        g.setColor(initialColor);
    }
}
