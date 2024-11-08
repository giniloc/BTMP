
import java.util.List;
import Heuristics.*;
import IO.*;
import Utils.*;
import localsearch.*;

public class Main {
    public static void main(String[] args) {
        InputReader inputReader = new InputReader("n50 t50 LonLr/cap100_n50_t50_LonLr_1.txt");
        List<Request> requests = inputReader.getRequests();

    var treeType = BalancedTreeType.BCHTRB;//change this to BCHTRB or BCHTAVL to test different tree types
        HeuristicRunner runner = new HeuristicRunner();
        IHeuristic bcht;

        switch (treeType) {
            case BCHT:
                bcht = new BCHT<IntervalTree>(inputReader, new IntervalTreeFactory(), "BCHT");
                runner.run(bcht, requests);
                var localSearchBCHT = new LocalSearchGeneric<IntervalTree, IntervalNode>(bcht.getSolution(), bcht);
                localSearchBCHT.run(100000);
                break;
            case BCHTRB:
                bcht = new BCHT<RBIntervalTree>(inputReader, new RBIntervalTreeFactory(), "BCHTRB");
                runner.run(bcht, requests);
                var localSearchRB = new LocalSearchGeneric<RBIntervalTree, RBIntervalNode>(bcht.getSolution(), bcht);
                localSearchRB.run(100000);
                break;
            case BCHTAVL:
            default:
                bcht = new BCHT<AVLIntervalTree>(inputReader, new AVLIntervalTreeFactory(), "BCHTAVL");
                //  bcht = new BestCapacityHeuristic<AVLIntervalTree>(inputReader, new AVLIntervalTreeFactory(), "BCHTAVL");
                runner.run(bcht, requests);
                var localSearchAVL = new LocalSearchGeneric<AVLIntervalTree, AVLIntervalNode>(bcht.getSolution(), bcht);
                localSearchAVL.run(1000);
                break;
        }
    }
}
