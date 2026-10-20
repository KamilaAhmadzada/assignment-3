package mst;

import graph.Edge;

import java.util.ArrayList;
import java.util.List;

public class MSTResult {
    public final List<Edge> edges = new ArrayList<>();
    public long weight;
    public boolean connected;
    public long timeNs;
    public long examined;
    public long comparisons;
    public long finds;
    public long unions;
    public long pq;
}