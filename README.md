# Mathematical Graph Viewer


## What is this?

This application allows users to construct an undirect [graph](https://en.wikipedia.org/wiki/Graph_(discrete_mathematics)) by adding/removing vertices and edges, either by interacting with a GUI or through a command-line interface. Also supports saving to / loading from JSON files.

This application is geared towards those having the first touches with graphs and related concepts. The idea first came to my mind because of my experience and affinity for competitive programming and DSA (data structures and algorithms) in general.

## User stories

- As a user, I would like to add/remove a vertex in the current graph.                                      (P3 mandatory)
- As a user, I would like to add/remove an edge connecting two vertices in the current graph.
- As a user, I would like to view a list of algorithms I can run on the current graph (and run them).
- As a user, I would like to view a list of graphs I created earlier (and load them to the current graph).  (P3 mandatory)
- As a user, I would like to save the current graph onto a .json file.                                      (P3 mandatory)
- As a user, I would like to view a list of vertices or edges currently existing in the graph.


## User guide

- To add a vertex to the current graph, left-click on an empty point on the screen not occupied by any existing vertex.
- To remove a vertex from the current graph, double-click on said vertex.
- To select a vertex, left-click on it.
- To add an edge to the current graph, select one vertex then left-click on the other vertex.
- To move a vertex, first select it, then drag it around.
- To save the graph, first click on the "Save" button then type the name of the savefile (should end in .json).
- To load the graph, first click on the "Load" button then choose the desired file in the pop-up file explorer.
- Visual component: the graph itself (duh).

## Log example (for debugging)

```
Fri Apr 05 15:06:48 PDT 2024
added vertex 1
Fri Apr 05 15:06:50 PDT 2024
added vertex 2
Fri Apr 05 15:06:52 PDT 2024
added vertex 3
Fri Apr 05 15:06:53 PDT 2024
selected vertex 1
Fri Apr 05 15:06:54 PDT 2024
added an edge connecting vertices 1 and 2
Fri Apr 05 15:06:54 PDT 2024
selected vertex 1
Fri Apr 05 15:06:55 PDT 2024
selected vertex 2
Fri Apr 05 15:06:55 PDT 2024
added an edge connecting vertices 2 and 3
Fri Apr 05 15:06:55 PDT 2024
selected vertex 2
Fri Apr 05 15:06:57 PDT 2024
selected vertex 2
Fri Apr 05 15:06:58 PDT 2024
selected vertex 2
Fri Apr 05 15:06:58 PDT 2024
removed vertex 2
```

## Commentaries

Overall I'm quite satisfied with the design; however, if I had more time, I would absorb all the functionalities of the `Edge` class into the `Vertex` class. At first, I built this class to represent the connection between `Vertex` objects; over time, however, I realized that this can be represented in the `Vertex` class file directly, and without much hassle.

One thing I also tried to do is to create classes in the `ui` package that extends the `Vertex`, `Edge`, and `Graph` classes and containing only functions related to the GUI (i.e. the `draw` function), however, there were so much technical difficulties that I had to abandon this soon after.