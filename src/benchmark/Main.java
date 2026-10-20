package benchmark;

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

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.function.Supplier;
import java.util.function.ToLongFunction;

public class Main {
    static final int RUNS = 5;
    static final int WARM = 3;
    static final long SEED = 42;
    static final int[][] SIZES = {{10, 30}, {100, 500}, {1000, 5000}};
    static final String DIR = "results/tables/";

    static class Stat<T> {
        T last;
        double ms;
    }

    static class Res {
        long t, f, u, h;
    }

    public static void main(String[] args) throws IOException {
        Files.createDirectories(Paths.get(DIR));
        mst();
        conn();
        dij();
        dsu();
        System.out.println("done, tables saved in " + DIR);
    }

    static <T> Stat<T> bench(Supplier<T> run, ToLongFunction<T> time) {
        for (int i = 0; i < WARM; i++) run.get();
        Stat<T> s = new Stat<>();
        long sum = 0;
        for (int i = 0; i < RUNS; i++) {
            s.last = run.get();
            sum += time.applyAsLong(s.last);
        }
        s.ms = sum / (double) RUNS / 1e6;
        return s;
    }

    static String fmt(String f, Object... a) {
        return String.format(Locale.US, f, a);
    }

    static void out(PrintWriter w, String s) {
        w.println(s);
        System.out.println(s);
    }

    static void mst() throws IOException {
        try (PrintWriter w = new PrintWriter(DIR + "mst.csv")) {
            out(w, "V,E,algorithm,avg_ms,weight,examined,comparisons,finds,unions,pq");
            for (int[] z : SIZES) {
                Graph g = GraphGenerator.connectedUndirected(z[0], z[1], SEED);
                Stat<MSTResult> k = bench(() -> Kruskal.run(g), r -> r.timeNs);
                Stat<MSTResult> p = bench(() -> Prim.run(g), r -> r.timeNs);
                out(w, mrow(z, "Kruskal", k));
                out(w, mrow(z, "Prim", p));
            }
        }
    }

    static String mrow(int[] z, String name, Stat<MSTResult> s) {
        MSTResult r = s.last;
        return fmt("%d,%d,%s,%.4f,%d,%d,%d,%d,%d,%d", z[0], z[1], name, s.ms,
                r.weight, r.examined, r.comparisons, r.finds, r.unions, r.pq);
    }

    static void conn() throws IOException {
        try (PrintWriter w = new PrintWriter(DIR + "connectivity.csv")) {
            out(w, "algorithm,graph,V,E,avg_ms,examined,detail");
            for (int[] z : SIZES) {
                Graph dag = GraphGenerator.directedAcyclic(z[0], z[1], SEED);
                Graph cyc = GraphGenerator.directedRandom(z[0], z[1], SEED);
                Stat<TopoResult> t1 = bench(() -> TopoSort.run(dag), r -> r.timeNs);
                Stat<TopoResult> t2 = bench(() -> TopoSort.run(cyc), r -> r.timeNs);
                Stat<SCCResult> s1 = bench(() -> Tarjan.run(dag), r -> r.timeNs);
                Stat<SCCResult> s2 = bench(() -> Tarjan.run(cyc), r -> r.timeNs);
                out(w, fmt("Topo,DAG,%d,%d,%.4f,%d,dag=%b", z[0], z[1], t1.ms, t1.last.examined, t1.last.dag));
                out(w, fmt("Topo,cyclic,%d,%d,%.4f,%d,dag=%b", z[0], z[1], t2.ms, t2.last.examined, t2.last.dag));
                out(w, fmt("SCC,DAG,%d,%d,%.4f,%d,components=%d", z[0], z[1], s1.ms, s1.last.examined, s1.last.comps.size()));
                out(w, fmt("SCC,cyclic,%d,%d,%.4f,%d,components=%d", z[0], z[1], s2.ms, s2.last.examined, s2.last.comps.size()));
            }
        }
    }

    static void dij() throws IOException {
        try (PrintWriter w = new PrintWriter(DIR + "dijkstra.csv")) {
            out(w, "graph,V,E,avg_ms,examined,relaxed,pq,reachable");
            for (int[] z : SIZES) {
                Graph u = GraphGenerator.connectedUndirected(z[0], z[1], SEED);
                Graph d = GraphGenerator.directedRandom(z[0], z[1], SEED);
                out(w, drow("undirected", z, bench(() -> Dijkstra.run(u, 0), r -> r.timeNs)));
                out(w, drow("directed", z, bench(() -> Dijkstra.run(d, 0), r -> r.timeNs)));
            }
        }
    }

    static String drow(String name, int[] z, Stat<DijkstraResult> s) {
        DijkstraResult r = s.last;
        int c = 0;
        for (long x : r.dist) if (x != DijkstraResult.INF) c++;
        return fmt("%s,%d,%d,%.4f,%d,%d,%d,%d", name, z[0], z[1], s.ms,
                r.examined, r.relaxed, r.pq, c);
    }

    static void dsu() throws IOException {
        String[] names = {"none", "compression", "rank", "both"};
        boolean[][] cfg = {{false, false}, {true, false}, {false, true}, {true, true}};
        try (PrintWriter w = new PrintWriter(DIR + "dsu.csv")) {
            out(w, "workload,n,config,avg_ms,finds,unions,hops");
            for (int[] z : SIZES) {
                Graph g = GraphGenerator.connectedUndirected(z[0], z[1], SEED);
                for (int k = 0; k < cfg.length; k++) {
                    boolean c = cfg[k][0];
                    boolean rk = cfg[k][1];
                    Stat<Res> a = bench(() -> dsuRun(z[0], g, c, rk), r -> r.t);
                    Stat<Res> b = bench(() -> dsuRun(z[0], null, c, rk), r -> r.t);
                    out(w, srow("edges", z[0], names[k], a));
                    out(w, srow("chain", z[0], names[k], b));
                }
            }
        }
    }

    static Res dsuRun(int n, Graph g, boolean c, boolean rk) {
        DSU d = new DSU(n, c, rk);
        long t0 = System.nanoTime();
        if (g != null) {
            for (Edge e : g.getEdges()) d.union(e.from, e.to);
        } else {
            for (int i = 0; i < n - 1; i++) d.union(i + 1, i);
            for (int i = 0; i < n; i++) d.find(i);
        }
        Res r = new Res();
        r.t = System.nanoTime() - t0;
        r.f = d.finds;
        r.u = d.unions;
        r.h = d.hops;
        return r;
    }

    static String srow(String wl, int n, String cfg, Stat<Res> s) {
        return fmt("%s,%d,%s,%.4f,%d,%d,%d", wl, n, cfg, s.ms, s.last.f, s.last.u, s.last.h);
    }
}