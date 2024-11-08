package localsearch;
import Heuristics.*;
import Utils.*;
import IO.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
public class LocalSearchGeneric<
        T extends IIntervalTree<N>,
        N extends IntervalNode
        > {
    private Solution<T> bestSolution;
    private Solution<T> currentSolution, oldSolution;
    private int bestBusyTime;
    private IHeuristic heuristic;
    private List<Move<N>> moves;
    private IIntervalTreeFactory<T> intervalTreeFactory;
    public LocalSearchGeneric(Solution<T> initialSolution, IHeuristic heuristic) {
        this.bestSolution = new Solution<>(initialSolution);
        this.currentSolution = new Solution<>(initialSolution);
        this.oldSolution = new Solution<>(initialSolution);
        this.bestBusyTime = initialSolution.getTotalBusyTime();
        this.moves = new ArrayList<>();
        this.heuristic = heuristic;
        this.intervalTreeFactory = heuristic.getFactory();
    }

    public void run(int iterations) {
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            System.out.println("Iteration " + i);
            generateNeighbor(currentSolution);
            int newBusyTime = calculateTotalBusyTime(currentSolution);

            if (newBusyTime < bestBusyTime) {
                System.out.println("New best solution found!");
                bestBusyTime = newBusyTime;
                bestSolution = new Solution<>(currentSolution);
            } else {
              this.currentSolution = new Solution<>(oldSolution);
             //   rollback();
            }
        }
        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;
        System.out.println("Elapsed time: "+ elapsedTime);
        SolutionWriter.writeSolutionToFile(bestSolution, heuristic.getInputReader().getTestInstance(), heuristic.getHeuristicName(), bestBusyTime);
    }


    private void generateNeighbor(Solution<T> solution) {
        var selectedTrees = selectTrees(solution, 10);
        List<Request> requestList = new ArrayList<>();

        for (T tree : selectedTrees) {
            var randomNode = tree.getRandomNode();
            if (randomNode != null) {
                Request request = createRequestFromNode(randomNode);
                requestList.add(request);
                var deleteMove = new Move<N>(true, tree, randomNode);
                moves.add(deleteMove);
                tree.delete(randomNode);
                if (tree.getRoot() == null) {
                    solution.getIntervalTrees().remove(tree);
                }
            }
        }
        reInsertNodes(requestList);
    }


    private int calculateTotalBusyTime(Solution<T> solution) {
        return solution.getIntervalTrees().stream()
                .mapToInt(T::calculateTotalBusyTime)
                .sum();
    }

    private Request createRequestFromNode(IIntervalNode node) {
        return new Request(node.getID(), node.getInterval().getStartTime(), node.getInterval().getEndTime(), node.getWeight());
    }

    private List<T> selectTrees(Solution<T> solution, int count) {
        var intervalTrees = solution.getIntervalTrees();
        int halfCount = count / 2;

        var busiestTrees = intervalTrees.stream()
                .sorted(Comparator.comparingInt(T::calculateTotalBusyTime).reversed())
                .limit(halfCount)
                .toList();

        var randomTrees = new ArrayList<>(intervalTrees);
        randomTrees.removeAll(busiestTrees);
        Collections.shuffle(randomTrees);
        var selectedRandomTrees = randomTrees.stream().limit(count - halfCount).toList();

        var selectedTrees = new ArrayList<>(busiestTrees);
        selectedTrees.addAll(selectedRandomTrees);

        return selectedTrees;
    }
    public void reInsertNodes(List<Request> requests){
        for (Request request : requests) {
            Interval interval = new Interval(request.getStartTime(), request.getEndTime());
            IntervalNode node = new IntervalNode(interval, request.getWeight(), request.getVmId());

            T bestTree = null;

            for (var intervalTree : this.currentSolution.getIntervalTrees()) {
                var overlappingNodes = intervalTree.findAllOverlapping(interval);
                int sum = 0;
                for (var overlappingNode : overlappingNodes) {
                    sum += overlappingNode.getWeight();
                }

                // Check if server has enough capacity for request
                if (sum + request.getWeight() <= 100) {
                    // search for server with least extra busy time
                    if (bestTree == null || intervalTree.calculateExtraBusyTime(interval) < bestTree.calculateExtraBusyTime(interval)) {
                        bestTree = intervalTree;
                    }
                }

            }

            // if no bestTree was found, create a new one
            if (bestTree == null) {
                bestTree = intervalTreeFactory.create(); //new T();
                currentSolution.add(bestTree);
            }
            Move insertMove = new Move(false, bestTree, node);
            moves.add(insertMove);
            bestTree.insert(node);
        }
    }

    public Solution<T> getBestSolution() {
        return bestSolution;
    }

    public int getBestBusyTime() {
        return bestBusyTime;
    }

    private void rollback() {
        for (var move : moves) {
            if (move.isDelete()) {
                move.getTree().insert(move.getNode());
            } else {
                var n = move.getNode();
                move.getTree().delete(n);
                if (move.getTree().getRoot() == null) {
                    currentSolution.getIntervalTrees().remove(move.getTree());
                }
            }
        }
        moves.clear();
    }

}
