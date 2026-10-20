package mst;

import graph.Edge;
import graph.Graph;

import java.util.PriorityQueue;

public class Prim {
    public static MSTResult run(Graph g) {
        long t0 = System.nanoTime();
        int n = g.getVertexCount();
        boolean[] in = new boolean[n];
        long[] cmp = {0};
        PriorityQueue<Edge> pq = new PriorityQueue<>((a, b) -> {
            cmp[0]++;
            return Integer.compare(a.weight, b.weight);
        });
        MSTResult r = new MSTResult();
        int comps = 0;

        for (int s = 0; s < n; s++) {
            if (in[s]) continue;
            comps++;
            in[s] = true;
            push(g, s, in, pq, r);
            while (!pq.isEmpty()) {
                Edge e = pq.poll();
                r.pq++;
                if (in[e.to]) continue;
                in[e.to] = true;
                r.edges.add(e);
                r.weight += e.weight;
                push(g, e.to, in, pq, r);
            }
        }

        r.connected = comps == 1;
        r.comparisons = cmp[0];
        r.timeNs = System.nanoTime() - t0;
        return r;
    }

    private static void push(Graph g, int u, boolean[] in, PriorityQueue<Edge> pq, MSTResult r) {
        for (Edge e : g.getNeighbors(u)) {
            r.examined++;
            if (!in[e.to]) {
                pq.add(e);
                r.pq++;
            }
        }
    }
}