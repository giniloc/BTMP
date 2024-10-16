
import java.util.List;
import Heuristics.*;
import IO.*;
import Utils.*;

public class Main {
    public static void main(String[] args) {
//        InputReader inputReader = new InputReader("n200 t240 ShSm/cap100_n200_t240_ShSm_4.txt");
//        List<Request> requests = inputReader.getRequests();
//
//        var treeType = BalancedTreeType.BCHTAVL;//change this to BCHTRB or BCHTAVL to test different tree types
//        HeuristicRunner runner = new HeuristicRunner();
//        IHeuristic bcht;
//
//        switch (treeType) {
//            case BCHT:
//                bcht = new BCHT<IntervalTree>(inputReader, new IntervalTreeFactory(), "BCHT");
//                runner.run(bcht, requests);
//                break;
//            case BCHTRB:
//                bcht = new BCHT<RBIntervalTree>(inputReader, new RBIntervalTreeFactory(), "BCHTRB");
//                runner.run(bcht, requests);
//                break;
//            case BCHTAVL:
//            default:
//                bcht = new BCHT<AVLIntervalTree>(inputReader, new AVLIntervalTreeFactory(), "BCHTAVL");
//              //  bcht = new BestCapacityHeuristic<AVLIntervalTree>(inputReader, new AVLIntervalTreeFactory(), "BCHTAVL");
//                runner.run(bcht, requests);
//                break;
//        }
        // Maak een nieuwe IntervalTree aan
        IntervalTree intervalTree = new IntervalTree();

        // Voeg een paar intervalnodes toe (niet gesorteerd)
        IntervalNode node1 = new IntervalNode(new Interval(20, 25), 10, 1);
        IntervalNode node2 = new IntervalNode(new Interval(10, 15), 15, 2);
        IntervalNode node3 = new IntervalNode(new Interval(30, 35), 20, 3);
        IntervalNode node4 = new IntervalNode(new Interval(5, 8), 25, 4);
        IntervalNode node5 = new IntervalNode(new Interval(15, 18), 30, 5);
        IntervalNode node6 = new IntervalNode(new Interval(25, 38), 35, 6);

        // Insert nodes in random order
        intervalTree.insert(node1);
        intervalTree.insert(node2);
        intervalTree.insert(node3);
        intervalTree.insert(node4);
        intervalTree.insert(node5);
        intervalTree.insert(node6);

        intervalTree.delete(node2);


    }
}
