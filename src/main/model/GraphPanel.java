package model;

import javax.swing.*;

import model.exception.GraphException;

import java.awt.*;
import java.awt.event.*;

public class GraphPanel extends JPanel {
    private Graph currentGraph;
    private Vertex lastActive; // to add edges

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

    // EFFECT: handle an event where the mouse was clicked
    // if single-click AND the position is empty, create a new vertex at said
    // position.
    // if single-click AND a vertex is in that position:
    // - if vertex was previously clicked on in the previous mouse action, create an
    // edge going from that vertex to the current vertex.
    // if double-click, remove any vertex occupying the mouse's position
    public void handleMouseClicked(MouseEvent e) throws GraphException {
        if (e.getClickCount() == 1) {
            if (numOfVertexAtPos(e.getPoint()) == 0) {
                addVertex(e.getPoint());
                clearActive();
            } else { // select vertex, deselect vertex, toggle edge
                if (lastActive != null && numOfVertexAtPos(e.getPoint()) > 0) {
                    // System.out.println("here");
                    Vertex otherVertex = currentGraph.vertexAtPos(e.getPoint());
                    if (currentGraph.hasEdge(lastActive, otherVertex)) {
                        
                        currentGraph.removeEdge(lastActive, otherVertex);
                    } else {
                        currentGraph.addEdge(lastActive, otherVertex);
                    }
                    clearActive();
                } else {
                    saveActive(e.getPoint());
                }
            }
        } else if (e.getClickCount() == 2) {
            removeVertex(e.getPoint());
        }
    }

    // EFFECT: handle an event where the mouse was dragged
    // if the position is occupied by a vertex, move said vertex to a new location
    // (provided that that position is not yet occupied by any other vertices)
    public void handleMouseDragged(MouseEvent e) {
        Vertex current = vertexAtPos(e.getPoint());
        if (current != null && numOfVertexAtPos(e.getPoint()) < 2 && current.getActive()) {
            current.setPos(e.getPoint());
        }
    }

    public Vertex vertexAtPos(Point pos) {
        return currentGraph.vertexAtPos(pos);
    }

    public int numOfVertexAtPos(Point pos) {
        return currentGraph.numOfVertexAtPos(pos);
    }

    public boolean addVertex(Point pos) {
        try {
            if (numOfVertexAtPos(pos) == 0) {
                currentGraph.addVertex(new Vertex(currentGraph.firstUnusedLabel(), (int) pos.getX(), (int) pos.getY()));
                return true;
            }
            return false;
        } catch (Exception e) {
            System.out.println("Unexpected error.");
            e.printStackTrace();
            return false;
        }
    }

    // public boolean addEdge(Vertex firstVertex, Point pos) {
    //     try {
    //         if (vertexAtPos(pos) != firstVertex) {
    //             currentGraph.addEdge(firstVertex, vertexAtPos(pos));
    //             return true;
    //         }
    //         return false;
    //     } catch (Exception e) {
    //         System.out.println("Unexpected error.");
    //         e.printStackTrace();
    //         return false;
    //     }
    // }

    // public boolean removeEdge(Vertex firstVertex, Point pos) {
    //     try {
    //         if (vertexAtPos(pos) != firstVertex) {
    //             currentGraph.addEdge(firstVertex, vertexAtPos(pos));
    //             return true;
    //         }
    //         return false;
    //     } catch (Exception e) {
    //         System.out.println("Unexpected error.");
    //         e.printStackTrace();
    //         return false;
    //     }
    // }

    public void removeVertex(Point pos) {
        try {
            if (numOfVertexAtPos(pos) > 0) {
                currentGraph.removeVertex(vertexAtPos(pos));
            }
        } catch (Exception e) {
            System.out.println("Unexpected error.");
            e.printStackTrace();
        }
    }

    public void saveActive(Point pos) {
        lastActive = currentGraph.vertexAtPos(pos);
        if (lastActive != null) {
            lastActive.setActive(true);
        }
    }

    public void clearActive() {
        if (lastActive != null) {
            lastActive.setActive(false);
        }
        lastActive = null;
    }

    public Vertex getLastActive() {
        return lastActive;
    }
}
