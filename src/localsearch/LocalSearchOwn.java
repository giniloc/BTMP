package localsearch;

import Heuristics.IHeuristic;
import IO.InputReader;
import IO.SolutionWriter;
import Utils.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class LocalSearchOwn <T extends IIntervalTree<N>,N extends IntervalNode> implements ILocalSearch {
    private Solution<T> bestSolution;
    private Solution<T> currentSolution, oldSolution;
    private int bestBusyTime;
    private IHeuristic heuristic;
    private List<Move<N>> moves;
    private IIntervalTreeFactory<T> intervalTreeFactory;
    private InputReader inputReader;
    private boolean deepCopyRollback;

    public LocalSearchOwn(Solution<T> initialSolution, IHeuristic heuristic, boolean deepCopyRollback, InputReader inputReader) {
        this.bestSolution = new Solution<>(initialSolution);
        this.currentSolution = new Solution<>(initialSolution);
        if (deepCopyRollback) this.oldSolution = new Solution<>(initialSolution);
        this.bestBusyTime = initialSolution.getTotalBusyTime();
        if (!deepCopyRollback) this.moves = new ArrayList<>();
        this.heuristic = heuristic;
        this.intervalTreeFactory = heuristic.getFactory();
        this.deepCopyRollback = deepCopyRollback;
        this.inputReader = inputReader;
    }


    /**
     *
     * @param iterations how many iterations do we perform
     * @param nrOfTrees how many trees do i use to remove a node from
     * @return
     */
    public LocalSearchResult run(int iterations, int nrOfTrees) {
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            generateNeighbor(currentSolution, nrOfTrees);
            int newBusyTime = calculateTotalBusyTime(currentSolution);

            if (newBusyTime < bestBusyTime) {
                bestBusyTime = newBusyTime;
                bestSolution = new Solution<>(currentSolution);
                if(deepCopyRollback) oldSolution = new Solution<>(currentSolution);
                if (!deepCopyRollback) moves.clear();
            } else {
                if (deepCopyRollback) this.currentSolution = new Solution<>(oldSolution);
                else rollback();
            }
        }

        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;
        System.out.println("Elapsed time: "+ elapsedTime);

        SolutionWriter.writeSolutionToFile(bestSolution, heuristic.getInputReader().getTestInstance(), heuristic.getHeuristicName(), bestBusyTime);

        return new LocalSearchResult(elapsedTime, bestBusyTime);
    }
    public LocalSearchResult run(int nrOfTrees) {
        int counter = 0;
        // int iterationIndex = 0;
        long startTime = System.currentTimeMillis();
        //long maxDuration = 1800000; // 30 minutes in milliseconds

        //while ((System.currentTimeMillis()- startTime) < maxDuration) {
        while (counter < 100_000){
            generateNeighbor(currentSolution, nrOfTrees);
            int newBusyTime = calculateTotalBusyTime(currentSolution);

            if (newBusyTime < bestBusyTime) {
                bestBusyTime = newBusyTime;
                bestSolution = new Solution<>(currentSolution);

                if (deepCopyRollback) {
                    oldSolution = new Solution<>(currentSolution);
                }
                if (!deepCopyRollback) {
                    moves.clear();
                }
                counter = 0;
            } else {
                counter++;
                if (deepCopyRollback) {
                    this.currentSolution = new Solution<>(oldSolution);
                } else {
                    rollback();
                }
            }
            //   SolutionWriter.solutionAnalysis(heuristic.getHeuristicName(),inputReader.getTestInstance(), iterationIndex, bestBusyTime);//This is for solution analysis
            //   iterationIndex++;
        }

        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;

        System.out.println("Elapsed time: " + elapsedTime);

        SolutionWriter.writeSolutionToFile(bestSolution, heuristic.getInputReader().getTestInstance(), heuristic.getHeuristicName(), bestBusyTime);

        return new LocalSearchResult(elapsedTime, bestBusyTime);
    }
    private void generateNeighbor(Solution<T> solution, int nrOfTrees) {
        var busiestTrees = selectTrees(solution, nrOfTrees);
        for (T tree : busiestTrees) {
            int busyTimeTree = tree.calculateTotalBusyTime();
            N nodeToRemove = tree.getMaxEndTimeNode();
            tree.delete(nodeToRemove);
            int newBusyTimeTree = tree.calculateTotalBusyTime();
            if (newBusyTimeTree < busyTimeTree) {
                T newTree = findBetterTree(nodeToRemove, busyTimeTree - newBusyTimeTree);
                if (newTree != null) {
                    tree.insert(nodeToRemove);
                }
                else tree.insert(nodeToRemove);
            }
            else {
                tree.insert(nodeToRemove);
            }
        }

    }

    private T findBetterTree(N nodeToRemove, int profit) {
        T bestTree = null;
        int bestProfit = profit;
        for (T tree: this.currentSolution.getIntervalTrees()){
            int addedTime = tree.calculateExtraBusyTime(nodeToRemove.getInterval());
            if (addedTime < bestProfit) {
                bestTree = tree;
                bestProfit = addedTime;
            }
        }
        return bestTree;
    }

    private List<T> selectTrees(Solution<T> solution, int count) {  //selects n busiest trees
        var intervalTrees = solution.getIntervalTrees();

        var busiestTrees = intervalTrees.stream()
                .sorted(Comparator.comparingInt(T::calculateTotalBusyTime).reversed())
                .toList();

        return busiestTrees.subList(0, count);
    }


    private int calculateTotalBusyTime(Solution<T> solution) {
        int totalBusyTime = 0;
        for (T tree : solution.getIntervalTrees()) {
            totalBusyTime += tree.calculateTotalBusyTime();
        }
        return totalBusyTime;
    }
    private Request createRequestFromNode(AVLIntervalNode node) {
        return new Request(node.getID(), node.getInterval().getStartTime(), node.getInterval().getEndTime(), node.getWeight());
    }
    private void rollback() {
        for (int i = moves.size() - 1; i >= 0; i--) {
            var move = moves.get(i);
            if (move.isDelete()) {
                move.getTree().insert(move.getNode());
            } else {
                var node = move.getNode();
                move.getTree().delete(node);
            }
        }
        moves.clear();
    }
}
