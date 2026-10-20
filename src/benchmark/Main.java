package benchmark;

import graph.Edge;
import graph.Graph;
import graph.GraphGenerator;
import shortestpath.Dijkstra;
import shortestpath.DijkstraResult;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        Graph a = new Graph(7, true);
        a.addEdge(0, 1, 7);
        a.addEdge(0, 2, 9);
        a.addEdge(0, 5, 14);
        a.addEdge(1, 2, 10);
        a.addEdge(1, 3, 15);
        a.addEdge(2, 3, 11);
        a.addEdge(2, 5, 2);
        a.addEdge(3, 4, 6);
        a.addEdge(5, 4, 9);
        DijkstraResult r = Dijkstra.run(a, 0);
        for (int v = 0; v < 7; v++) {
            if (r.dist[v] == DijkstraResult.INF) System.out.println("v=" + v + " unreachable");
            else System.out.println("v=" + v + " dist=" + r.dist[v] + " path=" + r.path(v));
        }

        Graph neg = new Graph(3, true);
        neg.addEdge(0, 1, 2);
        neg.addEdge(0, 2, 5);
        neg.addEdge(2, 1, -10);
        try {
            Dijkstra.run(neg, 0);
            System.out.println("negative edge rejected: false");
        } catch (IllegalArgumentException ex) {
            System.out.println("negative edge rejected: true");
        }

        System.out.println("single: dist=" + Dijkstra.run(new Graph(1, true), 0).dist[0]);

        int[][] sizes = {{10, 30}, {100, 500}, {1000, 5000}};
        for (int[] z : sizes) {
            System.out.println("V=" + z[0] + " E=" + z[1]);
            line("undirected", GraphGenerator.connectedUndirected(z[0], z[1], 42));
            line("directed", GraphGenerator.directedRandom(z[0], z[1], 42));
        }
    }

    static void line(String name, Graph g) {
        DijkstraResult r = Dijkstra.run(g, 0);
        int reach = 0;
        for (long x : r.dist) if (x != DijkstraResult.INF) reach++;
        boolean ok = Arrays.equals(r.dist, bf(g, 0));
        System.out.println("   " + name + ": reachable=" + reach + " examined=" + r.examined
                + " relaxed=" + r.relaxed + " pq=" + r.pq
                + " time=" + r.timeNs / 1e6 + " ms matches=" + ok);
    }

    static long[] bf(Graph g, int s) {
        int n = g.getVertexCount();
        long[] d = new long[n];
        Arrays.fill(d, DijkstraResult.INF);
        d[s] = 0;
        for (int i = 0; i < n - 1; i++) {
            boolean ch = false;
            for (int u = 0; u < n; u++) {
                if (d[u] == DijkstraResult.INF) continue;
                for (Edge e : g.getNeighbors(u)) {
                    if (d[u] + e.weight < d[e.to]) {
                        d[e.to] = d[u] + e.weight;
                        ch = true;
                    }
                }
            }
            if (!ch) break;
        }
        return d;
    }
}