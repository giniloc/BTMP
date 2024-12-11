package localsearch;
import Heuristics.*;
import Utils.*;
import IO.*;
import java.util.*;
import java.util.Optional;
import static Utils.Randomizer.random;

public class LocalSearchGeneric<
        T extends IIntervalTree<N>,
        N extends IntervalNode
        > implements ILocalSearch {
    private Solution<T> bestSolution;
    private Solution<T> currentSolution, oldSolution;
    private int bestBusyTime;
    private IHeuristic heuristic;
    private List<Move<N>> moves;
    private IIntervalTreeFactory<T> intervalTreeFactory;
    private InputReader inputReader;
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

    public LocalSearchResult run(int nrOfTrees) {
        long startTime = System.currentTimeMillis();
        long maxDuration = 180_000; // 30 minutes in milliseconds

        // Simulated Annealing parameters
        double initialTemperature = 10000.0;
        double finalTemperature = 1.0;
        double coolingRate = 0.9999; // Cooling factor, adjust for faster/slower cooling
        double temperature = initialTemperature;

        while ((System.currentTimeMillis() - startTime) < maxDuration && temperature > finalTemperature) {
            generateNeighbor(currentSolution, nrOfTrees);
            int newBusyTime = calculateTotalBusyTime(currentSolution);

            // Calculate the change in busy time
            int delta = newBusyTime - currentSolution.getTotalBusyTime();

            if (delta < 0 || acceptWorseSolution(delta, temperature)) {
                if (newBusyTime < bestBusyTime) {
                    bestBusyTime = newBusyTime;
                    bestSolution = new Solution<>(currentSolution);
                    System.out.println("New best solution found!");
                }
                if (deepCopyRollback) {
                    oldSolution = new Solution<>(currentSolution);
                }
                if (!deepCopyRollback) {
                    moves.clear();
                }
            } else {
                if (deepCopyRollback) {
                    currentSolution = new Solution<>(oldSolution);
                } else {
                    rollback();
                }
            }


            // Reduce the temperature
            temperature *= coolingRate;
        }

        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;

        System.out.println("Elapsed time: " + elapsedTime);

        SolutionWriter.writeSolutionToFile(bestSolution, heuristic.getInputReader().getTestInstance(), heuristic.getHeuristicName(), bestBusyTime);

        return new LocalSearchResult(elapsedTime, bestBusyTime);
    }

    private boolean acceptWorseSolution(int delta, double temperature) {
        // Calculate the acceptance probability
        double probability = Math.exp(-delta / temperature);
        // Accept the worse solution with this probability
        return Math.random() < probability;
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
            int nodesToRemove = Math.max(1, tree.getNodeCount() / 4);
            List<N> nodesToDelete = tree.getRandomNodes(nodesToRemove);
            Collections.shuffle(nodesToDelete, random);

            for (N node : nodesToDelete) {
                Request request = createRequestFromNode(node);
                requestList.add(request);

                var deletedNode = tree.delete(node);

                if (!deepCopyRollback) {
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
        var selectedRandomTrees = randomTrees.stream().limit(solution.getIntervalTrees().size()/2).toList();

        var selectedTrees = new ArrayList<>(busiestTrees);
        selectedTrees.addAll(selectedRandomTrees);

        return selectedTrees;
    }

    private void reInsertNodes(List<Request> requests) {
      //  System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "3"); // set the number of threads

        for (Request request : requests) {
            Interval interval = new Interval(request.getStartTime(), request.getEndTime());
            IntervalNode node = new IntervalNode(interval, request.getWeight(), request.getVmId());

            // Use parallel streams to find the best tree for the node
            Optional<T> bestTree = this.currentSolution.getIntervalTrees().parallelStream()
                    .filter(intervalTree -> {
                        var overlappingNodes = intervalTree.findAllOverlapping(interval);
                        int sum = overlappingNodes.stream().mapToInt(IntervalNode::getWeight).sum();
                        return sum + request.getWeight() <= inputReader.getServerCapacity() && sum != 0;
                    })
                    .reduce((currentBest, intervalTree) -> {
                        if (currentBest == null) {
                            return intervalTree;
                        }
                        return (intervalTree.calculateExtraBusyTime(interval) < currentBest.calculateExtraBusyTime(interval))
                                ? intervalTree : currentBest;
                    });
            // If no tree is found, create a new tree and add it to the solution
            T finalBestTree = bestTree.orElseGet(() -> {
                T newTree = intervalTreeFactory.create();
                currentSolution.add(newTree);
                return newTree;
            });

            if (!deepCopyRollback) {
                Move insertMove = new Move(false, finalBestTree, node);
                moves.add(insertMove);
            }
            finalBestTree.insert(node);
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
            } else {
                var node = move.getNode();
                move.getTree().delete(node);
            }
        }
        moves.clear();
    }
}
