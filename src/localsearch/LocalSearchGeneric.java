package localsearch;
import Heuristics.*;
import Utils.*;
import IO.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class LocalSearchGeneric<T extends IIntervalTree<? extends IIntervalNode>> {
    private Solution<T> bestSolution;
    private Solution<T> currentSolution;
    private int bestBusyTime;
    private IHeuristic bchtHeuristic;
    public LocalSearchGeneric(Solution<T> initialSolution, IHeuristic bchtHeuristic) {
        this.bestSolution = new Solution<>(initialSolution);
        this.currentSolution = initialSolution;
        this.bestBusyTime = initialSolution.getTotalBusyTime();
        this.bchtHeuristic = bchtHeuristic;
    }

    public void run(int iterations) {
        for (int i = 0; i < iterations; i++) {
            System.out.println("Iteration " + i);
          //  byte[] snapshotData = serializeSolution(currentSolution);
            Solution<T> snapshot = new Solution<>(currentSolution);

            generateNeighbor(currentSolution);
            int newBusyTime = calculateTotalBusyTime(currentSolution);

            if (newBusyTime < bestBusyTime) {
                bestSolution = new Solution<>(bchtHeuristic.getSolution());//the new updated solution is built by the heuristic
                bestBusyTime = newBusyTime;
                System.out.println("New best solution found with " + bestBusyTime + " busy time");
            } else {
             //   currentSolution = deserializeSolution(snapshotData);
                currentSolution = snapshot;
            }
        }
        SolutionWriter.writeSolutionToFile(bestSolution, bchtHeuristic.getInputReader().getTestInstance(), bchtHeuristic.getHeuristicName(), bestBusyTime);

    }


    private void generateNeighbor(Solution<T> solution) {
        var selectedTrees = selectTrees(solution, 20);
        List<Request> requestList = new ArrayList<>();

        for (T tree : selectedTrees) {
            var randomNode = tree.getRandomNode();
            if (randomNode != null) {
                Request request = createRequestFromNode(randomNode);
                requestList.add(request);
                tree.delete((IntervalNode) randomNode);
                if (tree.getRoot() == null) {
                    solution.getIntervalTrees().remove(tree);
                }
            }
        }

        // Pas de heuristiek toe en werk de oplossing bij
        bchtHeuristic.applyHeuristic(requestList);
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

    public Solution<T> getBestSolution() {
        return bestSolution;
    }

    public int getBestBusyTime() {
        return bestBusyTime;
    }
    private byte[] serializeSolution(Solution<T> solution) {
        try (ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
             ObjectOutputStream objectStream = new ObjectOutputStream(byteStream)) {
            objectStream.writeObject(solution);
            return byteStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Error serializing solution", e);
        }
    }

    private Solution<T> deserializeSolution(byte[] data) {
        try (ByteArrayInputStream byteStream = new ByteArrayInputStream(data);
             ObjectInputStream objectStream = new ObjectInputStream(byteStream)) {
            return (Solution<T>) objectStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Error deserializing solution", e);
        }
    }
}
