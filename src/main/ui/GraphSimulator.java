package ui;

import javax.swing.*;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.awt.*;

import model.*;

public class GraphSimulator extends JFrame {
    public static final int WIDTH = 1280;
    public static final int HEIGHT = 960;

    private GraphPanel graphPanel;

    public GraphSimulator() {
        super("Graph Simulator");
        initializeGraphics(); // from SimpleDrawingPlayer
        initializeInteractions();
    }

    // Originaly from SimpleDrawingPlayer
    // MODIFIES: this
    // EFFECTS: draws the JFrame window where this GraphSimulator will operate, and
    // populates the tools to be used
    // to manipulate this drawing
    private void initializeGraphics() {
        setLayout(new BorderLayout());
        setMinimumSize(new Dimension(WIDTH, HEIGHT));
        addGraphPanel();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // EFFECTS: initialize a MouseListener
    private void initializeInteractions() {
        MouseListener ml = new MouseListener();
        addMouseListener(ml);
        addMouseMotionListener(ml);
    }

    // Originaly from SimpleDrawingPlayer
    // MODIFIES: this
    // EFFECTS: declares and instantiates a GraphPanel, and adds it to drawings
    private void addGraphPanel() {
        GraphPanel newGraphPanel = new GraphPanel();
        graphPanel = newGraphPanel;
        add(graphPanel, BorderLayout.CENTER);
        validate();
    }

    // EFFECT: handle an event where the mouse was clicked
    // if single-click AND the position is empty, create a new vertex at said
    // position.
    // if single-click AND a vertex is in that position:
    // - if vertex was previously clicked on in the previous mouse action, create an
    // edge going from that vertex to the current vertex.
    // if double-click, remove any vertex occupying the mouse's position
    public void handleMouseClicked(MouseEvent e) {
        if (e.getClickCount() == 1) {
            if (!graphPanel.containsVertexAtPos(e.getPoint())) {
                graphPanel.addVertex(e.getPoint());
            } else {
                Vertex lastClickedOn = graphPanel.getLastClickedOn();
                if (lastClickedOn != null && graphPanel.containsVertexAtPos(e.getPoint())) {
                    System.out.println("here");
                    graphPanel.addEdge(lastClickedOn, e.getPoint());
                    graphPanel.clearClickedVertex();
                } else {
                    graphPanel.saveClickedVertex(e.getPoint());
                }
            }
        } else if (e.getClickCount() == 2) {
            graphPanel.removeVertex(e.getPoint());
        }
        repaint();
    }

    public void handleMouseDragged(MouseEvent e) {
        System.out.println(e.getPoint());
    }

    // Originaly from SimpleDrawingPlayer
    private class MouseListener extends MouseAdapter {

        // EFFECTS: Forward mouse pressed event to the active tool
        // public void mousePressed(MouseEvent e) {
        // handleMousePressed(translateEvent(e));
        // }

        // // EFFECTS: Forward mouse released event to the active tool
        // public void mouseReleased(MouseEvent e) {
        // handleMouseReleased(translateEvent(e));
        // }

        // EFFECTS:Forward mouse clicked event to the active tool
        public void mouseClicked(MouseEvent e) {
            handleMouseClicked(translateEvent(e));
        }

        // EFFECTS:Forward mouse dragged event to the active tool
        public void mouseDragged(MouseEvent e) {
            handleMouseDragged(translateEvent(e));
        }

        // EFFECTS: translates the mouse event to current drawing's coordinate system
        private MouseEvent translateEvent(MouseEvent e) {
            return SwingUtilities.convertMouseEvent(e.getComponent(), e, graphPanel);
        }
    }
}
