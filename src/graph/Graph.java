package graph;

import java.util.ArrayList;
import java.util.List;

public class Graph {
    private final int vertexCount;
    private final boolean directed;
    private final List<List<Edge>> adjacencyList;
    private final List<Edge> edgeList; // each edge stored once, used by Kruskal

    public Graph(int vertexCount, boolean directed) {
        this.vertexCount = vertexCount;
        this.directed = directed;
        this.adjacencyList = new ArrayList<>();
        for (int i = 0; i < vertexCount; i++) {
            adjacencyList.add(new ArrayList<>());
        }
        this.edgeList = new ArrayList<>();
    }

    public void addEdge(int u, int v, int weight) {
        if (u < 0 || u >= vertexCount || v < 0 || v >= vertexCount) {
            throw new IllegalArgumentException("Vertex out of range: " + u + ", " + v);
        }
        Edge edge = new Edge(u, v, weight);
        edgeList.add(edge);
        adjacencyList.get(u).add(edge);
        if (!directed) {
            // undirected: the reverse direction is needed for traversal (Prim, Dijkstra)
            adjacencyList.get(v).add(new Edge(v, u, weight));
        }
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public int getEdgeCount() {
        return edgeList.size();
    }

    public boolean isDirected() {
        return directed;
    }

    public List<Edge> getNeighbors(int vertex) {
        return adjacencyList.get(vertex);
    }

    public List<Edge> getEdges() {
        return edgeList;
    }
}