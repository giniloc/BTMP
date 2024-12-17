package Heuristics;

import IO.InputReader;
import IO.SolutionWriter;
import Utils.*;
import java.util.List;
import java.io.FileWriter;
import java.io.IOException;

public class BCHT<T extends IIntervalTree<? extends IIntervalNode>> implements IHeuristic {
    private InputReader inputReader;
    private IIntervalTreeFactory<T> factory;
    private Solution<T> solution;
    private String heuristicName;

    public String getHeuristicName() { return heuristicName;}

    public BCHT(InputReader inputReader, IIntervalTreeFactory<T> factory, String heuristicName) {
        this.inputReader = inputReader;
        this.solution = new Solution<>();
        this.factory = factory;
        this.heuristicName = heuristicName;
    }

    public void applyHeuristic(List<Request> requests) {
        long startTime = System.nanoTime();

        for (Request request : requests) {
            Interval interval = new Interval(request.getStartTime(), request.getEndTime());
            IntervalNode node = new IntervalNode(interval, request.getWeight(), request.getVmId());

            T bestTree = null;

            for (var intervalTree : solution.getIntervalTrees()) {
                var overlappingNodes = intervalTree.findAllOverlapping(interval);
                int sum = 0;
                for (var overlappingNode : overlappingNodes) {
                    sum += overlappingNode.getWeight();
                }

                // Check if server has enough capacity for request
                if (sum + request.getWeight() <= inputReader.getServerCapacity() && sum != 0) {
                    // search for server with least extra busy time
                    if (bestTree == null || intervalTree.calculateExtraBusyTime(interval) < bestTree.calculateExtraBusyTime(interval)) {
                        bestTree = intervalTree;
                    }
                }
            }

            // if no bestTree was found, create a new one
            if (bestTree == null) {
                bestTree = factory.create();
                solution.add(bestTree);
            }

            bestTree.insert(node);
        }

        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1_000_000; // runtime in milliseconds

        writeRuntimeToCSV(duration);

        // int totalBusyTime = 0;
        // for (var intervalTree : solution.getIntervalTrees()) {
        //     totalBusyTime += intervalTree.calculateTotalBusyTime();
        // }

        // SolutionWriter.writeSolutionToFile(solution, inputReader.getTestInstance(), this.heuristicName, totalBusyTime);
    }

    private void writeRuntimeToCSV(long duration) {
        try (FileWriter writer = new FileWriter("runtime.csv", true)) {
            writer.append(',')
                    .append(Long.toString(duration))
                    .append('\n');
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Solution<T> getSolution() {
        return solution;
    }

    public void setSolution(Solution<T> solution) {
        this.solution = solution;
    }

    public InputReader getInputReader() {
        return inputReader;
    }

    public IIntervalTreeFactory<T> getFactory() {
        return factory;
    }
}
