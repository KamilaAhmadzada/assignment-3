package benchmark;

import connectivity.SCCResult;
import connectivity.Tarjan;
import graph.Graph;
import graph.GraphGenerator;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        Graph a = new Graph(7, true);
        a.addEdge(0, 1, 1);
        a.addEdge(1, 2, 1);
        a.addEdge(2, 0, 1);
        a.addEdge(2, 3, 1);
        a.addEdge(3, 4, 1);
        a.addEdge(4, 5, 1);
        a.addEdge(5, 3, 1);
        small("small", a);

        small("single", new Graph(1, true));

        int[][] sizes = {{10, 30}, {100, 500}, {1000, 5000}};
        for (int[] z : sizes) {
            Graph dag = GraphGenerator.directedAcyclic(z[0], z[1], 42);
            Graph dir = GraphGenerator.directedRandom(z[0], z[1], 42);
            SCCResult d = Tarjan.run(dag);
            SCCResult y = Tarjan.run(dir);
            System.out.println("V=" + z[0] + " E=" + z[1]);
            System.out.println("   DAG: count=" + d.comps.size() + " largest=" + largest(d)
                    + " valid=" + ok(dag, d) + " examined=" + d.examined
                    + " time=" + d.timeNs / 1e6 + " ms");
            System.out.println("   random directed: count=" + y.comps.size() + " largest=" + largest(y)
                    + " valid=" + ok(dir, y) + " examined=" + y.examined
                    + " time=" + y.timeNs / 1e6 + " ms");
        }
    }

    static void small(String name, Graph g) {
        SCCResult r = Tarjan.run(g);
        System.out.println(name + ": count=" + r.comps.size() + " comps=" + r.comps
                + " valid=" + ok(g, r));
    }

    static int largest(SCCResult r) {
        int m = 0;
        for (List<Integer> c : r.comps) m = Math.max(m, c.size());
        return m;
    }

    static boolean ok(Graph g, SCCResult r) {
        boolean[] s = new boolean[g.getVertexCount()];
        int c = 0;
        for (List<Integer> k : r.comps) {
            for (int v : k) {
                if (s[v]) return false;
                s[v] = true;
                c++;
            }
        }
        return c == g.getVertexCount();
    }
}