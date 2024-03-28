# Personal Project 


## Description

This application will allow users to first construct a [graph](https://en.wikipedia.org/wiki/Graph_(discrete_mathematics)) by adding/removing vertices and edges, either by interacting with a GUI or through a command-line interface. It will then be able to perform an array of algorithms, with each step clearly demonstrated to the user (e.g. Dijkstra's path-finding algorithm, checking whether the graph is bipartite or a tree, ...).

This application is geared towards those having the first touches with graphs and related concepts; for example, CS 221 students. The idea first came to my mind because of my experience and affinity for competitive programming and DSA (data structures and algorithms) in general.

## User stories

- As a user, I would like to add/remove a vertex in the current graph.                                      (P3 mandatory)
- As a user, I would like to add/remove an edge connecting two vertices in the current graph.
- As a user, I would like to view a list of algorithms I can run on the current graph (and run them).
- As a user, I would like to view a list of graphs I created earlier (and load them to the current graph).  (P3 mandatory)
- As a user, I would like to save the current graph onto a .json file.                                      (P3 mandatory)
- As a user, I would like to view a list of vertices or edges currently existing in the graph.


## Instruction for Grader

- To add a vertex to the current graph, left-click on an empty point on the screen not occupied by any vertex.
- To remove a vertex from the current graph, double-click on said vertex.
- To add an edge to the current graph, left-click on its begin vertex THEN its end vertex.
- To save the graph click CTRL+S.
- To load the graph, first click CTRL+L then choose the desired file in the pop-up file explorer.

- Visual component: the graph itself (duh).