package shortestpath;

import graph.Edge;
import graph.Graph;

import java.util.Arrays;
import java.util.PriorityQueue;

public class Dijkstra {
    public static DijkstraResult run(Graph g, int src) {
        for (Edge e : g.getEdges()) {
            if (e.weight < 0) throw new IllegalArgumentException("negative weight: " + e);
        }
        long t0 = System.nanoTime();
        int n = g.getVertexCount();
        DijkstraResult r = new DijkstraResult();
        r.dist = new long[n];
        r.prev = new int[n];
        Arrays.fill(r.dist, DijkstraResult.INF);
        Arrays.fill(r.prev, -1);
        r.dist[src] = 0;
        PriorityQueue<long[]> q = new PriorityQueue<>((a, b) -> Long.compare(a[0], b[0]));
        q.add(new long[]{0, src});
        r.pq++;
        while (!q.isEmpty()) {
            long[] top = q.poll();
            r.pq++;
            int u = (int) top[1];
            if (top[0] > r.dist[u]) continue;
            for (Edge e : g.getNeighbors(u)) {
                r.examined++;
                long nd = r.dist[u] + e.weight;
                if (nd < r.dist[e.to]) {
                    r.dist[e.to] = nd;
                    r.prev[e.to] = u;
                    r.relaxed++;
                    q.add(new long[]{nd, e.to});
                    r.pq++;
                }
            }
        }
        r.timeNs = System.nanoTime() - t0;
        return r;
    }
}