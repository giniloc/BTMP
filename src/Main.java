import java.io.IOException;
import java.util.List;
import Heuristics.*;
import IO.*;
import Utils.*;


public class Main {
    public static void main(String[] args) {
        InputReader inputReader = new InputReader("n50 t50 LonLr/cap100_n50_t50_LonLr_1.txt");
        List<Request> requests = inputReader.getRequests();

        // Time BCHTRB heuristic
        System.out.println("\nRunning BCHTRB heuristic...");
        long startTimeBCHTRB = System.nanoTime();  // Start time for BCHTRB
        BCHTRB bchtrb = new BCHTRB(inputReader);
        bchtrb.applyHeuristic(requests);
        long endTimeBCHTRB = System.nanoTime();  // End time for BCHTRB
        long durationBCHTRB = (endTimeBCHTRB - startTimeBCHTRB) / 1_000_000;  // Convert to milliseconds
        System.out.println("BCHTRB Heuristic Execution Time: " + durationBCHTRB + " ms");

        // Time BCHT heuristic
//        System.out.println("\nRunning BCHT heuristic...");
//        long startTimeBCHT = System.nanoTime();  // Start time for BCHT
//        BCHT bcht = new BCHT(inputReader);
//        bcht.applyHeuristic(requests);
//        long endTimeBCHT = System.nanoTime();  // End time for BCHT
//        long durationBCHT = (endTimeBCHT - startTimeBCHT) / 1_000_000; // Convert to milliseconds
//        System.out.println("BCHT Heuristic Execution Time: " + durationBCHT + " ms");

//        Validator validator = new Validator();
//        validator.validate("TestInstances/n50 t50 LonLr/cap100_n50_t50_LonLr_1.txt",
//            "Solutions/n50_t50_LonLr_cap100_n50_t50_LonLr_1.txt_BCHT.txt");

    }
}
