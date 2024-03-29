package model;

import java.awt.*;

import org.json.JSONObject;

import persistence.Writable;

// Edges in graph implementation.
// Instantiated edges must have a non-zero label. Uninstantiated edges have a label of -1.
public class Edge implements Writable, Shape {
    private Vertex firstVertex;
    private Vertex secondVertex;
    private static final Color EDGE_COLOR = Color.green;
    // private int label = -1;
    // private int length = 0; // will lay dormant for now

    // EFFECTS: creates a new edge with a begin point/Vertex and an end point
    public Edge(Vertex beginPoint, Vertex endPoint) {
        this.firstVertex = beginPoint;
        this.secondVertex = endPoint;
    }

    public Vertex getfirstVertex() {
        return this.firstVertex;
    }

    public Vertex getsecondVertex() {
        return this.secondVertex;
    }

    // EFFECTS: return a JSON object in the form
    // {
    // "beginLabel": firstVertex.label,
    // "endLabel" : secondVertex.label
    // },
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("firstLabel", getfirstVertex().getLabel());
        json.put("secondLabel", getsecondVertex().getLabel());
        return json;
    }

    public void draw(Graphics g) {
        Color initialColor = g.getColor();
        g.setColor(EDGE_COLOR);
        g.drawLine(firstVertex.getxPos(), firstVertex.getyPos(),
                secondVertex.getxPos(), secondVertex.getyPos());
        g.setColor(initialColor);
        firstVertex.draw(g);
        secondVertex.draw(g); // redraw vertices to tidy up
    }
}
