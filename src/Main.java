
import java.util.List;
import Heuristics.*;
import IO.*;
import Utils.*;
import localsearch.LocalSearch;

public class Main {
    public static void main(String[] args) {
        InputReader inputReader = new InputReader("n50 t50 LonLr/cap100_n50_t50_LonLr_1.txt");
        List<Request> requests = inputReader.getRequests();

        var treeType = BalancedTreeType.BCHTAVL;//change this to BCHTRB or BCHTRB to test different tree types
        HeuristicRunner runner = new HeuristicRunner();
        IHeuristic bcht;
        Solution <AVLIntervalTree> solution = null;

        switch (treeType) {
            case BCHT:
                bcht = new BCHT<IntervalTree>(inputReader, new IntervalTreeFactory(), "BCHT");
                runner.run(bcht, requests);
                break;
            case BCHTRB:
                bcht = new BCHT<RBIntervalTree>(inputReader, new RBIntervalTreeFactory(), "BCHTRB");
                runner.run(bcht, requests);
                break;
            case BCHTAVL:
            default:
                bcht = new BCHT<AVLIntervalTree>(inputReader, new AVLIntervalTreeFactory(), "BCHTAVL");
              //  bcht = new BestCapacityHeuristic<AVLIntervalTree>(inputReader, new AVLIntervalTreeFactory(), "BCHTAVL");
                runner.run(bcht, requests);
                solution = bcht.getSolution();
                break;
        }
        LocalSearch localSearch = new LocalSearch(solution, (BCHT<AVLIntervalTree>) bcht);
        localSearch.run(10000000);

    }
}
