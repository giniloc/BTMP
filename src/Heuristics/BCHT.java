package Heuristics;
import java.util.List;
import Utils.*;
import IO.*;

public class BCHT {
    private InputReader inputReader;
    private Solution solution;

    public BCHT(InputReader inputReader) {
        this.inputReader = inputReader;
        this.solution = new Solution();
    }

    public void applyHeuristic(List<Request> requests) {
        for (Request request : requests) {
            Interval interval = new Interval(request.getStartTime(), request.getEndTime());
            IntervalNode node = new IntervalNode(interval, request.getWeight(), request.getVmId());

            IntervalTree bestTree = null;

            for (IntervalTree intervalTree : solution.getIntervalTrees()) {
                List<IntervalNode> overlappingNodes = intervalTree.findAllOverlapping(intervalTree.getRoot(), interval);
                int sum = 0;
                for (IntervalNode overlappingNode : overlappingNodes) {
                    sum += overlappingNode.getWeight();
                }

                // Check if server has enough capacity for request
                if (sum + request.getWeight() <= inputReader.getServerCapacity()) {
                    // seararch for server with least extra busy time
                    if (bestTree == null ||
                            intervalTree.calculateExtraBusyTime(interval) < bestTree.calculateExtraBusyTime(interval)) {
                        bestTree = intervalTree;
                    }
                }
            }

            // if no bestTree was found, create a new one
            if (bestTree == null) {
                bestTree = new IntervalTree();
                solution.add(bestTree);
            }

            // add request to bestTree
            bestTree.insert(node);
        }
        int totalBusyTime = 0;
        for (IntervalTree intervalTree : solution.getIntervalTrees()) {
            totalBusyTime += intervalTree.calculateTotalBusyTime();
        }
        SolutionWriter.writeSolutionToFile(solution, inputReader.getTestInstance(), "BCHT", totalBusyTime);

    }
}
