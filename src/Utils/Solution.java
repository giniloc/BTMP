package Utils;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Solution<T extends IIntervalTree<? extends IIntervalNode>> {
    private List<T> intervalTrees;  // List of servers

    public Solution() {
        this.intervalTrees = new ArrayList<>();
    }

    public Solution(Solution<T> otherSolution) {
        this.intervalTrees = new ArrayList<>();

        // Deep copy each tree in the other solution
        for (T tree : otherSolution.getIntervalTrees()) {
            this.intervalTrees.add((T) tree.deepCopy());
        }
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