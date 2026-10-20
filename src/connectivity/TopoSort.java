package connectivity;

import graph.Edge;
import graph.Graph;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

public class TopoSort {
    public static TopoResult run(Graph g) {
        long t0 = System.nanoTime();
        int n = g.getVertexCount();
        int[] in = new int[n];
        TopoResult r = new TopoResult();
        for (int u = 0; u < n; u++) {
            for (Edge e : g.getNeighbors(u)) {
                in[e.to]++;
                r.examined++;
            }
        }
        Queue<Integer> q = new ArrayDeque<>();
        for (int u = 0; u < n; u++) {
            if (in[u] == 0) q.add(u);
        }
        while (!q.isEmpty()) {
            int u = q.poll();
            r.pops++;
            r.order.add(u);
            for (Edge e : g.getNeighbors(u)) {
                r.examined++;
                if (--in[e.to] == 0) q.add(e.to);
            }
        }
        r.dag = r.order.size() == n;
        if (!r.dag) r.order.clear();
        r.timeNs = System.nanoTime() - t0;
        return r;
    }

    public static boolean valid(Graph g, List<Integer> order) {
        int n = g.getVertexCount();
        if (order.size() != n) return false;
        int[] pos = new int[n];
        for (int i = 0; i < n; i++) pos[order.get(i)] = i;
        for (Edge e : g.getEdges()) {
            if (pos[e.from] >= pos[e.to]) return false;
        }
        return true;
    }
}