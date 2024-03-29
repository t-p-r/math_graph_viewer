package ui;

import java.util.Scanner;

import model.*;
import model.exception.*;
import persistence.GraphWriter;

import java.time.LocalDateTime;
import java.io.*;
import java.nio.file.*;
import java.util.List;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;


// Terminal app for personal project.
public class GraphSimulatorTerminal {
    private static final int ADD_ACTION = 1;
    private static final int REMOVE_ACTION = 2;
    private static final int LIGHT_COMMAND_LENGTH = 2;
    private static final int HEAVY_COMMAND_LENGTH = 1;
    private static final String SAVE_DIR = "./data/";
    private Graph currentGraph;
    private Scanner getInput;
    private boolean stillRunning = true;

    // EFFECTS: run the graph simulator
    // Loosely based on TellerApp
    public GraphSimulatorTerminal() {
        init();
        System.out.print("\nWelcome to Graph Simulator! ");

        while (stillRunning) {
            System.out.println("Choose one of the options below:");
            displayOptions();
            System.out.println("");
            processCommand();
            System.out.println("");
        }
    }

    // MODIFIES: this
    // EFFECT: creates a new empty graph and instantiates getInput
    private void init() {
        currentGraph = new Graph();
        getInput = new Scanner(System.in);
    }

    // EFFECT: display available commands to the user
    private void displayOptions() {
        System.out.println(" \"av LABEL\" to add a vertex to the graph, or");
        System.out.println(" \"rv LABEL\" to remove a existing vertex from the graph, or");
        System.out.println(" \"ae LABEL1 LABEL2\" to add an edge to the graph, or");
        System.out.println(" \"re LABEL1 LABEL2\" to remove an existing edge from the graph, or");
        System.out.println(" \"vv\" to view the list of labels of current vertices, or");
        System.out.println(" \"ve\" to view the list of current edges, or");
        System.out.println(" \"A\" to run available algorithms on the graph, or");
        System.out.println(" \"R\" to reload the graph, or");
        System.out.println(" \"S\" to save the graph, or");
        System.out.println(" \"L\" to load a saved graph, or");
        System.out.println(" \"Q\" to quit.");
    }

    // EFFECT: process user input.
    private void processCommand() {
        String command = getInput.next();
        if (command.length() == LIGHT_COMMAND_LENGTH) {
            processLightCommand(command);
        } else if (command.length() == HEAVY_COMMAND_LENGTH) {
            processHeavyCommand(command);
        } else {
            System.out.println("Invalid command.");
        }
    }

    // EFFECT: process one half of user inputs
    private void processLightCommand(String command) {
        switch (command) {
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
            default:
                System.out.println("Invalid command.");
                break;
        }
    }

    // EFFECT: process the other half of user inputs
    private void processHeavyCommand(String command) {
        switch (command) {
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
                System.out.println("Invalid command.");
                break;
        }
    }

    // MODIFIES: this
    // EFFECT: attempts to add/remove a vertex. Outputs the first exception's
    // message, if any.
    private void tryVertex(int label, int action) {
        try {
            if (action == ADD_ACTION) {
                currentGraph.addVertex(new Vertex(label));
                System.out.println("Added a vertex with label " + Integer.toString(label) + ".");
            } else if (action == REMOVE_ACTION) {
                currentGraph.removeVertex(label);
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
            String message = "from vertex with label " + Integer.toString(label1) + " " + "to vertex with label "
                    + Integer.toString(label2) + ".";
            if (action == ADD_ACTION) {
                currentGraph.addEdge(label1, label2);
                System.out.println("Added an edge " + message);
            } else if (action == REMOVE_ACTION) {
                if (currentGraph.removeEdge(label1, label2)) {
                    System.out.println("Removed an edge " + message);
                } else {
                    System.out.println("The specified edge did not exist.");
                }
            }

        } catch (GraphException ge) {
            System.out.println(ge.getMessage());
        }
    }

    // EFFECT: list labels of vertices currently in the graph
    private void listVertices() {
        System.out.println("The current graph has vertices with labels:");
        List<Vertex> vertices = currentGraph.getVertices();
        for (Vertex v : vertices) {
            System.out.print(Integer.toString(v.getLabel()) + " ");
        }
        System.out.println("");
    }

    // EFFECT: list labels of edges currently in the graph
    private void listEdges() {
        System.out.println("The current graph has edges:");
        List<Edge> edges = currentGraph.getEdges();
        for (Edge e : edges) {
            System.out.print("From vertex with label ");
            System.out.print(Integer.toString(e.getfirstVertex().getLabel()) + " ");
            System.out.print("to vertex with label ");
            System.out.println(Integer.toString(e.getsecondVertex().getLabel()) + ".");
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
        if (getInput.next().equals("YOLO")) {
            reloadGraph();
            System.out.println("Operation succeded.");
        } else {
            System.out.println("Operation aborted.");
        }
    }

    // MODIFIES: this
    // EFFECT: resets the current graph to its beginning state
    private void reloadGraph() {
        currentGraph = new Graph();
    }

    // EFFECT: save the current Graph to the file "graph_yyyyMMdd_HHmmss.json"
    // (e.g. the file created on 15:45:17, Feb 14th 2024 is
    // "graph_20240214_154517.json")
    // Saved files have the form described in Graph::toJson().
    // Any IOException occured is unexpected and shall be outputed along with the
    // trace stack.
    public void saveGraph() {
        String saveFileName = SAVE_DIR + "graph_"
                + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        GraphWriter graphWriter = new GraphWriter(saveFileName + ".json");
        try {
            graphWriter.open();
            graphWriter.write(currentGraph);
            graphWriter.close();
        } catch (IOException ioe) {
            System.out.println("Unexpected file error.");
            ioe.printStackTrace();
        }
    }

    // MODIFIES: this
    // EFFECT: present the user with a list of saved graph files that can be chosen
    // to loaded.
    // Load the one chosen by the user; abort otherwise.
    // If a GraphException occurs, output the message.
    // Any IOException occured is unexpected and shall be outputed along with the
    // trace stack.
    public void loadGraph() {
        List<String> fileList = getSavedGraphFiles();
        System.out.println(Integer.toString(fileList.size()) + " save files found.");

        if (fileList != null && fileList.size() > 0) {
            try {
                System.out.println("Type the corresponding index number (1 - " + Integer.toString(fileList.size())
                        + ") to load them; type ANY other number to abort the operation:");
                for (int i = 1; i <= fileList.size(); i++) {
                    System.out.println(Integer.toString(i) + ": " + fileList.get(i - 1));
                }

                int index = getInput.nextInt();
                if (1 <= index && index <= fileList.size()) {
                    currentGraph = new Graph(Paths.get("./data/" + fileList.get(index - 1)));
                    System.out.println("Loaded graph saved in file " + fileList.get(index - 1) + ".");
                } else {
                    System.out.println("Operation aborted.");
                }

            } catch (IOException ioe) {
                System.out.println("Unexpected file error. The file may have been corrupted or deleted.");
                ioe.printStackTrace();
            }
        }
    }

    // EFFECT: get a list of saved graph files (i.e. those ending in ".gssf")
    // Doesn't check for corruption signs, which is the responsibility of functions
    // calling this.
    // Any IOException occured is unexpected and shall be outputed along with the
    // trace stack, in which case the function returns null.
    public List<String> getSavedGraphFiles() {
        try {
            List<String> fileList = Files.list(Paths.get("./data/")).filter(file -> !Files.isDirectory(file))
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .collect(Collectors.toList());
            fileList.removeIf(s -> !s.contains(".json")); // kinda looks like std::concept eh
            fileList.removeIf(s -> !s.contains("graph"));
            return fileList;
        } catch (IOException ioe) {
            System.out.println("Unexpected file error.");
            ioe.printStackTrace();
        }
        return null;
    }
}
