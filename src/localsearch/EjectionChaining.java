package localsearch;

import Heuristics.IHeuristic;
import IO.InputReader;
import IO.SolutionWriter;
import Utils.*;

import java.util.*;
import static Utils.Randomizer.random;


public class EjectionChaining <T extends IIntervalTree<N>,N extends IntervalNode> implements ILocalSearch {
    private Solution<T> bestSolution;
    private Solution<T> currentSolution, oldSolution;
    private int bestBusyTime;
    private IHeuristic heuristic;
    private List<Move<N>> moves;
    private IIntervalTreeFactory<T> intervalTreeFactory;
    private InputReader inputReader;
    private boolean deepCopyRollback;

    public EjectionChaining(Solution<T> initialSolution, IHeuristic heuristic, boolean deepCopyRollback, InputReader inputReader) {
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
        // Parameters voor Simulated Annealing
        double initialTemperature = 10_000; // Starttemperatuur
        double coolingRate = 0.999;         // Hoe snel de temperatuur afkoelt
        double temperature = initialTemperature;
        int iterationIndex = 0;

        long startTime = System.currentTimeMillis();
        int maxIterations = 100_000;        // Maximaal aantal iteraties
        System.out.println("Initial busy time: " + bestBusyTime);

        while (iterationIndex < maxIterations && temperature > 1e-3) { // Stop als de temperatuur laag is of max iteraties bereikt
            generateNeighbor(currentSolution);
            int newBusyTime = calculateTotalBusyTime(currentSolution);
            int delta = newBusyTime - bestBusyTime;

            if (delta < 0 || shouldAccept(delta, temperature)) {
                // Accepteer de nieuwe oplossing
                if (newBusyTime < bestBusyTime) {
                    System.out.println("New best solution found! " + newBusyTime);
                    bestBusyTime = newBusyTime;
                    bestSolution = new Solution<>(currentSolution);
                }
                if (deepCopyRollback) {
                    oldSolution = new Solution<>(currentSolution);
                } else {
                    moves.clear();
                }
            } else {
                if (deepCopyRollback) {
                    this.currentSolution = new Solution<>(oldSolution);
                } else {
                    rollback();
                }
            }

            // Verlaag de temperatuur en verhoog de iteratie-index
            temperature *= coolingRate;
            iterationIndex++;
        }

        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;

        System.out.println("Elapsed time: " + elapsedTime);

        SolutionWriter.writeSolutionToFile(bestSolution, heuristic.getInputReader().getTestInstance(), heuristic.getHeuristicName(), bestBusyTime);

        return new LocalSearchResult(elapsedTime, bestBusyTime);
    }
    private boolean shouldAccept(int delta, double temperature) {
        // Bereken de acceptatiekans
        double probability = Math.exp(-delta / temperature);
        // Vergelijk met een willekeurige waarde
        return random.nextDouble() < probability;
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

    private void makeCombination(List<T> allTrees) {
        for (int i = 0; i < allTrees.size(); i++) {
            T currentTree = allTrees.get(random.nextInt(allTrees.size()));
            N randomNode = currentTree.getRandomNode();
            if (randomNode == null) continue;
            N deletedNode = currentTree.delete(randomNode);
            if (deletedNode != null) {
               ejectionChaining(currentTree, deletedNode);
            }
        }
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
    private void ejectionChaining(T sourceTree, N node) {
        for (T targetTree : currentSolution.getIntervalTrees()) {
            if (sourceTree == targetTree) continue;
            List<N> overlappingNodes = targetTree.findAllOverlapping(node.getInterval());
            int overlapWeight = 0;
            for (N overlappingNode : overlappingNodes) {
                overlapWeight += overlappingNode.getWeight();
            }
            if (overlapWeight + node.getWeight() <= inputReader.getServerCapacity()) {//if the node can be inserted in the target tree, do it
                targetTree.insert(node);
                return;
            }
            N ejectedNode = insertWithEjection(sourceTree, targetTree, node, overlapWeight);
            int newbusyTime = currentSolution.getTotalBusyTime();
            if (newbusyTime < bestBusyTime) {
                System.out.println("New best solution found! " + newbusyTime);
                bestBusyTime = newbusyTime;
                bestSolution = new Solution<>(currentSolution);
                return;
            }
            if (ejectedNode == null) continue;
            if (ejectedNode.getID() == node.getID()) {
                sourceTree.insert(ejectedNode);
            }
            else {
                ejectionChaining(targetTree, ejectedNode);
            }
        }
    }


    private N insertWithEjection(T sourceTree, T targetTree, N newNode, int overlapWeight) {//the overlapweight is from the target tree
        List<N> overlappingNodes = targetTree.findAllOverlapping(newNode.getInterval());
        N mostOverlappingNode = findMostOverlappingNode(overlappingNodes, newNode);
        if (mostOverlappingNode == null) {
            targetTree.insert(newNode);
            return null;
        }
        List<N> overlappingNodesSource = sourceTree.findAllOverlapping(mostOverlappingNode.getInterval());
        int overlapWeightSource = 0;
        for (N overlappingNode : overlappingNodesSource) {
            overlapWeightSource += overlappingNode.getWeight();
        }
        if (overlapWeightSource + newNode.getWeight() + mostOverlappingNode.getWeight() <= inputReader.getServerCapacity()
                && calculateOverlap(newNode, mostOverlappingNode) <= calculateOverlap(mostOverlappingNode, findMostOverlappingNode(overlappingNodes, mostOverlappingNode))) {
            sourceTree.insert(newNode);
            N deletedNode = targetTree.delete(mostOverlappingNode);
            sourceTree.insert(deletedNode);
            return null;
        }
        if(overlapWeight - mostOverlappingNode.getWeight() + newNode.getWeight() <= inputReader.getServerCapacity()) {
            targetTree.insert(newNode);
            return mostOverlappingNode;
        }
        return newNode;
}

    private N findMostOverlappingNode(List<N> overlappingNodes, N newNode) {
        N mostOverlappingNode = null;
        int maxOverlap = 0;
        for (N node : overlappingNodes) {
            int overlap = calculateOverlap(node, newNode);
            if (overlap > maxOverlap) {
                maxOverlap = overlap;
                mostOverlappingNode = node;
            }
        }
        return mostOverlappingNode;
    }

    private int calculateOverlap(N node, N newNode) {
        if (node.getInterval().getEndTime() <= newNode.getInterval().getStartTime() ||
                node.getInterval().getStartTime() >= newNode.getInterval().getEndTime()) {
            return 0;
        }
        else {
            return Math.min(node.getInterval().getEndTime(), newNode.getInterval().getEndTime()) -
                    Math.max(node.getInterval().getStartTime(), newNode.getInterval().getStartTime());
        }
    }
}