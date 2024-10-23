package localsearch;

import Utils.AVLIntervalTree;
import Utils.AVLIntervalNode;
import Utils.*;
import Heuristics.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;

public class LocalSearch {
    private Solution<AVLIntervalTree> bestSolution;
    private int bestBusyTime;
    private BCHT<AVLIntervalTree> bchtHeuristic;  // BCHT Heuristic for reinsertion
  //  private BestCapacityHeuristic<AVLIntervalTree> bestCapacityHeuristic;

    public LocalSearch(Solution<AVLIntervalTree> initialSolution, BCHT<AVLIntervalTree> bchtHeuristic) {
        this.bestSolution = initialSolution;
        this.bestBusyTime = initialSolution.getTotalBusyTime();
        this.bchtHeuristic = bchtHeuristic;
    }

    public void run(int iterations) {
        for (int i = 0; i < iterations; i++) {
            Solution<AVLIntervalTree> newSolution = generateNeighbor(bestSolution);

            int newBusyTime = calculateTotalBusyTime(newSolution);
           // System.out.println("New solution with " + newBusyTime + " busy time");
            if (newBusyTime < bestBusyTime) {
                bestSolution = newSolution;
                bestBusyTime = newBusyTime;
                System.out.println("New best solution found with " + bestBusyTime + " busy time");
            }
        }
    }

    // Generate a neighboring solution by making small changes to the current solution
    private Solution<AVLIntervalTree> generateNeighbor(Solution<AVLIntervalTree> solution) {
    //    List<AVLIntervalTree> busiestTrees = getBusiestTrees(solution, 3);
        List<AVLIntervalTree> selectedTrees = selectTrees(solution, 3);


        List<AVLIntervalNode> removedNodes = new ArrayList<>();
        for (AVLIntervalTree tree : selectedTrees) {
            AVLIntervalNode randomNode = tree.getRandomNode();
            if (randomNode != null){
                removedNodes.add(randomNode);
                tree.delete(randomNode);
            }
        }
        List<Request> requestList = new ArrayList<>();
        for (AVLIntervalNode node : removedNodes) {
            Request request = createRequestFromNode(node);
            requestList.add(request);
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
    // Method to select a mix of busiest and random trees
    private List<AVLIntervalTree> selectTrees(Solution<AVLIntervalTree> solution, int count) {
        List<AVLIntervalTree> intervalTrees = solution.getIntervalTrees();

        // Select half based on busiest time and half randomly
        int halfCount = count / 2;

        // Get busiest trees
        List<AVLIntervalTree> busiestTrees = intervalTrees.stream()
                .sorted(Comparator.comparingInt(AVLIntervalTree::calculateTotalBusyTime).reversed())
                .limit(halfCount)
                .toList();

        // Get random trees (excluding the already selected busiest trees)
        List<AVLIntervalTree> randomTrees = new ArrayList<>(intervalTrees);
        randomTrees.removeAll(busiestTrees);
        java.util.Collections.shuffle(randomTrees);
        List<AVLIntervalTree> selectedRandomTrees = randomTrees.stream().limit(count - halfCount).toList();

        // Combine both lists
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
