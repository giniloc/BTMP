import Heuristics.IHeuristic;
import Utils.Request;

import java.util.List;

public class HeuristicRunner {
    public void run(IHeuristic heuristic, List<Request> requests) {
        System.out.println("\nRunning " + heuristic.getHeuristicName() + " heuristic...");
        long startTimeBCHTRB = System.nanoTime();  // Start time for BCHT
        heuristic.applyHeuristic(requests);
        long endTimeBCHTRB = System.nanoTime();  // End time for BCHT
        long durationBCHTRB = (endTimeBCHTRB - startTimeBCHTRB) / 1_000_000;  // Convert to milliseconds
        System.out.println(heuristic.getHeuristicName() + " Heuristic Execution Time: " + durationBCHTRB + " ms");
    }
}
