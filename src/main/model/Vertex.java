package model;

import java.util.List;
import java.awt.*;
import org.json.JSONObject;
import persistence.Writable;
import java.util.ArrayList;

// Vertices in graph implementation. Uses an adjacent list which connects to edges and not other vertices directly.
// Instantiated vertices MUST have a POSITIVE label. Uninstantiated vertices have a label of -1.
public class Vertex implements Writable, Shape {
    private int label = -1;
    private List<Edge> adjacent;

    // these are GUI-only
    private static final int RADIUS = 40; // radius of vertex Circle.
    private static final Color IDLE_COLOR = Color.white;
    private static final Color ACTIVE_COLOR = Color.orange;
    private boolean isActive; // whether the Vertex is being HOVERED ON by a mouse
    private int x;
    private int y; // coordinates

    // REQUIRES: label >= 0
    // EFFECTS: creates a new vertex with said label and no adjacent vertices
    public Vertex(int label) {
        this.label = label;
        this.adjacent = new ArrayList<>();
    }

    // REQUIRES: label >= 0
    // EFFECTS: creates a new vertex with said label and no adjacent vertices
    public Vertex(int label, int x, int y) {
        this.label = label;
        this.x = x;
        this.y = y;
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

    // Originaly from SimpleDrawingPlayer
    // EFFECTS: draws this Shape on the SimpleDrawingPlayer, if the shape is
    // selected, Shape is filled in
    // else, Shape is unfilled (white)
    public void draw(Graphics g) {
        Color initialColor = g.getColor();
        drawGraphics(g);
        if (isActive) {
            g.setColor(ACTIVE_COLOR);
        } else {
            g.setColor(IDLE_COLOR);
        }
        fillGraphics(g);
        g.setColor(initialColor);
    }

    // EFFECTS: draws a circle of radius RADIUS representing the vertex
    private void drawGraphics(Graphics g) {
        g.drawOval(x, y, RADIUS, RADIUS);
        g.drawString(Integer.toString(getLabel()), x, y);
    }

    // EFFECTS: fills the circle above
    private void fillGraphics(Graphics g) {
        g.fillOval(x, y, RADIUS, RADIUS);
    }

    // Originaly from SimpleDrawingPlayer
    // EFFECTS: return true if the given Point (x,y) is contained within the bounds
    // of this Vertex
    public boolean contains(Point point) {
        int point_x = point.x;
        int point_y = point.y;

        // Pythagoras' theorem
        return (point_x - this.x) * (point_x - this.x) + (point_y - this.y) * (point_y - this.y) <= RADIUS * RADIUS;
    }

    public int getLabel() {
        return this.label;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getRadius() {
        return RADIUS;
    }

    public List<Edge> getAdjacent() {
        return this.adjacent;
    }
}
