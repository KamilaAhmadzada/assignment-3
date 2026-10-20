package shortestpath;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DijkstraResult {
    public static final long INF = Long.MAX_VALUE;
    public long[] dist;
    public int[] prev;
    public long timeNs;
    public long examined;
    public long relaxed;
    public long pq;

    public List<Integer> path(int t) {
        List<Integer> p = new ArrayList<>();
        if (dist[t] == INF) return p;
        for (int v = t; v != -1; v = prev[v]) p.add(v);
        Collections.reverse(p);
        return p;
    }
}