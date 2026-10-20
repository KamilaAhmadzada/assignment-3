package benchmark;

import connectivity.TopoResult;
import connectivity.TopoSort;
import graph.Graph;
import graph.GraphGenerator;

public class Main {
    public static void main(String[] args) {
        Graph a = new Graph(5, true);
        a.addEdge(0, 1, 1);
        a.addEdge(0, 2, 1);
        a.addEdge(1, 3, 1);
        a.addEdge(2, 3, 1);
        a.addEdge(3, 4, 1);
        small("small DAG", a);

        Graph c = new Graph(4, true);
        c.addEdge(0, 1, 1);
        c.addEdge(1, 2, 1);
        c.addEdge(2, 0, 1);
        c.addEdge(2, 3, 1);
        small("small cycle", c);

        small("single", new Graph(1, true));

        int[][] sizes = {{10, 30}, {100, 500}, {1000, 5000}};
        for (int[] z : sizes) {
            Graph dag = GraphGenerator.directedAcyclic(z[0], z[1], 42);
            Graph cyc = GraphGenerator.directedRandom(z[0], z[1], 42);
            TopoResult d = TopoSort.run(dag);
            TopoResult y = TopoSort.run(cyc);
            System.out.println("V=" + z[0] + " E=" + z[1]
                    + " DAG: dag=" + d.dag + " valid=" + TopoSort.valid(dag, d.order)
                    + " pops=" + d.pops + " examined=" + d.examined
                    + " time=" + d.timeNs / 1e6 + " ms");
            System.out.println("   random directed: dag=" + y.dag + " pops=" + y.pops
                    + " examined=" + y.examined + " time=" + y.timeNs / 1e6 + " ms");
        }
    }

    static void small(String name, Graph g) {
        TopoResult r = TopoSort.run(g);
        System.out.println(name + ": dag=" + r.dag + " order=" + r.order
                + " valid=" + TopoSort.valid(g, r.order));
    }
}