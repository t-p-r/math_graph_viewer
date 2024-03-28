package model;

import javax.swing.*;
import java.awt.*;

public class GraphPanel extends JPanel {
    private Graph currentGraph;
    private Vertex lastClickedOn; // to add edges

    public GraphPanel() {
        super();
        currentGraph = new Graph();
        setBackground(Color.white);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (Vertex v : currentGraph.getVertices()) {
            v.draw(g);
        }

        for (Edge e : currentGraph.getEdges()) {
            e.draw(g);
        }
    }

    public Vertex vertexAtPos(Point pos) {
        return currentGraph.vertexAtPos(pos);
    }

    public Boolean containsVertexAtPos(Point pos) {
        return vertexAtPos(pos) != null;
    }

    public void addVertex(Point pos) {
        try {
            if (!containsVertexAtPos(pos)) {
                currentGraph.addVertex(new Vertex(currentGraph.firstUnusedLabel(), (int) pos.getX(), (int) pos.getY()));
            }
        } catch (Exception e) {
            System.out.println("Unexpected error.");
            e.printStackTrace();
        }
    }

    public void addEdge(Vertex beginVertex, Point pos) {
        try {
            currentGraph.addEdge(beginVertex, vertexAtPos(pos));
        } catch (Exception e) {
            System.out.println("Unexpected error.");
            e.printStackTrace();
        }
    }

    public void removeVertex(Point pos) {
        try {
            if (containsVertexAtPos(pos)) {
                currentGraph.removeVertex(vertexAtPos(pos));
            }
        } catch (Exception e) {
            System.out.println("Unexpected error.");
            e.printStackTrace();
        }
    }

    public void saveClickedVertex(Point pos) {
        lastClickedOn = currentGraph.vertexAtPos(pos);
    }

    public void clearClickedVertex() {
        lastClickedOn = null;
    }

    public Vertex getLastClickedOn() {
        return lastClickedOn;
    }
}
