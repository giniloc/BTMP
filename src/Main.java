
import java.util.List;
import Heuristics.*;
import IO.*;
import Utils.*;

public class Main {
    public static void main(String[] args) {
        InputReader inputReader = new InputReader("n50 t50 LonLr/cap100_n50_t50_LonLr_1.txt");
        List<Request> requests = inputReader.getRequests();

        var treeType = BalancedTreeType.BCHTAVL;//change this to BCHTRB or BCHTAVL to test different tree types
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
                break;
            case BCHTAVL:
            default:
                bcht = new BCHT<AVLIntervalTree>(inputReader, new AVLIntervalTreeFactory(), "BCHTAVL");
                runner.run(bcht, requests);
                break;
        }

//        Validator validator = new Validator();
//        validator.validate("TestInstances/n50 t50 LonLr/cap100_n50_t50_LonLr_1.txt",
//            "Solutions/n50_t50_LonLr_cap100_n50_t50_LonLr_1.txt_BCHT.txt");

    }
}
