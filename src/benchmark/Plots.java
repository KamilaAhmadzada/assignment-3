package benchmark;

import javax.imageio.ImageIO;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Plots {
    static final String T = "results/tables/";
    static final String P = "results/plots/";
    static final Color[] COL = {
            new Color(31, 119, 180), new Color(255, 127, 14), new Color(44, 160, 44),
            new Color(214, 39, 40), new Color(148, 103, 189), new Color(140, 86, 75)};

    public static void main(String[] args) throws IOException {
        Files.createDirectories(Paths.get(P));
        Map<String, List<double[]>> all = new LinkedHashMap<>();
        Map<String, List<double[]>> mst = new LinkedHashMap<>();
        Map<String, List<double[]>> dsu = new LinkedHashMap<>();

        for (String[] c : rows("mst.csv")) {
            double[] p = {num(c[0]), num(c[1]), num(c[3])};
            all.computeIfAbsent(c[2], k -> new ArrayList<>()).add(p);
            mst.computeIfAbsent(c[2], k -> new ArrayList<>()).add(p);
        }
        for (String[] c : rows("connectivity.csv")) {
            String k = c[0] + " (" + c[1] + ")";
            all.computeIfAbsent(k, x -> new ArrayList<>())
                    .add(new double[]{num(c[2]), num(c[3]), num(c[4])});
        }
        for (String[] c : rows("dijkstra.csv")) {
            String k = "Dijkstra (" + c[0] + ")";
            all.computeIfAbsent(k, x -> new ArrayList<>())
                    .add(new double[]{num(c[1]), num(c[2]), num(c[3])});
        }
        for (String[] c : rows("dsu.csv")) {
            if (!c[0].equals("chain")) continue;
            dsu.computeIfAbsent(c[2], k -> new ArrayList<>())
                    .add(new double[]{num(c[1]), num(c[1]), num(c[6])});
        }

        chart("time_vs_vertices.png", "Execution time vs number of vertices",
                "vertices V (log scale)", "average time, ms (log scale)", all, 0);
        chart("time_vs_edges.png", "Execution time vs number of edges",
                "edges E (log scale)", "average time, ms (log scale)", all, 1);
        chart("kruskal_vs_prim.png", "Kruskal vs Prim",
                "edges E (log scale)", "average time, ms (log scale)", mst, 1);
        chart("dsu_chain_hops.png", "DSU: parent steps on a chain workload",
                "elements n (log scale)", "parent steps (log scale)", dsu, 0);
        System.out.println("plots saved in " + P);
    }

    static List<String[]> rows(String f) throws IOException {
        List<String[]> r = new ArrayList<>();
        List<String> ls = Files.readAllLines(Paths.get(T + f));
        for (int i = 1; i < ls.size(); i++) {
            if (!ls.get(i).isBlank()) r.add(ls.get(i).split(","));
        }
        return r;
    }

    static double num(String s) {
        return Double.parseDouble(s);
    }

    static double lg(double v) {
        return Math.log10(Math.max(v, 1e-3));
    }

    static String lab(int k) {
        return k >= 0 ? String.valueOf((long) Math.pow(10, k)) : "1e" + k;
    }

    static void chart(String file, String title, String xl, String yl,
                      Map<String, List<double[]>> data, int xi) throws IOException {
        int W = 980, H = 600, L = 90, R = 250, TP = 60, B = 80;
        int w = W - L - R, h = H - TP - B;
        double x0 = 1e18, x1 = -1e18, y0 = 1e18, y1 = -1e18;
        for (List<double[]> l : data.values()) {
            for (double[] p : l) {
                x0 = Math.min(x0, lg(p[xi]));
                x1 = Math.max(x1, lg(p[xi]));
                y0 = Math.min(y0, lg(p[2]));
                y1 = Math.max(y1, lg(p[2]));
            }
        }
        int xa = (int) Math.floor(x0), xb = (int) Math.ceil(x1);
        int ya = (int) Math.floor(y0), yb = (int) Math.ceil(y1);
        if (xb == xa) xb++;
        if (yb == ya) yb++;

        BufferedImage img = new BufferedImage(W, H, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, W, H);
        g.setFont(new Font("SansSerif", Font.PLAIN, 13));

        for (int k = xa; k <= xb; k++) {
            int px = L + (int) ((k - xa) * (double) w / (xb - xa));
            g.setColor(new Color(225, 225, 225));
            g.drawLine(px, TP, px, TP + h);
            g.setColor(Color.BLACK);
            String s = lab(k);
            g.drawString(s, px - g.getFontMetrics().stringWidth(s) / 2, TP + h + 20);
        }
        for (int k = ya; k <= yb; k++) {
            int py = TP + h - (int) ((k - ya) * (double) h / (yb - ya));
            g.setColor(new Color(225, 225, 225));
            g.drawLine(L, py, L + w, py);
            g.setColor(Color.BLACK);
            String s = lab(k);
            g.drawString(s, L - 10 - g.getFontMetrics().stringWidth(s), py + 5);
        }
        g.setColor(Color.BLACK);
        g.drawRect(L, TP, w, h);

        g.setFont(new Font("SansSerif", Font.BOLD, 17));
        g.drawString(title, L + (w - g.getFontMetrics().stringWidth(title)) / 2, 35);
        g.setFont(new Font("SansSerif", Font.PLAIN, 14));
        g.drawString(xl, L + (w - g.getFontMetrics().stringWidth(xl)) / 2, H - 25);
        g.rotate(-Math.PI / 2);
        g.drawString(yl, -(TP + h / 2 + g.getFontMetrics().stringWidth(yl) / 2), 25);
        g.rotate(Math.PI / 2);

        g.setFont(new Font("SansSerif", Font.PLAIN, 13));
        int i = 0;
        for (Map.Entry<String, List<double[]>> e : data.entrySet()) {
            List<double[]> pts = new ArrayList<>(e.getValue());
            pts.sort(Comparator.comparingDouble(q -> q[xi]));
            Color c = COL[i % COL.length];
            g.setColor(c);
            g.setStroke(new BasicStroke(2f));
            int lx = -1, ly = -1;
            for (double[] p : pts) {
                int px = L + (int) ((lg(p[xi]) - xa) * w / (double) (xb - xa));
                int py = TP + h - (int) ((lg(p[2]) - ya) * h / (double) (yb - ya));
                if (lx >= 0) g.drawLine(lx, ly, px, py);
                g.fillOval(px - 4, py - 4, 8, 8);
                lx = px;
                ly = py;
            }
            g.fillRect(L + w + 20, TP + 20 + i * 26, 14, 14);
            g.setColor(Color.BLACK);
            g.drawString(e.getKey(), L + w + 42, TP + 32 + i * 26);
            i++;
        }
        g.dispose();
        ImageIO.write(img, "png", new File(P + file));
    }
}