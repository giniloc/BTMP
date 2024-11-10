import Utils.*;
import org.junit.Test;

public class AVLIntervalTreeTest {
    AVLIntervalNode node1 = new AVLIntervalNode(new Interval(2, 25), 10, 1);
    AVLIntervalNode node2 = new AVLIntervalNode(new Interval(5, 50), 15, 2);
    AVLIntervalNode node3 = new AVLIntervalNode(new Interval(25, 80), 20, 3);
    AVLIntervalNode node4 = new AVLIntervalNode(new Interval(1, 8), 25, 4);
    AVLIntervalNode node5 = new AVLIntervalNode(new Interval(11, 18), 30, 5);
    AVLIntervalNode node6 = new AVLIntervalNode(new Interval(15, 38), 35, 6);
    AVLIntervalNode node7 = new AVLIntervalNode(new Interval(25, 38), 40, 7);
    AVLIntervalNode node8 = new AVLIntervalNode(new Interval(6, 38), 45, 8);
    AVLIntervalNode node9 = new AVLIntervalNode(new Interval(22, 38), 55, 9);
    AVLIntervalNode node10 = new AVLIntervalNode(new Interval(27, 38), 60, 10);
    private AVLIntervalTree buildTree(){
        AVLIntervalTree avlIntervalTree = new AVLIntervalTree();




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

        return avlIntervalTree;
    }
    @Test
    public void testInsert() {
        var avlIntervalTree = buildTree();
        var root = avlIntervalTree.getRoot();
        assert root != null;
        assert avlIntervalTree.isInBalance();
    }
    @Test
    public void deepCopyTest(){
            AVLIntervalTree originalTree = buildTree();

            AVLIntervalTree copiedTree = originalTree.deepCopy();
            assert copiedTree.isInBalance();
            for (AVLIntervalNode node : originalTree.findAllOverlapping(new Interval(0, 999999999))) {
                assert copiedTree.findNode(node) != null;
            }
    }
    @Test
    public void deleteTest(){
        AVLIntervalTree originalTree = buildTree();

        originalTree.delete(node3);
        assert originalTree.isInBalance();
        assert originalTree.findNode(node3) == null;
    }
}
