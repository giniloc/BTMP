import Utils.Interval;
import Utils.AVLIntervalNode;
import Utils.AVLIntervalTree;
import org.junit.Test;

public class AVLIntervalTreeTest {
    @Test
    public void main() {

        AVLIntervalTree avlIntervalTree = new AVLIntervalTree();

        AVLIntervalNode node1 = new AVLIntervalNode(new Interval(49, 75), 10, 1);
        AVLIntervalNode node2 = new AVLIntervalNode(new Interval(17, 85), 15, 2);
        AVLIntervalNode node3 = new AVLIntervalNode(new Interval(71, 95), 20, 3);
        AVLIntervalNode node4 = new AVLIntervalNode(new Interval(4, 105), 25, 4);
        AVLIntervalNode node5 = new AVLIntervalNode(new Interval(8, 918), 30, 5);
        AVLIntervalNode node6 = new AVLIntervalNode(new Interval(25, 838), 35, 6);
        AVLIntervalNode node7 = new AVLIntervalNode(new Interval(81, 738), 40, 7);
        AVLIntervalNode node8 = new AVLIntervalNode(new Interval(60, 638), 45, 8);
//        AVLIntervalNode node9 = new AVLIntervalNode(new Interval(68, 538), 55, 9);
//        AVLIntervalNode node10 = new AVLIntervalNode(new Interval(90, 138), 60, 10);
//        AVLIntervalNode node11 = new AVLIntervalNode(new Interval(125, 338), 65, 11);
//        AVLIntervalNode node12 = new AVLIntervalNode(new Interval(83, 238), 70, 12);
//        AVLIntervalNode node13 = new AVLIntervalNode(new Interval(100, 138), 70, 13);


        avlIntervalTree.insert(node1);
        avlIntervalTree.insert(node2);
        avlIntervalTree.insert(node3);
        avlIntervalTree.insert(node4);
        avlIntervalTree.insert(node5);
//        avlIntervalTree.insert(node6);
//        avlIntervalTree.insert(node7);
//        avlIntervalTree.insert(node8);
//        avlIntervalTree.insert(node9);
//        avlIntervalTree.insert(node10);
//       avlIntervalTree.insert(node11);
//       avlIntervalTree.insert(node12);
//       avlIntervalTree.insert(node13);

        avlIntervalTree.delete(node7);
//        avlIntervalTree.delete(node3);
//       avlIntervalTree.delete(node10);
        avlIntervalTree.insert(node7);
    }
}
