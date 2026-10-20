import connectivity.SCCResult;
import connectivity.Tarjan;
import connectivity.TopoResult;
import connectivity.TopoSort;
import dsu.DSU;
import graph.Edge;
import graph.Graph;
import graph.GraphGenerator;
import mst.Kruskal;
import mst.MSTResult;
import mst.Prim;
import shortestpath.Dijkstra;
import shortestpath.DijkstraResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class AllTests {
    static int pass;
    static int fail;

    public static void main(String[] args) {
        dsu();
        mst();
        topo();
        scc();
        dijkstra();
        random();
        System.out.println(pass + " passed, " + fail + " failed");
        if (fail > 0) System.exit(1);
    }

    static void check(String name, boolean ok) {
        if (ok) {
            pass++;
            System.out.println("PASS: " + name);
        } else {
            fail++;
            System.out.println("FAIL: " + name);
        }
    }

    static void dsu() {
        DSU d = new DSU(6);
        check("dsu: starts with 6 sets", d.count() == 6);
        check("dsu: union merges two sets", d.union(0, 1));
        check("dsu: repeated union returns false", !d.union(1, 0));
        d.union(2, 3);
        d.union(1, 3);
        check("dsu: connected after chain of unions", d.connected(0, 2));
        check("dsu: separate elements stay separate", !d.connected(0, 5));
        check("dsu: set count after unions", d.count() == 3);
        DSU s = new DSU(1);
        check("dsu: single element", s.find(0) == 0 && s.count() == 1);
    }

    static void mst() {
        Graph a = new Graph(4, false);
        a.addEdge(0, 1, 1);
        a.addEdge(1, 2, 2);
        a.addEdge(2, 3, 3);
        a.addEdge(0, 3, 10);
        a.addEdge(0, 2, 4);
        MSTResult k = Kruskal.run(a);
        MSTResult p = Prim.run(a);
        check("mst small: both weights are 6", k.weight == 6 && p.weight == 6);
        check("mst small: V-1 edges and connected",
                k.edges.size() == 3 && p.edges.size() == 3 && k.connected && p.connected);

        Graph dc = new Graph(4, false);
        dc.addEdge(0, 1, 1);
        dc.addEdge(2, 3, 2);
        k = Kruskal.run(dc);
        p = Prim.run(dc);
        check("mst disconnected: both detect it", !k.connected && !p.connected);
        check("mst disconnected: forest weight is 3", k.weight == 3 && p.weight == 3);

        k = Kruskal.run(new Graph(1, false));
        p = Prim.run(new Graph(1, false));
        check("mst single vertex: weight 0, connected",
                k.weight == 0 && p.weight == 0 && k.connected && p.connected);

        Graph sq = new Graph(4, false);
        sq.addEdge(0, 1, 1);
        sq.addEdge(1, 2, 1);
        sq.addEdge(2, 3, 1);
        sq.addEdge(3, 0, 1);
        check("mst multiple valid MSTs: same weight 3",
                Kruskal.run(sq).weight == 3 && Prim.run(sq).weight == 3);

        Graph tri = new Graph(3, false);
        tri.addEdge(0, 1, 5);
        tri.addEdge(1, 2, 5);
        tri.addEdge(0, 2, 5);
        check("mst duplicate weights: same weight 10",
                Kruskal.run(tri).weight == 10 && Prim.run(tri).weight == 10);
    }

    static void topo() {
        Graph a = new Graph(5, true);
        a.addEdge(0, 1, 1);
        a.addEdge(0, 2, 1);
        a.addEdge(1, 3, 1);
        a.addEdge(2, 3, 1);
        a.addEdge(3, 4, 1);
        TopoResult r = TopoSort.run(a);
        check("topo DAG: valid ordering", r.dag && r.order.size() == 5 && TopoSort.valid(a, r.order));

        Graph c = new Graph(4, true);
        c.addEdge(0, 1, 1);
        c.addEdge(1, 2, 1);
        c.addEdge(2, 0, 1);
        c.addEdge(2, 3, 1);
        r = TopoSort.run(c);
        check("topo cycle: detected", !r.dag);
        check("topo cycle: no ordering returned", r.order.isEmpty());

        r = TopoSort.run(new Graph(1, true));
        check("topo single vertex", r.dag && r.order.equals(Arrays.asList(0)));

        Graph e = new Graph(3, true);
        r = TopoSort.run(e);
        check("topo no edges: valid ordering", r.dag && TopoSort.valid(e, r.order));

        Graph two = new Graph(4, true);
        two.addEdge(0, 1, 1);
        two.addEdge(2, 3, 1);
        r = TopoSort.run(two);
        check("topo two separate chains: valid ordering", r.dag && TopoSort.valid(two, r.order));
    }

    static void scc() {
        Graph a = new Graph(7, true);
        a.addEdge(0, 1, 1);
        a.addEdge(1, 2, 1);
        a.addEdge(2, 0, 1);
        a.addEdge(2, 3, 1);
        a.addEdge(3, 4, 1);
        a.addEdge(4, 5, 1);
        a.addEdge(5, 3, 1);
        SCCResult r = Tarjan.run(a);
        List<Integer> sizes = new ArrayList<>();
        for (List<Integer> c : r.comps) sizes.add(c.size());
        Collections.sort(sizes);
        check("scc small: component sizes are 1, 3, 3", sizes.equals(Arrays.asList(1, 3, 3)));
        check("scc small: every vertex in exactly one component", partition(a, r));

        Graph dag = GraphGenerator.directedAcyclic(20, 60, 7);
        r = Tarjan.run(dag);
        check("scc DAG: every vertex is its own component", r.comps.size() == 20 && partition(dag, r));

        Graph ring = new Graph(5, true);
        for (int i = 0; i < 5; i++) ring.addEdge(i, (i + 1) % 5, 1);
        check("scc full cycle: one component", Tarjan.run(ring).comps.size() == 1);

        Graph one = new Graph(1, true);
        r = Tarjan.run(one);
        check("scc single vertex", r.comps.size() == 1 && partition(one, r));
    }

    static void dijkstra() {
        Graph a = new Graph(7, true);
        a.addEdge(0, 1, 7);
        a.addEdge(0, 2, 9);
        a.addEdge(0, 5, 14);
        a.addEdge(1, 2, 10);
        a.addEdge(1, 3, 15);
        a.addEdge(2, 3, 11);
        a.addEdge(2, 5, 2);
        a.addEdge(3, 4, 6);
        a.addEdge(5, 4, 9);
        DijkstraResult r = Dijkstra.run(a, 0);
        check("dijkstra: known distances",
                r.dist[0] == 0 && r.dist[1] == 7 && r.dist[2] == 9
                        && r.dist[3] == 20 && r.dist[4] == 20 && r.dist[5] == 11);
        check("dijkstra: path to vertex 4", r.path(4).equals(Arrays.asList(0, 2, 5, 4)));
        check("dijkstra: unreachable vertex", r.dist[6] == DijkstraResult.INF && r.path(6).isEmpty());

        Graph neg = new Graph(3, true);
        neg.addEdge(0, 1, 2);
        neg.addEdge(0, 2, 5);
        neg.addEdge(2, 1, -10);
        boolean threw = false;
        try {
            Dijkstra.run(neg, 0);
        } catch (IllegalArgumentException ex) {
            threw = true;
        }
        check("dijkstra: negative weight rejected", threw);

        check("dijkstra single vertex", Dijkstra.run(new Graph(1, true), 0).dist[0] == 0);

        Graph z = new Graph(3, true);
        z.addEdge(0, 1, 0);
        z.addEdge(1, 2, 5);
        check("dijkstra: zero weight edge", Arrays.equals(Dijkstra.run(z, 0).dist, new long[]{0, 0, 5}));
    }

    static void random() {
        int[][] sizes = {{10, 30}, {50, 200}, {100, 500}};
        boolean mstOk = true, topoOk = true, ringOk = true, sccOk = true, dijOk = true;
        for (int[] z : sizes) {
            for (long seed = 1; seed <= 50; seed++) {
                Graph u = GraphGenerator.connectedUndirected(z[0], z[1], seed);
                MSTResult k = Kruskal.run(u);
                MSTResult p = Prim.run(u);
                if (k.weight != p.weight || !k.connected || !p.connected
                        || k.edges.size() != z[0] - 1 || p.edges.size() != z[0] - 1) mstOk = false;

                Graph dag = GraphGenerator.directedAcyclic(z[0], z[1], seed);
                TopoResult t = TopoSort.run(dag);
                if (!t.dag || !TopoSort.valid(dag, t.order)) topoOk = false;

                Graph ring = new Graph(z[0], true);
                for (int i = 0; i < z[0]; i++) ring.addEdge(i, (i + 1) % z[0], 1);
                Random r = new Random(seed);
                for (int j = 0; j < z[1]; j++) {
                    int a = r.nextInt(z[0]);
                    int b = r.nextInt(z[0]);
                    if (a != b) ring.addEdge(a, b, 1);
                }
                if (TopoSort.run(ring).dag || Tarjan.run(ring).comps.size() != 1) ringOk = false;

                Graph dir = GraphGenerator.directedRandom(z[0], z[1], seed);
                if (!partition(dir, Tarjan.run(dir))) sccOk = false;

                if (!Arrays.equals(Dijkstra.run(u, 0).dist, bf(u, 0))
                        || !Arrays.equals(Dijkstra.run(dir, 0).dist, bf(dir, 0))) dijOk = false;
            }
        }
        check("random x150: Kruskal and Prim give the same weight", mstOk);
        check("random x150: topological order valid on every DAG", topoOk);
        check("random x150: cyclic graphs detected, one big SCC", ringOk);
        check("random x150: every vertex in exactly one SCC", sccOk);
        check("random x150: Dijkstra matches Bellman-Ford", dijOk);
    }

    static boolean partition(Graph g, SCCResult r) {
        boolean[] seen = new boolean[g.getVertexCount()];
        int c = 0;
        for (List<Integer> comp : r.comps) {
            for (int v : comp) {
                if (seen[v]) return false;
                seen[v] = true;
                c++;
            }
        }
        return c == g.getVertexCount();
    }

    static long[] bf(Graph g, int s) {
        int n = g.getVertexCount();
        long[] d = new long[n];
        Arrays.fill(d, DijkstraResult.INF);
        d[s] = 0;
        for (int i = 0; i < n - 1; i++) {
            boolean ch = false;
            for (int u = 0; u < n; u++) {
                if (d[u] == DijkstraResult.INF) continue;
                for (Edge e : g.getNeighbors(u)) {
                    if (d[u] + e.weight < d[e.to]) {
                        d[e.to] = d[u] + e.weight;
                        ch = true;
                    }
                }
            }
            if (!ch) break;
        }
        return d;
    }
}