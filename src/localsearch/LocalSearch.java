package localsearch;

import Utils.AVLIntervalTree;
import Utils.AVLIntervalNode;
import Utils.*;
import Heuristics.BCHT;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;

public class LocalSearch {
    private Solution<AVLIntervalTree> bestSolution;
    private int bestBusyTime;
    private BCHT<AVLIntervalTree> bchtHeuristic;  // BCHT Heuristic for reinsertion

    public LocalSearch(Solution<AVLIntervalTree> initialSolution, BCHT<AVLIntervalTree> bchtHeuristic) {
        this.bestSolution = initialSolution;
        this.bestBusyTime = initialSolution.getTotalBusyTime();
        this.bchtHeuristic = bchtHeuristic;
    }

    public void run(int iterations) {
        for (int i = 0; i < iterations; i++) {
            Solution<AVLIntervalTree> newSolution = generateNeighbor(bestSolution);

            int newBusyTime = calculateTotalBusyTime(newSolution);

            if (newBusyTime < bestBusyTime) {
                bestSolution = newSolution;
                bestBusyTime = newBusyTime;
                System.out.println("New best solution found with " + bestBusyTime + " busy time");
            }
        }
    }

    // Generate a neighboring solution by making small changes to the current solution
    private Solution<AVLIntervalTree> generateNeighbor(Solution<AVLIntervalTree> solution) {
        List<AVLIntervalTree> busiestTrees = getBusiestTrees(solution, 4);

        List<AVLIntervalNode> removedNodes = new ArrayList<>();
        for (AVLIntervalTree tree : busiestTrees) {
            AVLIntervalNode randomNode = tree.getRandomNode();
            if (randomNode != null){
                removedNodes.add(randomNode);
                tree.delete(randomNode);
            }
        }
        for (AVLIntervalNode node : removedNodes) {
            Request request = createRequestFromNode(node);
            List<Request> requestList = List.of(request);
            bchtHeuristic.applyHeuristic(requestList);
        }

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
        return new Request(node.getInterval().getStartTime(), node.getInterval().getEndTime(), node.getWeight(), node.getID());
    }

    public Solution<AVLIntervalTree> getBestSolution() {
        return bestSolution;
    }

    public int getBestBusyTime() {
        return bestBusyTime;
    }

}
