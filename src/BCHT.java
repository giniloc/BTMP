import java.util.ArrayList;
import java.util.List;

public class BCHT {
    private InputReader inputReader;
    private List<IntervalTree> intervalTrees;  // Lijst van servers

    public BCHT(InputReader inputReader) {
        this.inputReader = inputReader;
        this.intervalTrees = new ArrayList<>();
    }

    public void applyHeuristic(List<Request> requests) {
        for (Request request : requests) {
            Interval interval = new Interval(request.getStartTime(), request.getEndTime());
            IntervalNode node = new IntervalNode(interval, request.getWeight());

            // Zoek naar de beste server (IntervalTree) om de nieuwe request in te plannen
            IntervalTree bestTree = null;

            for (IntervalTree intervalTree : intervalTrees) {
                List<IntervalNode> overlappingNodes = intervalTree.findAllOverlapping(intervalTree.getRoot(), interval);
                int sum = 0;
                for (IntervalNode overlappingNode : overlappingNodes) {
                    sum += overlappingNode.getWeight();
                }

                // Check of de server capaciteit heeft voor deze request
                if (sum + request.getWeight() <= inputReader.getServerCapacity()) {
                    // Zoek de server met de minste 'extra busy time' als deze request daar wordt toegevoegd
                    if (bestTree == null ||
                            intervalTree.calculateExtraBusyTime(interval) < bestTree.calculateExtraBusyTime(interval)) {
                        bestTree = intervalTree;
                    }
                }
            }

            // Als geen bestaande server geschikt is, voeg een nieuwe server toe als beste server
            if (bestTree == null) {
                bestTree = new IntervalTree();
                intervalTrees.add(bestTree);
            }

            // Voeg de nieuwe request toe aan de gekozen server (bestTree), of de nieuwe server die net is aangemaakt
            bestTree.setRoot(bestTree.insert(bestTree.getRoot(), node));
        }
        // Print tree in order
        for (IntervalTree intervalTree : intervalTrees) {
            System.out.println("Server: " + intervalTrees.indexOf(intervalTree));
            IntervalTree.inOrder(intervalTree.getRoot());
        }
    }
}
