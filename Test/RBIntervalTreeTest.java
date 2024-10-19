import Utils.Interval;
import Utils.RBIntervalNode;
import Utils.RBIntervalTree;
import org.junit.Test;

public class RBIntervalTreeTest {
    @Test
    public void main() {

        RBIntervalTree rbIntervalTree = new RBIntervalTree();

        RBIntervalNode node1 = new RBIntervalNode(new Interval(13, 25), 10, 1);
        RBIntervalNode node2 = new RBIntervalNode(new Interval(8, 15), 15, 2);
        RBIntervalNode node3 = new RBIntervalNode(new Interval(17, 35), 20, 3);
        RBIntervalNode node4 = new RBIntervalNode(new Interval(1, 8), 25, 4);
        RBIntervalNode node5 = new RBIntervalNode(new Interval(11, 18), 30, 5);
        RBIntervalNode node6 = new RBIntervalNode(new Interval(15, 38), 35, 6);
        RBIntervalNode node7 = new RBIntervalNode(new Interval(25, 38), 40, 7);
        RBIntervalNode node8 = new RBIntervalNode(new Interval(6, 38), 45, 8);
        RBIntervalNode node9 = new RBIntervalNode(new Interval(22, 38), 55, 9);
        RBIntervalNode node10 = new RBIntervalNode(new Interval(27, 38), 60, 10);
//        RBIntervalNode node11 = new RBIntervalNode(new Interval(125, 38), 65, 11);
//        RBIntervalNode node12 = new RBIntervalNode(new Interval(83, 38), 70, 12);
//        RBIntervalNode node13 = new RBIntervalNode(new Interval(100, 38), 70, 13);



        rbIntervalTree.insert(node1);
        rbIntervalTree.insert(node2);
        rbIntervalTree.insert(node3);
        rbIntervalTree.insert(node4);
        rbIntervalTree.insert(node5);
        rbIntervalTree.insert(node6);
        rbIntervalTree.insert(node7);
        rbIntervalTree.insert(node8);
        rbIntervalTree.insert(node9);
        rbIntervalTree.insert(node10);
//        rbIntervalTree.insert(node11);
//        rbIntervalTree.insert(node12);
//        rbIntervalTree.insert(node13);

        rbIntervalTree.delete(node2);
    }
}
