package benchmark;

import graph.Graph;
import graph.GraphGenerator;
import mst.Kruskal;
import mst.MSTResult;
import mst.Prim;

public class Main {
    public static void main(String[] args) {
        Graph s = new Graph(4, false);
        s.addEdge(0, 1, 1);
        s.addEdge(1, 2, 2);
        s.addEdge(2, 3, 3);
        s.addEdge(0, 3, 10);
        s.addEdge(0, 2, 4);
        both("small", s);

        Graph dc = new Graph(4, false);
        dc.addEdge(0, 1, 1);
        dc.addEdge(2, 3, 2);
        both("disconnected", dc);

        both("single", new Graph(1, false));

        Graph eq = new Graph(4, false);
        eq.addEdge(0, 1, 1);
        eq.addEdge(1, 2, 1);
        eq.addEdge(2, 3, 1);
        eq.addEdge(3, 0, 1);
        both("equal weights", eq);

        int[][] sizes = {{10, 30}, {100, 500}, {1000, 5000}};
        for (int[] z : sizes) {
            Graph g = GraphGenerator.connectedUndirected(z[0], z[1], 42);
            MSTResult k = Kruskal.run(g);
            MSTResult p = Prim.run(g);
            System.out.println("V=" + z[0] + " E=" + z[1]
                    + " kruskal=" + k.weight + " prim=" + p.weight
                    + " same=" + (k.weight == p.weight));
            System.out.println("   kruskal: examined=" + k.examined + " cmp=" + k.comparisons
                    + " finds=" + k.finds + " unions=" + k.unions + " time=" + k.timeNs / 1e6 + " ms");
            System.out.println("   prim:    examined=" + p.examined + " cmp=" + p.comparisons
                    + " pq=" + p.pq + " time=" + p.timeNs / 1e6 + " ms");
        }
    }

    static void both(String name, Graph g) {
        MSTResult k = Kruskal.run(g);
        MSTResult p = Prim.run(g);
        System.out.println(name + ": kruskal=" + k.weight + " prim=" + p.weight
                + " same=" + (k.weight == p.weight) + " connected=" + p.connected);
    }
}