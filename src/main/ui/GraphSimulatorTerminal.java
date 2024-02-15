package ui;

import java.util.Scanner;
import java.util.Set;

import model.*;
import model.exception.*;
import java.time.LocalDateTime;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

// Terminal app for personal project.
public class GraphSimulatorTerminal {
    private static final int ADD_ACTION = 1;
    private static final int REMOVE_ACTION = 2;
    private Graph g;
    private Scanner getInput;
    private boolean stillRunning = true;

    // EFFECTS: run the graph simulator
    // Loosely based on TellerApp
    public GraphSimulatorTerminal() {
        init();
        System.out.println("\n\nWelcome to Graph Simulator!");

        while (stillRunning) {
            System.out.println("\n\n");
            System.out.println("Choose one of the options below:");
            displayOptions();
            processCommand();
        }
    }

    // MODIFIES: this
    // EFFECT: creates a new empty graph and instantiates getInput
    private void init() {
        g = new Graph();
        getInput = new Scanner(System.in);
    }

    // EFFECT: display available commands to the user
    private void displayOptions() {
        System.out.println(" \"av LABEL\" to add a vertex to the graph, or");
        System.out.println(" \"rv LABEL\" to remove a existing vertex from the graph, or");
        System.out.println(" \"ae LABEL1 LABEL2\" to add an edge to the graph, or");
        System.out.println(" \"re LABEL1 LABEL2\" to remove an existing edge from the graph, or");
        System.out.println(" \"vv\" to see a list of labels of current vertices, or");
        System.out.println(" \"ve\" to see a list of current edges, or");
        System.out.println(" \"A\" to run available algorithms on the graph, or");
        System.out.println(" \"R\" to reload the graph, or");
        System.out.println(" \"S\" to save the graph, or");
        System.out.println(" \"L\" to load a saved graph, or");
        System.out.println(" \"Q\" to quit.");
    }

    // EFFECT: process user input.
    // Due to the sheer number of commands available, the 25-line limit is easily
    // breached.
    @SuppressWarnings("methodlength")
    private void processCommand() {
        switch (getInput.next()) {
            case "av":
                tryVertex(getInput.nextInt(), ADD_ACTION);
                break;

            case "rv":
                tryVertex(getInput.nextInt(), REMOVE_ACTION);
                break;

            case "ae":
                tryEdge(getInput.nextInt(), getInput.nextInt(), ADD_ACTION);
                break;

            case "re":
                tryEdge(getInput.nextInt(), getInput.nextInt(), REMOVE_ACTION);
                break;

            case "vv":
                listVertices();
                break;

            case "ve":
                listEdges();
                break;

            case "A":
                runAlgorithms();
                break;

            case "R":
                tryReloadGraph();
                break;

            case "S":
                saveGraph();
                break;

            case "L":
                loadGraph();
                break;

            case "Q":
                stillRunning = false;
                break;

            default:
                System.out.println("Action invalid.");
                break;
        }
    }

    // MODIFIES: this
    // EFFECT: attempts to add/remove a vertex. Outputs the first exception's
    // message, if any.
    private void tryVertex(int label, int action) {
        try {
            if (action == ADD_ACTION) {
                g.addVertex(label);
                System.out.println("Added a vertex with label " + Integer.toString(label) + ".");
            } else if (action == REMOVE_ACTION) {
                g.removeVertex(label);
                System.out.println("Removed a vertex with label " + Integer.toString(label) + ".");
            }
        } catch (GraphException ge) {
            System.out.println(ge.getMessage());
        }
    }

    // MODIFIES: this
    // EFFECT: attempts to add/remove an edge. Outputs the first exception's
    // message, if any.
    private void tryEdge(int label1, int label2, int action) {
        try {
            if (action == ADD_ACTION) {
                g.addEdge(label1, label2);
            } else if (action == REMOVE_ACTION) {
                g.removeEdge(label1, label2);
            }
        } catch (GraphException ge) {
            System.out.println(ge.getMessage());
        }
    }

    // EFFECT: list labels of vertices currently in the graph
    private void listVertices() {
        System.out.println("The current graph has vertices with labels:");
        List<Vertex> vertices = g.getVertices();
        for (Vertex v : vertices) {
            System.out.print(Integer.toString(v.getLabel()) + " ");
        }
        System.out.println("");
    }

    // EFFECT: list labels of edges currently in the graph
    private void listEdges() {
        System.out.println("The current graph has edges:");
        List<Edge> edges = g.getEdges();
        for (Edge e : edges) {
            System.out.print("From vertex with label ");
            System.out.print(Integer.toString(e.getBeginVertex().getLabel()) + " ");
            System.out.print("to vertex with label ");
            System.out.println(Integer.toString(e.getEndVertex().getLabel()) + ".");
        }
    }

    // EFFECT: presents a list of algorithms that can be run on the current graph.
    // Run the one chosen by the user.
    private void runAlgorithms() {
        System.out.println("Currently no algorithms are available.");
    }

    // MODIFIES: this
    // EFFECT: reloadGraph() only if the user types "YOLO".
    private void tryReloadGraph() {
        System.out.println("This action is irreversible. If you really intends to do this, type \"YOLO\" below:");
        if (getInput.next() == "YOLO") {
            reloadGraph();
            System.out.println("Graph reloaded.");
        }
    }

    // MODIFIES: this
    // EFFECT: resets the current graph to its beginning state
    private void reloadGraph() {
        g = new Graph();
    }

    // EFFECT: save the current Graph to <yyyyMMdd_HHmmss.gssf>.
    // Saved files have the form:
    // <number of vertices> <number of edges>
    // <label of first vertex>
    // ...
    // <label of last vertex>
    // <label of first vertex of first edge> <label of second vertex of first edge>
    // ...
    // <label of first vertex of last edge> <label of second vertex of last edge>
    // Any IOException occured is unexpected and shall be outputed along with the
    // trace stack.
    private void saveGraph() {
        String currentTimeFormatted = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        try {
            FileWriter saveFile = new FileWriter(currentTimeFormatted + ".gssf");
            List<Vertex> vertices = g.getVertices();
            List<Edge> edges = g.getEdges();

            saveFile.write(Integer.toString(vertices.size()) + " ");
            saveFile.write(Integer.toString(edges.size()) + "\n");

            for (Vertex v : vertices) {
                saveFile.write(Integer.toString(v.getLabel()) + "\n");
            }
            for (Edge e : edges) {
                saveFile.write(Integer.toString(e.getBeginVertex().getLabel()) + " ");
                saveFile.write(Integer.toString(e.getEndVertex().getLabel()) + "\n");
            }

            saveFile.close();
            System.out.println("Saved current graph to file " + currentTimeFormatted + ".gssf.");
        } catch (IOException ioe) {
            System.out.println("Unexpected file error.");
            ioe.printStackTrace();
        }
    }

    // MODIFIES: this
    // EFFECT: present the user with a list of saved graph files that can be chosen
    // to loaded.
    // If a GraphException occurs, output the message.
    // Any IOException occured is unexpected and shall be outputed along with the
    // trace stack.
    // Really can't be decomposed further than this.
    @SuppressWarnings("methodlength")
    public void loadGraph() {
        try {
            List<String> fileList = Files.list(Paths.get("")).filter(file -> !Files.isDirectory(file))
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .collect(Collectors.toList());
            fileList.removeIf(s -> !s.contains("gssf"));

            System.out.println(
                    "Type the corresponding index number (1 - " + Integer.toString(fileList.size())
                            + ") to load them.");
            for (int i = 1; i <= fileList.size(); i++) {
                System.out.println(Integer.toString(i) + ": " + fileList.get(i - 1));
            }

            int index = getInput.nextInt();
            if (1 <= index && index <= fileList.size()) {
                g = new Graph(new File(fileList.get(index - 1)));
                System.out.println("Loaded graph saved in file " + fileList.get(index - 1));
            } else {
                System.out.println("Invalid index number.");
            }

        } catch (GraphException ge) {
            System.out.print("An error has occured while loading the graph: ");
            System.out.println(ge.getMessage());
        } catch (IOException ioe) {
            System.out.println("Unexpected file error.");
            ioe.printStackTrace();
        }
    }
}
