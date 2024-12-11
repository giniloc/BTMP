package localsearch;

import Heuristics.IHeuristic;
import IO.InputReader;
import IO.SolutionWriter;
import Utils.*;

import java.util.*;
import static Utils.Randomizer.random;


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
     * @return
     */
    public LocalSearchResult run(int iterations) {
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            generateNeighbor(currentSolution);
            int newBusyTime = calculateTotalBusyTime(currentSolution);

            if (newBusyTime < bestBusyTime) {
                System.out.println("New best solution found!" + newBusyTime);
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
        int iterationIndex = 0;
        long startTime = System.currentTimeMillis();
       // long maxDuration = 1800000; // 30 minutes in milliseconds

       // while ((System.currentTimeMillis()- startTime) < maxDuration) {
        while (counter < 1_000){
            generateNeighbor(currentSolution);
            int newBusyTime = calculateTotalBusyTime(currentSolution);

            if (newBusyTime < bestBusyTime) {
                System.out.println("New best solution found!" + newBusyTime);
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
              // SolutionWriter.solutionAnalysis(heuristic.getHeuristicName(),inputReader.getTestInstance(), iterationIndex, bestBusyTime);//This is for solution analysis
              // iterationIndex++;
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
//        mergeServers(solution);
    }


    private List<T> selectTrees(Solution<T> solution) {  //selects n busiest trees
        var intervalTrees = solution.getIntervalTrees();

        var busiestTrees = intervalTrees.stream()
                .sorted(Comparator.comparingInt(T::calculateTotalBusyTime).reversed())
                .toList();

        return busiestTrees;
    }

    private void makeCombination(List<T> allTrees) {
        // Select the two busiest trees
        List<T> busiestTrees = new ArrayList<>(allTrees.stream()
                .sorted(Comparator.comparingInt(T::calculateTotalBusyTime).reversed())
                .limit(2)
                .toList());

//        for (int i = 0; i < 10; i++) {
//            int randomIndex = random.nextInt(allTrees.size() - 1);
//            busiestTrees.add(allTrees.get(randomIndex));
//        }

        for (int i = 0; i < busiestTrees.size(); i++) {
            T currentTree = busiestTrees.get(i);
            N maxEndTimeNode = currentTree.getMaxEndTimeNode();
            N nodeToRemove = maxEndTimeNode;
            if (nodeToRemove == null) continue;
            int busyTimeCurrentBefore = currentTree.calculateTotalBusyTime();
            N firstDeletedNode = currentTree.delete(nodeToRemove);
            int profitCurrent = busyTimeCurrentBefore - currentTree.calculateTotalBusyTime();

            for (int j = 0; j < busiestTrees.size(); j++) {
                if (i == j) continue;

                T targetTree = busiestTrees.get(j);
                int busyTimeTargetBefore = targetTree.calculateTotalBusyTime();
                N nodeToRemoveSecond = targetTree.getMaxEndTimeNode();
                N secondDeletedNode = targetTree.delete(nodeToRemoveSecond);
                int profitTarget = busyTimeTargetBefore - targetTree.calculateTotalBusyTime();
                int smallestBegintime = Math.min(firstDeletedNode.getInterval().getStartTime(), secondDeletedNode.getInterval().getStartTime());
                int largestEndTime = Math.max(firstDeletedNode.getInterval().getEndTime(), secondDeletedNode.getInterval().getEndTime());
                if (profitTarget + profitCurrent > largestEndTime - smallestBegintime) {
                    setupNewServer(firstDeletedNode, secondDeletedNode);
                    break;
                } else {
                    currentTree.insert(firstDeletedNode);
                    targetTree.insert(secondDeletedNode);
                }
            }
        }
    }


    private void mergeServers(Solution<T> solution) {
        List<T> servers = solution.getIntervalTrees().stream()
                .sorted(Comparator.comparingInt(T::getNodeCount))
                .toList();

        for (int i = 0; i < servers.size(); i++) {
            T currentServer = servers.get(i);
            N maxEndTimeNode = currentServer.getMaxEndTimeNode();
            N nodeToRemove = maxEndTimeNode;
            if (nodeToRemove == null) continue;
            int busyTimeCurrent = currentServer.calculateTotalBusyTime();
            N firstDeletedNode = currentServer.delete(nodeToRemove);
            int profit = busyTimeCurrent - currentServer.calculateTotalBusyTime();
            for (int j = 0; j < servers.size(); j++) {
                if (i == j) continue;
                T targetServer = servers.get(j);
                int overlappingWeight = targetServer.findAllOverlapping(nodeToRemove.getInterval())
                        .stream()
                        .mapToInt(IntervalNode::getWeight)
                        .sum();
                if (overlappingWeight == 0) continue;
                if (overlappingWeight + nodeToRemove.getWeight() <= inputReader.getServerCapacity()) {
                    int extraCost = targetServer.calculateExtraBusyTime(nodeToRemove.getInterval());
                    if (extraCost < profit) {
                        targetServer.insert(nodeToRemove);
                        return;
                    }

                 }

            }
            currentServer.insert(nodeToRemove);
        }
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
//    private void swapNodes(List<T> allTrees) {
//        List<T> sortedTrees = allTrees.stream()
//                .sorted(Comparator.comparingInt(T::calculateTotalBusyTime).reversed())
//                .toList();
//        T busiestTree = sortedTrees.get(0);
//        T secondBusiestTree = sortedTrees.get(1);
//        N busiestNode = busiestTree.getMaxEndTimeNode();
//        List<N> overlappingNodesFirst = busiestTree.findAllOverlapping(busiestNode.getInterval());
//        List<N> overlappingNodesSecond = secondBusiestTree.findAllOverlapping(busiestNode.getInterval());
//
//        // Itereer door combinaties van nodes uit beide bomen
//        for (N node1 : overlappingNodesFirst) {
//            for (N node2 : overlappingNodesSecond) {
//                // Controleer capaciteiten na de swap
//                boolean fitsInFirst = (calculateTotalOverlappingWeight(busiestTree, node2) - node1.getWeight() + node2.getWeight())
//                        <= inputReader.getServerCapacity();
//                boolean fitsInSecond = (calculateTotalOverlappingWeight(secondBusiestTree, node1) - node2.getWeight() + node1.getWeight())
//                        <= inputReader.getServerCapacity();
//
//                if (fitsInFirst && fitsInSecond) {
//                    // Bereken huidige totale drukte
//                    int currentBusyTime = busiestTree.calculateTotalBusyTime() + secondBusiestTree.calculateTotalBusyTime();
//
//                    // Simuleer de swap
//                    N firstDeletion = busiestTree.delete(node1);
//                    N secondDeletion = secondBusiestTree.delete(node2);
//                    busiestTree.insert(secondDeletion);
//                    secondBusiestTree.insert(firstDeletion);
//
//                    // Bereken nieuwe totale drukte
//                    int newBusyTime = busiestTree.calculateTotalBusyTime() + secondBusiestTree.calculateTotalBusyTime();
//
//                    // Controleer of de swap winst oplevert
//                    if (newBusyTime < currentBusyTime) {
//                        // Swap is winstgevend; stop verdere checks
//                        System.out.println("Swap is winstgevend!");
//                        return;
//                    } else {
//                        // Rollback als de swap geen winst oplevert
//                        busiestTree.delete(secondDeletion);
//                        secondBusiestTree.delete(firstDeletion);
//                        busiestTree.insert(firstDeletion);
//                        secondBusiestTree.insert(secondDeletion);
//                    }
//                }
//            }
//        }
//    }
    private void swapNodes(List<T> allTrees) {
        List<T> sortedTrees = allTrees.stream()
                .sorted(Comparator.comparingInt(T::calculateTotalBusyTime).reversed())
                .toList();

        T busiestTree = sortedTrees.get(0);
        T secondBusiestTree = sortedTrees.get(random.nextInt(sortedTrees.size() - 1));

        List<N> nodesFirstTree = busiestTree.findAllOverlapping(new Interval(0, 999999999));
        List<N> nodesSecondTree = secondBusiestTree.findAllOverlapping(new Interval(0, 999999999));

        for (N node1 : nodesFirstTree) {
            for (N node2 : nodesSecondTree) {
                if (node2.getID() == node1.getID()) {
                    continue;
                }
                int currentBusyTime = busiestTree.calculateTotalBusyTime() + secondBusiestTree.calculateTotalBusyTime();

                N deletedNodeFirst = busiestTree.delete(node1);
                N deletedNodeSecond = secondBusiestTree.delete(node2);

                busiestTree.insert(deletedNodeSecond);
                secondBusiestTree.insert(deletedNodeFirst);

                int newBusyTime = busiestTree.calculateTotalBusyTime() + secondBusiestTree.calculateTotalBusyTime();

                if (newBusyTime < currentBusyTime && calculateTotalOverlappingWeight(busiestTree, deletedNodeSecond)<= inputReader.getServerCapacity()
                        && calculateTotalOverlappingWeight(secondBusiestTree, deletedNodeFirst)<= inputReader.getServerCapacity()) {
                    System.out.println("Winstgevende swap gevonden!");
                    return;
                } else {
                    N recoveryNodeFirst = busiestTree.delete(deletedNodeSecond);
                    N recoveryNodeSecond = secondBusiestTree.delete(deletedNodeFirst);

                    busiestTree.insert(recoveryNodeSecond);
                    secondBusiestTree.insert(recoveryNodeFirst);
                }
            }
        }
    }


    private int calculateTotalOverlappingWeight(T tree, N node) {
        return tree.findAllOverlapping(node.getInterval())
                .stream()
                .mapToInt(IntervalNode::getWeight)
                .sum();
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
