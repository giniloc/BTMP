package Heuristics;
import java.util.List;
import Utils.*;
import IO.*;

public class BCHTRB {
    private InputReader inputReader;
    private RBSolution solution;

    public BCHTRB(InputReader inputReader) {
        this.inputReader = inputReader;
        this.solution = new RBSolution();
    }

    public void applyHeuristic(List<Request> requests) {
        for (Request request : requests) {
            Interval interval = new Interval(request.getStartTime(), request.getEndTime());
            RBIntervalNode node = new RBIntervalNode(interval, request.getWeight(), request.getVmId(), Color.RED);
            RBIntervalTree bestTree = null;

            for (RBIntervalTree intervalTree : solution.getIntervalTrees()) {
                List<RBIntervalNode> overlappingNodes = intervalTree.findAllOverlapping(interval);
                int sum = 0;
                for (IntervalNode overlappingNode : overlappingNodes) {
                    sum += overlappingNode.getWeight();
                }


                if (sum + request.getWeight() <= inputReader.getServerCapacity()) {
                    if (bestTree == null ||
                            intervalTree.calculateExtraBusyTime(interval) < bestTree.calculateExtraBusyTime(interval)) {
                        bestTree = intervalTree;
                    }
                }
            }

            // Als er geen geschikte boom is gevonden, maak een nieuwe aan
            if (bestTree == null) {
                bestTree = new RBIntervalTree();  // Gebruik RBIntervalTree
                solution.add(bestTree);
            }

            // Voeg de nieuwe node toe aan de beste boom
            bestTree.insert(node);  // Invoegen via de RBIntervalTree insert-methode
        }

        // Bereken de totale bezettingstijd (busy time)
        int totalBusyTime = 0;
        int counter = 0;
        for (RBIntervalTree intervalTree : solution.getIntervalTrees()) {
            totalBusyTime += intervalTree.calculateTotalBusyTime();
            counter++;
        }

        // Schrijf de oplossing naar een bestand
        SolutionWriter.writeSolutionToFile(solution, inputReader.getTestInstance(), "BCHT", totalBusyTime);
    }
}
