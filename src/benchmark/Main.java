package benchmark;

import graph.Graph;
import graph.GraphGenerator;
import mst.Kruskal;
import mst.MSTResult;

public class Main {
    public static void main(String[] args) {
        Graph s = new Graph(4, false);
        s.addEdge(0, 1, 1);
        s.addEdge(1, 2, 2);
        s.addEdge(2, 3, 3);
        s.addEdge(0, 3, 10);
        s.addEdge(0, 2, 4);
        show("small", Kruskal.run(s));

        Graph dc = new Graph(4, false);
        dc.addEdge(0, 1, 1);
        dc.addEdge(2, 3, 2);
        show("disconnected", Kruskal.run(dc));

        show("single", Kruskal.run(new Graph(1, false)));

        int[][] sizes = {{10, 30}, {100, 500}, {1000, 5000}};
        for (int[] z : sizes) {
            Graph g = GraphGenerator.connectedUndirected(z[0], z[1], 42);
            MSTResult r = Kruskal.run(g);
            System.out.println("V=" + z[0] + " E=" + z[1] + " weight=" + r.weight
                    + " edges=" + r.edges.size() + " connected=" + r.connected
                    + " examined=" + r.examined + " comparisons=" + r.comparisons
                    + " finds=" + r.finds + " unions=" + r.unions
                    + " time=" + r.timeNs / 1e6 + " ms");
        }
    }

    static void show(String name, MSTResult r) {
        System.out.println(name + ": weight=" + r.weight + " edges=" + r.edges.size()
                + " connected=" + r.connected);
    }
}