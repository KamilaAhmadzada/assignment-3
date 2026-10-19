package benchmark;

import dsu.DSU;

public class Main {
    public static void main(String[] args) {
        DSU d = new DSU(10);
        d.union(0, 1);
        d.union(2, 3);
        d.union(1, 3);
        System.out.println("0 and 2 connected: " + d.connected(0, 2));
        System.out.println("0 and 4 connected: " + d.connected(0, 4));
        System.out.println("sets left: " + d.count());
        System.out.println("repeat union returns: " + d.union(0, 2));

        int n = 10000;
        boolean[][] cfg = {{false, false}, {true, false}, {false, true}, {true, true}};
        String[] names = {"none", "compression only", "rank only", "both"};
        for (int k = 0; k < cfg.length; k++) {
            DSU x = new DSU(n, cfg[k][0], cfg[k][1]);
            for (int i = 0; i < n - 1; i++) x.union(i + 1, i);
            x.resetCounters();
            x.find(0);
            x.find(0);
            System.out.println(names[k] + ": hops for two find(0) calls = " + x.hops);
        }
    }
}