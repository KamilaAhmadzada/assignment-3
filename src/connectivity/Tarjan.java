package connectivity;

import graph.Edge;
import graph.Graph;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

public class Tarjan {
    private int[] idx;
    private int[] low;
    private boolean[] on;
    private Deque<Integer> st;
    private int t;
    private Graph g;
    private SCCResult r;

    public static SCCResult run(Graph g) {
        return new Tarjan().go(g);
    }

    private SCCResult go(Graph g) {
        long t0 = System.nanoTime();
        this.g = g;
        int n = g.getVertexCount();
        idx = new int[n];
        low = new int[n];
        on = new boolean[n];
        Arrays.fill(idx, -1);
        st = new ArrayDeque<>();
        r = new SCCResult();
        for (int u = 0; u < n; u++) {
            if (idx[u] == -1) dfs(u);
        }
        r.timeNs = System.nanoTime() - t0;
        return r;
    }

    private void dfs(int u) {
        idx[u] = low[u] = t++;
        st.push(u);
        on[u] = true;
        for (Edge e : g.getNeighbors(u)) {
            r.examined++;
            int w = e.to;
            if (idx[w] == -1) {
                dfs(w);
                low[u] = Math.min(low[u], low[w]);
            } else if (on[w]) {
                low[u] = Math.min(low[u], idx[w]);
            }
        }
        if (low[u] == idx[u]) {
            List<Integer> c = new ArrayList<>();
            int w;
            do {
                w = st.pop();
                on[w] = false;
                c.add(w);
            } while (w != u);
            r.comps.add(c);
        }
    }
}