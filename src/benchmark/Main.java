package benchmark;

import graph.Edge;
import graph.Graph;
import graph.GraphGenerator;

import java.util.ArrayDeque;
import java.util.Queue;

public class Main {
    public static void main(String[] args) {
        int[][] sizes = {{10, 30}, {100, 500}, {1000, 5000}};
        for (int[] s : sizes) {
            Graph a = GraphGenerator.connectedUndirected(s[0], s[1], 42);
            Graph b = GraphGenerator.connectedUndirected(s[0], s[1], 42);
            boolean same = a.getEdges().toString().equals(b.getEdges().toString());
            System.out.println("V=" + s[0] + " E=" + a.getEdgeCount()
                    + " connected=" + connected(a) + " reproducible=" + same);

            Graph dag = GraphGenerator.directedAcyclic(s[0], s[1], 42);
            Graph dir = GraphGenerator.directedRandom(s[0], s[1], 42);
            System.out.println("   DAG edges=" + dag.getEdgeCount()
                    + ", directed random edges=" + dir.getEdgeCount());
        }
    }

    static boolean connected(Graph g) {
        boolean[] seen = new boolean[g.getVertexCount()];
        Queue<Integer> q = new ArrayDeque<>();
        q.add(0);
        seen[0] = true;
        int c = 1;
        while (!q.isEmpty()) {
            int u = q.poll();
            for (Edge e : g.getNeighbors(u)) {
                if (!seen[e.to]) {
                    seen[e.to] = true;
                    c++;
                    q.add(e.to);
                }
            }
        }
        return c == g.getVertexCount();
    }
}