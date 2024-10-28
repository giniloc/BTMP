package localsearch;

import Heuristics.BCHT;
import Utils.AVLIntervalNode;
import Utils.AVLIntervalTree;
import Utils.Request;
import Utils.Solution;

import java.util.Comparator;
import java.util.List;

public class LocalSearchOwn {
    private Solution<AVLIntervalTree> bestSolution;
    private int bestBusyTime;
    private BCHT<AVLIntervalTree> bchtHeuristic;

    public LocalSearchOwn(Solution<AVLIntervalTree> initialSolution, BCHT<AVLIntervalTree> bchtHeuristic) {
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
    private Solution<AVLIntervalTree> generateNeighbor(Solution<AVLIntervalTree> bestSolution) {
        List<AVLIntervalTree> busiestTrees = getBusiestTrees(bestSolution, 2);
        return null;
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
}
