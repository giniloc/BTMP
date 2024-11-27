package localsearch;

import Heuristics.IHeuristic;
import IO.InputReader;
import IO.SolutionWriter;
import Utils.*;

import java.util.*;


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
//     * @param nrOfTrees how many trees do i use to remove a node from
     * @return
     */
    public LocalSearchResult run(int iterations) {
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            generateNeighbor(currentSolution);
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
    public LocalSearchResult run() {
        int counter = 0;
        // int iterationIndex = 0;
        long startTime = System.currentTimeMillis();
        //long maxDuration = 1800000; // 30 minutes in milliseconds

        //while ((System.currentTimeMillis()- startTime) < maxDuration) {
        while (counter < 100_000){
            generateNeighbor(currentSolution);
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
    private void generateNeighbor(Solution<T> solution) {
        var busiestTrees = selectTrees(solution);
        makeCombination(busiestTrees);
    }


    private List<T> selectTrees(Solution<T> solution) {  //selects n busiest trees
        var intervalTrees = solution.getIntervalTrees();

        var busiestTrees = intervalTrees.stream()
                .sorted(Comparator.comparingInt(T::calculateTotalBusyTime).reversed())
                .toList();

        return busiestTrees;
    }
    private void makeCombination(List<T> busiestTrees) {
        T currentTree = busiestTrees.get(0);
        T secondTree = busiestTrees.get(1);

        N nodeToRemove = currentTree.getMaxEndTimeNode();
        int busyTimeCurrentBefore = currentTree.calculateTotalBusyTime();
        N firstDeletedNode = currentTree.delete(nodeToRemove);
        int profitCurrent = busyTimeCurrentBefore - currentTree.calculateTotalBusyTime();

        // Check if the node can be inserted in the second tree with profit
        int overlappingWeightSecond = secondTree.findAllOverlapping(nodeToRemove.getInterval())
                .stream()
                .mapToInt(IntervalNode::getWeight)
                .sum();
        if (overlappingWeightSecond + nodeToRemove.getWeight() <= inputReader.getServerCapacity()) {
            int extraCost = secondTree.calculateExtraBusyTime(nodeToRemove.getInterval());
            if (extraCost < profitCurrent) {
                secondTree.insert(nodeToRemove);
                return;
            }
        }

        // Calculate the profits of each Node if you would delete them from the tree
        List<N> overlappingNodesSecond = secondTree.findAllOverlapping(nodeToRemove.getInterval());
        Map<N, Integer> profitMap = new TreeMap<>(Comparator.comparingInt(node -> -node.getInterval().getEndTime()));
        calculateProfitMap(secondTree, profitMap, overlappingNodesSecond);

        N mostProfitableNode = profitMap.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
        if (mostProfitableNode != null && mostProfitableNode.getWeight() + firstDeletedNode.getWeight() < inputReader.getServerCapacity()) {
            int profitSecond = profitMap.get(mostProfitableNode);
            int busyTimeNewServer = calculateBusyTimeForNewServer(nodeToRemove, mostProfitableNode);

            if (busyTimeNewServer < (profitCurrent + profitSecond)) {
                N deletedNode = secondTree.delete(mostProfitableNode);
//                secondTree.insert(deletedNode);
                setupNewServer(firstDeletedNode, deletedNode);
                return;
            }
        }

        // If no profit can be made, repair the tree
        currentTree.insert(nodeToRemove);
    }
    private void calculateProfitMap(T tree, Map<N, Integer> profitMap, List<N> allNodes) {
        for (N node : allNodes) {
            int busyTimeBefore = tree.calculateTotalBusyTime();
            N newNode = tree.delete(node);
            int profit = busyTimeBefore - tree.calculateTotalBusyTime();
            profitMap.put(newNode, profit);
            tree.insert(newNode);
        }
    }
    private int calculateBusyTimeForNewServer(N node1, N node2) {
        int startTime = Math.min(node1.getInterval().getStartTime(), node2.getInterval().getStartTime());
        int endTime = Math.max(node1.getInterval().getEndTime(), node2.getInterval().getEndTime());
        return endTime - startTime;
    }
    private void setupNewServer(N node1, N node2) {
        T newServer = intervalTreeFactory.create();
        newServer.insert(node1);
        newServer.insert(node2);
        currentSolution.getIntervalTrees().add(newServer);
    }

    private int calculateTotalBusyTime(Solution<T> solution) {
        int totalBusyTime = 0;
        for (T tree : solution.getIntervalTrees()) {
            totalBusyTime += tree.calculateTotalBusyTime();
        }
        return totalBusyTime;
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
