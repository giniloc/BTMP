package Utils;

import java.util.ArrayList;
import java.util.List;

public class RBSolution {
    private List<RBIntervalTree> intervalTrees;  // List of servers


    public RBSolution() {
            this.intervalTrees = new ArrayList<>();
        }
        public List<RBIntervalTree> getIntervalTrees() {
            return intervalTrees;
        }

        public void add(RBIntervalTree bestTree) {
            this.intervalTrees.add(bestTree);
        }

}
