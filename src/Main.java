import java.util.List;
import Heuristics.*;
import IO.*;
import Utils.*;

public class Main {
    public static void main(String[] args) {
        InputReader inputReader = new InputReader("n200 t240 ShSm/cap100_n200_t240_ShSm_4.txt");
        System.out.println("Number of VM requests: " + inputReader.getNumberOfVMRequests());
        System.out.println("Server capacity: " + inputReader.getServerCapacity());
        List<Request> requests = inputReader.getRequests();

        // Time BCHTRB heuristic
//        System.out.println("\nRunning BCHTRB heuristic...");
//        long startTimeBCHTRB = System.nanoTime();  // Start time for BCHTRB
//        BCHTRB bchtrb = new BCHTRB(inputReader);
//        bchtrb.applyHeuristic(requests);
//        long endTimeBCHTRB = System.nanoTime();  // End time for BCHTRB
//        long durationBCHTRB = (endTimeBCHTRB - startTimeBCHTRB) / 1_000_000;  // Convert to milliseconds
//        System.out.println("BCHTRB Heuristic Execution Time: " + durationBCHTRB + " ms");

        // Time BCHT heuristic
        System.out.println("\nRunning BCHT heuristic...");
        long startTimeBCHT = System.nanoTime();  // Start time for BCHT
        BCHT bcht = new BCHT(inputReader);
        bcht.applyHeuristic(requests);
        long endTimeBCHT = System.nanoTime();  // End time for BCHT
        long durationBCHT = (endTimeBCHT - startTimeBCHT) / 1_000_000; // Convert to milliseconds
        System.out.println("BCHT Heuristic Execution Time: " + durationBCHT + " ms");
    }
}
