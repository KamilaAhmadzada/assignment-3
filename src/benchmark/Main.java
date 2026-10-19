package benchmark;

import graph.Edge;
import graph.Graph;

public class Main {
    public static void main(String[] args) {
        Graph g = new Graph(4, false);
        g.addEdge(0, 1, 5);
        g.addEdge(1, 2, 3);
        g.addEdge(2, 3, 7);

        System.out.println("Vertices: " + g.getVertexCount());
        System.out.println("Edges: " + g.getEdgeCount());
        for (Edge e : g.getNeighbors(1)) {
            System.out.println("Neighbor of 1: " + e);
        }
    }
}