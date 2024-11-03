package localsearch;

import IO.SolutionWriter;
import Utils.*;
import Heuristics.*;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;

public class LocalSearchGeneric<T extends IIntervalTree<? extends IIntervalNode>> {
    private Solution<T> bestSolution;
    private Solution<T> initialSolution;
    private int bestBusyTime;
    private IHeuristic bchtHeuristic;

    public LocalSearchGeneric(Solution<T> initialSolution, IHeuristic bchtHeuristic) {
        this.initialSolution = initialSolution;
        this.bestBusyTime = initialSolution.getTotalBusyTime();
        this.bchtHeuristic = bchtHeuristic;
    }

    public void run(int iterations) {
        for (int i = 0; i < iterations; i++) {
             System.out.println("Iteration " + i);
            var newSolution = generateNeighbor(initialSolution);
            int newBusyTime = calculateTotalBusyTime(bchtHeuristic.getSolution());

            if (newBusyTime < bestBusyTime) {
                bestSolution = new Solution<>(newSolution);
                bestBusyTime = newBusyTime;
                System.out.println("New best solution found with " + bestBusyTime + " busy time");
                SolutionWriter.writeSolutionToFile(bchtHeuristic.getSolution(), this.bchtHeuristic.getInputReader().getTestInstance(), this.bchtHeuristic.getHeuristicName(), bestBusyTime);
            }
        }
    }

    // Generate a neighboring solution by making small changes to the current solution
    private Solution<T> generateNeighbor(Solution<T> solution) {
        var selectedTrees = selectTrees(solution, 20);
        //  List<AVLIntervalTree> selectedTrees = getBusiestTrees(solution, 10);
        List<Request> requestList = new ArrayList<>();


        for (T tree : selectedTrees) {
            // Hier zit de fout
            var randomNode = tree.getRandomNode();
            if (randomNode != null) {
                Request request = createRequestFromNode(randomNode);
                requestList.add(request);
                //todo avoid cast?
                tree.delete((IntervalNode) randomNode);
                if (tree.getRoot() == null) {
                    solution.getIntervalTrees().remove(tree);
                }
            }
        }

        bchtHeuristic.applyHeuristic(requestList);

        return solution;
    }

    private List<T> getBusiestTrees(Solution<T> solution, int count) {
        return solution.getIntervalTrees()
                .stream()
                .sorted(Comparator.comparingInt(T::calculateTotalBusyTime).reversed())
                .limit(count)
                .toList();
    }

    private int calculateTotalBusyTime(Solution<T> solution) {
        int totalBusyTime = 0;
        for (T tree : solution.getIntervalTrees()) {
            totalBusyTime += tree.calculateTotalBusyTime();
        }
        return totalBusyTime;
    }
    private Request createRequestFromNode(IIntervalNode node) {
        return new Request(node.getID(), node.getInterval().getStartTime(), node.getInterval().getEndTime(), node.getWeight());
    }
    private List<T> selectTrees(Solution<T> solution, int count) {
        var intervalTrees = solution.getIntervalTrees();

        // Select half based on busiest time and half randomly
        int halfCount = count / 2;

        var busiestTrees = intervalTrees.stream()
                .sorted(Comparator.comparingInt(T::calculateTotalBusyTime).reversed())
                .limit(halfCount)
                .toList();

        // Get random trees (excluding the already selected busiest trees)
        var randomTrees = new ArrayList<>(intervalTrees);
        randomTrees.removeAll(busiestTrees);
        Collections.shuffle(randomTrees);
        var selectedRandomTrees = randomTrees.stream().limit(count - halfCount).toList();

        var selectedTrees = new ArrayList<>(busiestTrees);
        selectedTrees.addAll(selectedRandomTrees);

        return selectedTrees;
    }

    public Solution<T> getBestSolution() {
        return bestSolution;
    }

    public int getBestBusyTime() {
        return bestBusyTime;
    }


}