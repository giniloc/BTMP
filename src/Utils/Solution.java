package Utils;
import java.util.ArrayList;
import java.util.List;

public class Solution {
    private List<IntervalTree> intervalTrees;  // List of servers


    public Solution() {
        this.intervalTrees = new ArrayList<>();
    }

    public List<IntervalTree> getIntervalTrees() {
        return intervalTrees;
    }

    public void add(IntervalTree bestTree) {
        this.intervalTrees.add(bestTree);
    }
}
