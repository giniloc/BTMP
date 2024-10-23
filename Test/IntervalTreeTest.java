import Utils.Interval;
import Utils.IntervalNode;
import Utils.IntervalTree;
import org.junit.Test;

public class IntervalTreeTest {
    @Test
    public void main() {

        IntervalTree IntervalTree = new IntervalTree();

        IntervalNode node1 = new IntervalNode(new Interval(50, 75), 10, 1);
        IntervalNode node2 = new IntervalNode(new Interval(25, 85), 15, 2);
        IntervalNode node3 = new IntervalNode(new Interval(75, 95), 20, 3);
        IntervalNode node4 = new IntervalNode(new Interval(15, 105), 25, 4);
        IntervalNode node5 = new IntervalNode(new Interval(35, 918), 30, 5);
        IntervalNode node6 = new IntervalNode(new Interval(60, 838), 35, 6);
        IntervalNode node7 = new IntervalNode(new Interval(120, 738), 40, 7);
        IntervalNode node8 = new IntervalNode(new Interval(10, 638), 45, 8);
        IntervalNode node9 = new IntervalNode(new Interval(68, 538), 55, 9);
        IntervalNode node10 = new IntervalNode(new Interval(90, 138), 60, 10);
        IntervalNode node11 = new IntervalNode(new Interval(125, 338), 65, 11);
        IntervalNode node12 = new IntervalNode(new Interval(83, 238), 70, 12);
        IntervalNode node13 = new IntervalNode(new Interval(100, 138), 70, 13);
        IntervalNode node14 = new IntervalNode(new Interval(122, 999), 70, 14);


        IntervalTree.insert(node1);
        IntervalTree.insert(node2);
        IntervalTree.insert(node3);
        IntervalTree.insert(node4);
        IntervalTree.insert(node5);
        IntervalTree.insert(node6);
        IntervalTree.insert(node7);
        IntervalTree.insert(node8);
        IntervalTree.insert(node9);
        IntervalTree.insert(node10);
        IntervalTree.insert(node11);
        IntervalTree.insert(node12);
        IntervalTree.insert(node13);
        IntervalTree.insert(node14);

        IntervalTree.delete(node4);
//        IntervalTree.delete(node3);
//       IntervalTree.delete(node10);
    }
}
