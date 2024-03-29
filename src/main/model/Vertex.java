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

    // MODIFIES: this
    // EFFECTS: Attempts to remove a directed edge from the current vertex to
    // <other>.
    // If there are many such edges, removes the first one found.
    // Returns whether an edge was succesfully removed.
    public boolean removeEdge(Vertex other) {
        for (Edge e : adjacent) {
            if (e.getSecondVertex() == other) {
                adjacent.remove(e);
                return true;
            }
        }
        return false;
    }

    // EFFECTS: return a JSON object. Keys are label, x, y
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("label", this.label);
        json.put("x", this.xpos);
        json.put("y", ypos);
        return json;
    }

    public int getLabel() {
        return this.label;
    }

    public List<Edge> getAdjacent() {
        return this.adjacent;
    }

    private static final int RADIUS = 40; // radius of vertex Circle.
    private static final Color IDLE_COLOR = Color.orange;
    private static final Color ACTIVE_COLOR = Color.red;
    private int xpos;
    private int ypos;
    private boolean isSelected; // whether the Vertex is being HOVERED ON by a mouse

    // REQUIRES: label >= 0
    // EFFECTS: creates a new vertex with said label and no adjacent vertices
    public Vertex(int label, int x, int y) {
        this.label = label;
        this.xpos = x; // offset by RADIUS/2
        this.ypos = y; // so that the circle is centered around the mouse when spawn
        this.adjacent = new ArrayList<>();
    }

    // Originaly from SimpleDrawingPlayer
    // MODIFIES: g
    // EFFECTS: draws this Shape on the SimpleDrawingPlayer, if the shape is
    // selected, Shape is filled in
    // else, Shape is unfilled (white)
    public void draw(Graphics g) {
        Color initialColor = g.getColor();
        g.drawOval(xpos - RADIUS / 2, ypos - RADIUS / 2, RADIUS, RADIUS);
        if (isSelected) {
            g.setColor(ACTIVE_COLOR);
        } else {
            g.setColor(IDLE_COLOR);
        }
        g.fillOval(xpos - RADIUS / 2, ypos - RADIUS / 2, RADIUS, RADIUS);
        g.setColor(initialColor);
        g.drawString(Integer.toString(getLabel()), xpos - 3, ypos + 4); // draw label
    }

    // Originaly from SimpleDrawingPlayer
    // EFFECTS: return true if the given Point (x,y) is contained within the circle
    // representing the Vertex
    public boolean contains(Point point) {
        // Pythagoras' theorem
        return (point.x - this.xpos) * (point.x - this.xpos)
                + (point.y - this.ypos) * (point.y - this.ypos) <= RADIUS * RADIUS;
    }

    // MODIFIES: this
    // EFFECTS: change the Vertex's status to Active; recoloring it to red.
    public void setSelected(boolean active) {
        isSelected = active;
    }

    // MODIFIES: this
    // EFFECTS: move the Vertex to a new location.
    public void setPos(Point point) {
        this.xpos = point.x;
        this.ypos = point.y;
    }

    public int getXpos() {
        return this.xpos;
    }

    public int getYpos() {
        return this.ypos;
    }

    public boolean getActive() {
        return isSelected;
    }
}
