package Heuristics;

import IO.InputReader;
import IO.SolutionWriter;
import Utils.*;

import java.util.List;

public class BestCapacityHeuristic {
    private InputReader inputReader;
    private Solution solution;

    public BestCapacityHeuristic(InputReader inputReader) {
        this.inputReader = inputReader;
        this.solution = new Solution();
    }

    public void applyHeuristic(List<Request> requests) {
        for (Request request : requests) {
            Interval interval = new Interval(request.getStartTime(), request.getEndTime());
            IntervalNode node = new IntervalNode(interval, request.getWeight(), request.getVmId());

            IntervalTree bestTree = null;
            int bestRemainingCapacity = Integer.MAX_VALUE;  // We want the server with the least remaining capacity

            for (IntervalTree intervalTree : solution.getIntervalTrees()) {
                List<IntervalNode> overlappingNodes = intervalTree.findAllOverlapping(intervalTree.getRoot(), interval);
                int sum = 0;

                for (IntervalNode overlappingNode : overlappingNodes) {
                    sum += overlappingNode.getWeight();
                }

                // Check if the server can accommodate the request
                if (sum + request.getWeight() <= inputReader.getServerCapacity()) {
                    // Calculate the remaining capacity after placing the request
                    int remainingCapacity = inputReader.getServerCapacity() - (sum + request.getWeight());

                    // We want the server that leaves the least remaining capacity
                    if (bestTree == null || remainingCapacity < bestRemainingCapacity) {
                        bestRemainingCapacity = remainingCapacity;
                        bestTree = intervalTree;
                    }
                }
            }

            // If no suitable server was found, create a new server
            if (bestTree == null) {
                bestTree = new IntervalTree();
                solution.add(bestTree);
            }

            // Add request to bestTree
            bestTree.insert(node);
        }

        int totalBusyTime = 0;
        for (IntervalTree intervalTree : solution.getIntervalTrees()) {
            totalBusyTime += intervalTree.calculateTotalBusyTime();
        }

        SolutionWriter.writeSolutionToFile(solution, inputReader.getTestInstance(), "BestCap", totalBusyTime);
    }
}
