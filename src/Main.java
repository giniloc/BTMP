
import java.util.List;
import Heuristics.*;
import IO.*;
import Utils.*;

public class Main {
    public static void main(String[] args) {
//        InputReader inputReader = new InputReader("n200 t240 ShSm/cap100_n200_t240_ShSm_4.txt");
//        List<Request> requests = inputReader.getRequests();
//
//        var treeType = BalancedTreeType.BCHTAVL;//change this to BCHTAVL or BCHTAVL to test different tree types
//        HeuristicRunner runner = new HeuristicRunner();
//        IHeuristic bcht;
//
//        switch (treeType) {
//            case BCHT:
//                bcht = new BCHT<IntervalTree>(inputReader, new IntervalTreeFactory(), "BCHT");
//                runner.run(bcht, requests);
//                break;
//            case BCHTAVL:
//                bcht = new BCHT<AVLIntervalTree>(inputReader, new AVLIntervalTreeFactory(), "BCHTAVL");
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
        AVLIntervalTree avlIntervalTree = new AVLIntervalTree();

        // Voeg een paar intervalnodes toe (niet gesorteerd)
        AVLIntervalNode node1 = new AVLIntervalNode(new Interval(50, 25), 10, 1);
        AVLIntervalNode node2 = new AVLIntervalNode(new Interval(25, 15), 15, 2);
        AVLIntervalNode node3 = new AVLIntervalNode(new Interval(75, 35), 20, 3);
        AVLIntervalNode node4 = new AVLIntervalNode(new Interval(15, 8), 25, 4);
        AVLIntervalNode node5 = new AVLIntervalNode(new Interval(35, 18), 30, 5);
        AVLIntervalNode node6 = new AVLIntervalNode(new Interval(60, 38), 35, 6);
        AVLIntervalNode node7 = new AVLIntervalNode(new Interval(120, 38), 40, 7);
        AVLIntervalNode node8 = new AVLIntervalNode(new Interval(10, 38), 45, 8);
        AVLIntervalNode node9 = new AVLIntervalNode(new Interval(68, 38), 55, 9);
        AVLIntervalNode node10 = new AVLIntervalNode(new Interval(90, 38), 60, 10);
        AVLIntervalNode node11 = new AVLIntervalNode(new Interval(125, 38), 65, 11);
        AVLIntervalNode node12 = new AVLIntervalNode(new Interval(83, 38), 70, 12);
        AVLIntervalNode node13 = new AVLIntervalNode(new Interval(100, 38), 70, 13);


        // Insert nodes in random order
        avlIntervalTree.insert(node1);
        avlIntervalTree.insert(node2);
        avlIntervalTree.insert(node3);
        avlIntervalTree.insert(node4);
        avlIntervalTree.insert(node5);
        avlIntervalTree.insert(node6);
        avlIntervalTree.insert(node7);
        avlIntervalTree.insert(node8);
        avlIntervalTree.insert(node9);
        avlIntervalTree.insert(node10);
        avlIntervalTree.insert(node11);
        avlIntervalTree.insert(node12);
        avlIntervalTree.insert(node13);

        avlIntervalTree.delete(node7);

    }
}
