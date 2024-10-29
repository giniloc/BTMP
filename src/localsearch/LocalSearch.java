package localsearch;

import IO.InputReader;
import IO.SolutionWriter;
import Utils.AVLIntervalTree;
import Utils.AVLIntervalNode;
import Utils.*;
import Heuristics.*;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;

public class LocalSearch {
    private Solution<AVLIntervalTree> bestSolution;
    private Solution<AVLIntervalTree> initialSolution;
    private Solution<AVLIntervalTree> originalSolution; // De originele, onaangetaste kopie van initialSolution
    private int bestBusyTime;
    private BCHT<AVLIntervalTree> bchtHeuristic;

    public LocalSearch(Solution<AVLIntervalTree> initialSolution, BCHT<AVLIntervalTree> bchtHeuristic) {
        this.bestSolution = new Solution<>(initialSolution);
        this.originalSolution = new Solution<>(initialSolution); // Maak een kopie om te resetten
        this.initialSolution = initialSolution;
        this.bestBusyTime = initialSolution.getTotalBusyTime();
        this.bchtHeuristic = bchtHeuristic;
    }

    public void run(int iterations) {
        for (int i = 0; i < iterations; i++) {
            this.initialSolution = new Solution<>(originalSolution);
            Solution<AVLIntervalTree> newSolution = generateNeighbor(initialSolution);
            int newBusyTime = calculateTotalBusyTime(newSolution);

            if (newBusyTime < bestBusyTime) {
                bestSolution = new Solution<>(newSolution);
                bestBusyTime = newBusyTime;
                System.out.println("New best solution found with " + bestBusyTime + " busy time");
            }
        }
        SolutionWriter.writeSolutionToFile(this.bchtHeuristic.getSolution(), this.bchtHeuristic.getInputReader().getTestInstance(), this.bchtHeuristic.getHeuristicName(), bestBusyTime);
    }

    // Generate a neighboring solution by making small changes to the current solution
    private Solution<AVLIntervalTree> generateNeighbor(Solution<AVLIntervalTree> solution) {
        List<AVLIntervalTree> selectedTrees = selectTrees(solution, 3);
      //  List<AVLIntervalTree> selectedTrees = getBusiestTrees(solution, 10);
        List<Request> requestList = new ArrayList<>();


        for (AVLIntervalTree tree : selectedTrees) {
            if (tree.getRoot() == null) {
                solution.getIntervalTrees().remove(tree);
                continue;
            }
            // Hier zit de fout
            AVLIntervalNode randomNode = tree.getRandomNode();
            if (randomNode != null) {
                Request request = createRequestFromNode(randomNode);
                requestList.add(request);
                tree.delete(randomNode);
            }
        }

        bchtHeuristic.applyHeuristic(requestList);

        return solution;
    }

    private List<AVLIntervalTree> getBusiestTrees(Solution<AVLIntervalTree> solution, int count) {
        return solution.getIntervalTrees()
                .stream()
                .sorted(Comparator.comparingInt(AVLIntervalTree::calculateTotalBusyTime).reversed())
                .limit(count)
                .toList();
    }

    private int calculateTotalBusyTime(Solution<AVLIntervalTree> solution) {
        int totalBusyTime = 0;
        for (AVLIntervalTree tree : solution.getIntervalTrees()) {
            totalBusyTime += tree.calculateTotalBusyTime();
        }
        return totalBusyTime;
    }
    private Request createRequestFromNode(AVLIntervalNode node) {
        return new Request(node.getID(), node.getInterval().getStartTime(), node.getInterval().getEndTime(), node.getWeight());
    }
    private List<AVLIntervalTree> selectTrees(Solution<AVLIntervalTree> solution, int count) {
        List<AVLIntervalTree> intervalTrees = solution.getIntervalTrees();

        // Select half based on busiest time and half randomly
        int halfCount = count / 2;

        List<AVLIntervalTree> busiestTrees = intervalTrees.stream()
                .sorted(Comparator.comparingInt(AVLIntervalTree::calculateTotalBusyTime).reversed())
                .limit(halfCount)
                .toList();

        // Get random trees (excluding the already selected busiest trees)
        List<AVLIntervalTree> randomTrees = new ArrayList<>(intervalTrees);
        randomTrees.removeAll(busiestTrees);
        Collections.shuffle(randomTrees);
        List<AVLIntervalTree> selectedRandomTrees = randomTrees.stream().limit(count - halfCount).toList();

        List<AVLIntervalTree> selectedTrees = new ArrayList<>(busiestTrees);
        selectedTrees.addAll(selectedRandomTrees);

        return selectedTrees;
    }

    public Solution<AVLIntervalTree> getBestSolution() {
        return bestSolution;
    }

    public int getBestBusyTime() {
        return bestBusyTime;
    }

}
