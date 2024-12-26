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
        boolean improvementFound = false;
        for (int i = 0; i < allTrees.size(); i++) {
            if (improvementFound) break; // Stop als een verbetering is gevonden

            T currentTree = allTrees.get(i);
            N randomNode = currentTree.getRandomNode();
            if (randomNode == null) continue;

            N deletedNode = currentTree.delete(randomNode);

            if (deletedNode != null) {
                improvementFound = ejectionChaining(currentTree, deletedNode, 0);

                // Controleer of de huidige oplossing beter is dan de beste oplossing
                int newBusyTime = calculateTotalBusyTime(currentSolution);
                if (newBusyTime < bestBusyTime) {
                    bestBusyTime = newBusyTime;
                    bestSolution = new Solution<>(currentSolution);
                    System.out.println("Nieuwe beste oplossing gevonden! " + newBusyTime);
                    improvementFound = true; // Stop verdere iteraties
                }

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
    private boolean ejectionChaining(T sourceTree, N node, int depth) {
        if (depth > 1000) {
            sourceTree.insert(node);
            return false;
        }

        for (T targetTree : currentSolution.getIntervalTrees()) {
            if (sourceTree == targetTree) continue;

            int overlappingWeight = targetTree.findAllOverlapping(node.getInterval())
                    .stream()
                    .mapToInt(IntervalNode::getWeight)
                    .sum();

            if (overlappingWeight + node.getWeight() <= inputReader.getServerCapacity() && overlappingWeight != 0) {
                    N ejectedNode = insertWithEjection(sourceTree, targetTree, node);
                    if (ejectedNode == null) {
                        return true;
                    } else {
                        return ejectionChaining(targetTree, ejectedNode, depth + 1);
                }
            }
        }
        sourceTree.insert(node);
        return false;
    }



    public N insertWithEjection(T sourcetree, T targetTree, N newNode) { //TODO check this method for design flaws
        List<N> overlappingNodes = targetTree.findAllOverlapping(newNode.getInterval());

        int totalWeight = newNode.getWeight();
        for (N node : overlappingNodes) {
            totalWeight += node.getWeight();
        }

        if (totalWeight <= inputReader.getServerCapacity() && !overlappingNodes.isEmpty()) {
            targetTree.insert(newNode);
        }


        N nodeToEject = null;
        for (N node : overlappingNodes) {
            if (totalWeight - node.getWeight() <= inputReader.getServerCapacity()) {
                nodeToEject = node;
                break;
            }
        }
        if (nodeToEject != null) {
            N deletednode = targetTree.delete(nodeToEject);
            targetTree.insert(newNode);
            return deletednode;
        }

        return newNode;
    }

}