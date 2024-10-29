package Utils;
import java.util.ArrayList;
import java.util.List;

public class Solution<T extends IIntervalTree<? extends IIntervalNode>> {
    private List<T> intervalTrees;  // List of servers

    public Solution() {
        this.intervalTrees = new ArrayList<>();
    }

    public Solution(Solution<T> initialSolution) {
        this.intervalTrees = new ArrayList<>(initialSolution.intervalTrees);
    }

    public List<T> getIntervalTrees() {
        return intervalTrees;
    }

    public void add(T bestTree) {
        this.intervalTrees.add(bestTree);
    }

    public int getTotalBusyTime() {
        int totalBusyTime = 0;
        for (var intervalTree : intervalTrees) {
            totalBusyTime += intervalTree.calculateTotalBusyTime();
        }
        return totalBusyTime;
    }
}