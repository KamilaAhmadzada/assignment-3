package mst;

import dsu.DSU;
import graph.Edge;
import graph.Graph;

import java.util.ArrayList;
import java.util.List;

public class Kruskal {
    public static MSTResult run(Graph g) {
        long t0 = System.nanoTime();
        int n = g.getVertexCount();
        List<Edge> es = new ArrayList<>(g.getEdges());
        long[] cmp = {0};
        es.sort((a, b) -> {
            cmp[0]++;
            return Integer.compare(a.weight, b.weight);
        });

        DSU d = new DSU(n);
        MSTResult r = new MSTResult();
        for (Edge e : es) {
            r.examined++;
            if (d.union(e.from, e.to)) {
                r.edges.add(e);
                r.weight += e.weight;
                if (r.edges.size() == n - 1) break;
            }
        }
        r.connected = r.edges.size() == n - 1;
        r.comparisons = cmp[0];
        r.finds = d.finds;
        r.unions = d.unions;
        r.timeNs = System.nanoTime() - t0;
        return r;
    }
}