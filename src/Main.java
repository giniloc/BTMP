import java.util.List;
import Heuristics.BCHT;

import IO.*;
import Utils.*;

public class Main {
    public static void main(String[] args) {
        InputReader inputReader = new InputReader("n200 t240 ShSm/cap100_n200_t240_ShSm_5.txt");
        System.out.println("Number of VM requests: " + inputReader.getNumberOfVMRequests());
        System.out.println("Server capacity: " + inputReader.getServerCapacity());
        List<Request> requests = inputReader.getRequests();
        BCHT bcht = new BCHT(inputReader);
        bcht.applyHeuristic(requests);

    }
}