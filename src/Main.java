
import java.util.List;
import Heuristics.*;
import IO.*;
import Utils.*;
import localsearch.*;

public class Main {
    public static void main(String[] args) {
        InputReader inputReader = new InputReader("d2/10000_inf_10.txt");
        List<Request> requests = inputReader.getRequests();

        var treeType = BalancedTreeType.BCHTAVL;//change this to BCHTRB or BCHTRB to test different tree types
        HeuristicRunner runner = new HeuristicRunner();
        IHeuristic bcht;

        switch (treeType) {
            case BCHT:
                bcht = new BCHT<IntervalTree>(inputReader, new IntervalTreeFactory(), "BCHT");
                runner.run(bcht, requests);
                break;
            case BCHTRB:
                bcht = new BCHT<RBIntervalTree>(inputReader, new RBIntervalTreeFactory(), "BCHTRB");
                runner.run(bcht, requests);
                var localSearch = new LocalSearchGeneric<RBIntervalTree>(bcht.getSolution(), bcht);
                localSearch.run(100000);
                break;
            case BCHTAVL:
            default:
                bcht = new BCHT<AVLIntervalTree>(inputReader, new AVLIntervalTreeFactory(), "BCHTAVL");
                //  bcht = new BestCapacityHeuristic<AVLIntervalTree>(inputReader, new AVLIntervalTreeFactory(), "BCHTAVL");
                runner.run(bcht, requests);
                var localSearchAvl = new LocalSearchGeneric<AVLIntervalTree>(bcht.getSolution(), bcht);
                localSearchAvl.run(100000);
                break;
        }
    }
}
