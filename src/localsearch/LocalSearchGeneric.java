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

//    /**
//     *
//     * @param iterations how many iterations do we perform
//     * @param nrOfTrees how many trees do i use to remove a node from
//     * @return
//     */
//    public LocalSearchResult run(int iterations, int nrOfTrees) {
//        long startTime = System.currentTimeMillis();
//        for (int i = 0; i < iterations; i++) {
//            System.out.println("Iteration " + i);
//            generateNeighbor(currentSolution, nrOfTrees);
//            int newBusyTime = calculateTotalBusyTime(currentSolution);
//
//            if (newBusyTime < bestBusyTime) {
//                bestBusyTime = newBusyTime;
//                bestSolution = new Solution<>(currentSolution);
//                if(deepCopyRollback) oldSolution = new Solution<>(currentSolution);
//                if (!deepCopyRollback) moves.clear();
//            } else {
//                if (deepCopyRollback) this.currentSolution = new Solution<>(oldSolution);
//                else rollback();
//            }
//        }
//
//        long endTime = System.currentTimeMillis();
//        long elapsedTime = endTime - startTime;
//        System.out.println("Elapsed time: "+ elapsedTime);
//
//        SolutionWriter.writeSolutionToFile(bestSolution, heuristic.getInputReader().getTestInstance(), heuristic.getHeuristicName(), bestBusyTime);
//
//        return new LocalSearchResult(elapsedTime, bestBusyTime);
//    }
    public LocalSearchResult run(int nrOfTrees) {
        int counter = 0;
        long startTime = System.currentTimeMillis();
        while (counter < 100000) {
            generateNeighbor(currentSolution, nrOfTrees);
            int newBusyTime = calculateTotalBusyTime(currentSolution);
            if (newBusyTime < bestBusyTime) {
                bestBusyTime = newBusyTime;
                bestSolution = new Solution<>(currentSolution);
                if(deepCopyRollback) oldSolution = new Solution<>(currentSolution);
                if (!deepCopyRollback) moves.clear();
                counter = 0;
            } else {
                counter++;
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

    private void reInsertNodes(List<Request> requests) {
        // Stel het aantal threads in voor de ForkJoinPool
      //  System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "3"); // set the number of threads

        for (Request request : requests) {
            Interval interval = new Interval(request.getStartTime(), request.getEndTime());
            IntervalNode node = new IntervalNode(interval, request.getWeight(), request.getVmId());

            // Use parallel streams to find the best tree for the node
            Optional<T> bestTree = this.currentSolution.getIntervalTrees().parallelStream()
                    .filter(intervalTree -> {
                        var overlappingNodes = intervalTree.findAllOverlapping(interval);
                        int sum = overlappingNodes.stream().mapToInt(IntervalNode::getWeight).sum();
                        return sum + request.getWeight() <= inputReader.getServerCapacity();
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
