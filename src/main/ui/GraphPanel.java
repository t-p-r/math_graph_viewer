package ui;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import model.Edge;
import model.Graph;
import model.Vertex;
import model.exception.GraphException;
import persistence.GraphWriter;

import java.awt.*;
import java.awt.event.*;

public class GraphPanel extends JPanel {
    private static final String DATA_DIR = "./data/";
    private Graph currentGraph;
    private Vertex lastActive; // to add edges
    private JButton load, save;

    public GraphPanel() {
        super();
        currentGraph = new Graph();
        load = new JButton("Load");
        save = new JButton("Save");
        setBackground(Color.white);
        add(load);
        add(save);
        load.addActionListener(new LoadGraph());
        save.addActionListener(new SaveGraph());
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
                    } else if (lastActive != otherVertex) {
                        currentGraph.addEdge(lastActive, otherVertex);
                    }
                    clearActive();
                } else {
                    saveActive(e.getPoint());
                }
            }
        } else if (e.getClickCount() == 2) {
            removeVertex(e.getPoint());
            clearActive(); // hard hitting bug
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

    class LoadGraph implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JFileChooser chooser = new JFileChooser(DATA_DIR);
            chooser.setFileFilter(new FileNameExtensionFilter("JSON files", "json"));
            if (chooser.showOpenDialog(GraphPanel.this) == JFileChooser.APPROVE_OPTION) {
                try {
                    currentGraph = new Graph(chooser.getSelectedFile());
                    repaint();
                } catch (Exception ioe) {
                    System.out.println(
                            "Unexpected error. The graph file may have been corrupted, deleted or moved elsewhere.");
                    ioe.printStackTrace();
                }
            }
        }
    }

    class SaveGraph implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JFileChooser chooser = new JFileChooser(DATA_DIR);
            chooser.setFileFilter(new FileNameExtensionFilter("JSON files", "json"));
            if (chooser.showSaveDialog(GraphPanel.this) == JFileChooser.APPROVE_OPTION) {
                try {
                    System.out.println(chooser.getSelectedFile().getCanonicalPath());
                    GraphWriter graphWriter = new GraphWriter(chooser.getSelectedFile().getCanonicalPath());
                    graphWriter.open();
                    graphWriter.write(currentGraph);
                    graphWriter.close();
                } catch (Exception ioe) {
                    System.out.println(
                            "Unexpected error. The graph file may have been corrupted, deleted or moved elsewhere.");
                    ioe.printStackTrace();
                }
            }
        }
    }
}
