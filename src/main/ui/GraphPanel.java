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

// A panel containg a Graph and load/save buttons.
public class GraphPanel extends JPanel {
    private static final String DATA_DIR = "./data/";
    private Graph currentGraph;
    private Vertex lastActive; // to add edges
    private JButton load;
    private JButton save;

    // EFFECTS: create a GraphPanel with a white background. The initial Graph is
    // empty.
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

    // MODIFIES: this
    // EFFECTS: draw the Graph onto the canvas. First draw edges, then vertices
    // (so that edge lines doesn't overlap with the Vertex circles).
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (Edge e : currentGraph.getEdges()) {
            e.draw(g);
        }
        for (Vertex v : currentGraph.getVertices()) {
            v.draw(g);
        }
    }

    // MODIFIES: this
    // EFFECTS: handle an event where the mouse was clicked
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
                    Vertex otherVertex = currentGraph.vertexAtPos(e.getPoint());
                    if (currentGraph.hasEdge(lastActive, otherVertex)) {
                        currentGraph.removeEdge(lastActive, otherVertex);
                    } else if (lastActive != otherVertex) {
                        currentGraph.addEdge(lastActive, otherVertex);
                    }
                    clearActive();
                } else {
                    saveSelected(e.getPoint());
                }
            }
        } else if (e.getClickCount() == 2) {
            removeVertex(e.getPoint());
            clearActive(); // hard hitting bug
        }
    }

    // MODIFIES: this
    // EFFECTS: handle an event where the mouse was dragged
    // if the position is occupied by a vertex, move said vertex to a new location
    // (provided that that position is not yet occupied by any other vertices)
    public void handleMouseDragged(MouseEvent e) {
        Vertex current = vertexAtPos(e.getPoint());
        if (current != null && numOfVertexAtPos(e.getPoint()) < 2 && current.getActive()) {
            current.setPos(e.getPoint());
        }
    }

    // EFFECTS: returns the first Vertex found contaning pos, or null if there is
    // none.
    public Vertex vertexAtPos(Point pos) {
        return currentGraph.vertexAtPos(pos);
    }

    // EFFECTS: returns the number of Vertex found contaning pos.
    public int numOfVertexAtPos(Point pos) {
        return currentGraph.numOfVertexAtPos(pos);
    }

    // MODIFIES: this
    // EFFECTS: add a Vertex onto the canvas.
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

    // MODIFIES: this
    // EFFECTS: remove the first Vertex found containg pos.
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

    // MODIFIES: this
    // EFFECTS: mark the first Vertex found occupying pos as active.
    public void saveSelected(Point pos) {
        lastActive = currentGraph.vertexAtPos(pos);
        if (lastActive != null) {
            lastActive.setSelected(true);
        }
    }

    // MODIFIES: this
    // EFFECTS: deactive lastActive and set it to null.
    public void clearActive() {
        if (lastActive != null) {
            lastActive.setSelected(false);
        }
        lastActive = null;
    }

    public Vertex getLastActive() {
        return lastActive;
    }

    class LoadGraph implements ActionListener {
        // EFFECTS: Present the user with a file explorer. The file chosen will be loaded
        // onto the Graph.
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
        // MODIFIES: the file chosen by the user
        // EFFECTS: Present the user with a file explorer. Save the Graph at the location
        // chosen by the user.
        public void actionPerformed(ActionEvent e) {
            JFileChooser chooser = new JFileChooser(DATA_DIR);
            chooser.setFileFilter(new FileNameExtensionFilter("JSON files", "json"));
            if (chooser.showSaveDialog(GraphPanel.this) == JFileChooser.APPROVE_OPTION) {
                try {
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
