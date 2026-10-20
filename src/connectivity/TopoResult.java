package connectivity;

import java.util.ArrayList;
import java.util.List;

public class TopoResult {
    public final List<Integer> order = new ArrayList<>();
    public boolean dag;
    public long timeNs;
    public long examined;
    public long pops;
}