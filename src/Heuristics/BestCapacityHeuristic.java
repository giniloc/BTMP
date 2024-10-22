package Heuristics;

import IO.InputReader;
import IO.SolutionWriter;
import Utils.*;

import java.util.List;

public class BestCapacityHeuristic<T extends IIntervalTree<? extends IIntervalNode>> implements IHeuristic {
    private InputReader inputReader;
    private IIntervalTreeFactory<T> factory;
    private Solution<T> solution;
    private String heuristicName;

    public BestCapacityHeuristic(InputReader inputReader, IIntervalTreeFactory<T> factory, String heuristicName) {
        this.inputReader = inputReader;
        this.solution = new Solution<>();
        this.factory = factory;
        this.heuristicName = heuristicName;
    }

    @Override
    public String getHeuristicName() {
        return this.heuristicName;
    }

    public void applyHeuristic(List<Request> requests) {
        for (Request request : requests) {
            Interval interval = new Interval(request.getStartTime(), request.getEndTime());
            IntervalNode node = new IntervalNode(interval, request.getWeight(), request.getVmId());

            T bestTree = null;
            int bestRemainingCapacity = Integer.MAX_VALUE;  // We want the server with the least remaining capacity

            for (var intervalTree : solution.getIntervalTrees()) {
                var overlappingNodes = intervalTree.findAllOverlapping(interval);
                int sum = 0;

                for (var overlappingNode : overlappingNodes) {
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
                bestTree = factory.create();
                solution.add(bestTree);
            }

            // Add request to bestTree
            bestTree.insert(node);
        }

        int totalBusyTime = 0;
        for (var intervalTree : solution.getIntervalTrees()) {
            totalBusyTime += intervalTree.calculateTotalBusyTime();
        }

        SolutionWriter.writeSolutionToFile(solution, inputReader.getTestInstance(), this.heuristicName, totalBusyTime);
    }

    @Override
    public Solution getSolution() {
        return null;
    }
}
