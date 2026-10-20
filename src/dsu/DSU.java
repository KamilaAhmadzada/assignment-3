package dsu;

public class DSU {
    private final int[] p;
    private final int[] rk;
    private final boolean compress;
    private final boolean byRank;
    private int sets;
    public long finds;
    public long unions;
    public long hops;

    public DSU(int n) {
        this(n, true, true);
    }

    public DSU(int n, boolean compress, boolean byRank) {
        this.p = new int[n];
        this.rk = new int[n];
        this.compress = compress;
        this.byRank = byRank;
        this.sets = n;
        for (int i = 0; i < n; i++) p[i] = i;
    }

    public int find(int x) {
        finds++;
        int r = x;
        while (p[r] != r) {
            r = p[r];
            hops++;
        }
        if (compress) {
            while (p[x] != r) {
                int next = p[x];
                p[x] = r;
                x = next;
            }
        }
        return r;
    }

    public boolean union(int a, int b) {
        int ra = find(a);
        int rb = find(b);
        if (ra == rb) return false;
        unions++;
        if (byRank) {
            if (rk[ra] < rk[rb]) {
                int t = ra;
                ra = rb;
                rb = t;
            }
            p[rb] = ra;
            if (rk[ra] == rk[rb]) rk[ra]++;
        } else {
            p[rb] = ra;
        }
        sets--;
        return true;
    }

    public boolean connected(int a, int b) {
        return find(a) == find(b);
    }

    public int count() {
        return sets;
    }

    public void resetCounters() {
        finds = 0;
        unions = 0;
        hops = 0;
    }
}