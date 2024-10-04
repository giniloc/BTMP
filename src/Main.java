import java.util.List;
import Heuristics.*;

import IO.*;
import Utils.*;

public class Main {
    public static void main(String[] args) {
        InputReader inputReader = new InputReader("n50 t50 LonLr/cap100_n50_t50_LonLr_5.txt");
        System.out.println("Number of VM requests: " + inputReader.getNumberOfVMRequests());
        System.out.println("Server capacity: " + inputReader.getServerCapacity());
        List<Request> requests = inputReader.getRequests();
     //   BCHT bcht = new BCHT(inputReader);
     //   bcht.applyHeuristic(requests);
        BestCapacityHeuristic bestCapacityHeuristic = new BestCapacityHeuristic(inputReader);
        bestCapacityHeuristic.applyHeuristic(requests);

    }
}