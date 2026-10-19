package graph;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class GraphGenerator {
    static final int MAXW = 100;

    public static Graph connectedUndirected(int v, int e, long seed) {
        long max = (long) v * (v - 1) / 2;
        if (e < v - 1 || e > max) throw new IllegalArgumentException("E must be between V-1 and V(V-1)/2");
        Random r = new Random(seed);
        Graph g = new Graph(v, false);
        Set<Long> used = new HashSet<>();
        for (int i = 1; i < v; i++) add(g, used, i, r.nextInt(i), r, false);
        while (g.getEdgeCount() < e) {
            int a = r.nextInt(v), b = r.nextInt(v);
            if (a != b) add(g, used, a, b, r, false);
        }
        return g;
    }

    public static Graph directedAcyclic(int v, int e, long seed) {
        long max = (long) v * (v - 1) / 2;
        if (e > max) throw new IllegalArgumentException("E must be at most V(V-1)/2 for a DAG");
        Random r = new Random(seed);
        int[] p = new int[v];
        for (int i = 0; i < v; i++) p[i] = i;
        for (int i = v - 1; i > 0; i--) {
            int j = r.nextInt(i + 1);
            int t = p[i];
            p[i] = p[j];
            p[j] = t;
        }
        Graph g = new Graph(v, true);
        Set<Long> used = new HashSet<>();
        while (g.getEdgeCount() < e) {
            int a = r.nextInt(v), b = r.nextInt(v);
            if (a == b) continue;
            add(g, used, p[Math.min(a, b)], p[Math.max(a, b)], r, true);
        }
        return g;
    }

    public static Graph directedRandom(int v, int e, long seed) {
        long max = (long) v * (v - 1);
        if (e > max) throw new IllegalArgumentException("E must be at most V(V-1)");
        Random r = new Random(seed);
        Graph g = new Graph(v, true);
        Set<Long> used = new HashSet<>();
        while (g.getEdgeCount() < e) {
            int a = r.nextInt(v), b = r.nextInt(v);
            if (a != b) add(g, used, a, b, r, true);
        }
        return g;
    }

    private static void add(Graph g, Set<Long> used, int u, int w, Random r, boolean dir) {
        int n = g.getVertexCount();
        long key = dir ? (long) u * n + w : (long) Math.min(u, w) * n + Math.max(u, w);
        if (used.add(key)) g.addEdge(u, w, 1 + r.nextInt(MAXW));
    }
}