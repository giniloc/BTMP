package localsearch;
import Heuristics.*;
import Utils.*;
import IO.*;

import java.util.*;

import static Utils.Randomizer.random;

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
    private InputReader inputReader;

    // TODO We need to inject a RollBack strategy instead of using a boolean.
    private boolean deepCopyRollback;

    public LocalSearchGeneric(Solution<T> initialSolution, IHeuristic heuristic, boolean deepCopyRollback, InputReader inputReader) {
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
            System.out.println("Iteration " + i);
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


    /**
     * generate neighbor
     * @param solution solution containing a tree per server
     * @param nrOfTrees how many trees do we use to remove a node from
     */
    private void generateNeighbor(Solution<T> solution, int nrOfTrees) {
        var selectedTrees = selectTrees(solution, nrOfTrees);
        List<Request> requestList = new ArrayList<>();

        for (T tree : selectedTrees) {
            var randomNode = tree.getRandomNode();

            if (randomNode != null) {
                Request request = createRequestFromNode(randomNode);
                requestList.add(request);

                var deletedNode = tree.delete(randomNode);

                if (!deepCopyRollback){
                    var deleteMove = new Move<N>(true, tree, deletedNode);
                    moves.add(deleteMove);
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
        Collections.shuffle(randomTrees, random);
        var selectedRandomTrees = randomTrees.stream().limit(count - halfCount).toList();

        var selectedTrees = new ArrayList<>(busiestTrees);
        selectedTrees.addAll(selectedRandomTrees);

        return selectedTrees;
    }

    private void reInsertNodes(List<Request> requests){
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
                if (sum + request.getWeight() <= inputReader.getServerCapacity()) {
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

            if (!deepCopyRollback){
                Move insertMove = new Move(false, bestTree, node);
                moves.add(insertMove);
            }

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
        for (int i = moves.size() - 1; i >= 0; i--) {
            var move = moves.get(i);
            if (move.isDelete()) {
                move.getTree().insert(move.getNode());
//                if (move.getTree().findNode(move.getNode()) == null) {
//                    System.out.println("Node not inserted");
//                }
            } else {
                var node = move.getNode();
                move.getTree().delete(node);
//                if (move.getTree().findNode(move.getNode()) != null) {
//                    System.out.println("Node not deleted");
//                }
//                if (move.getTree().getRoot() == null) {
//                    currentSolution.getIntervalTrees().remove(move.getTree());
//                }
            }
        }
        moves.clear();
    }
}
