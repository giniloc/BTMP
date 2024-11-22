import Utils.*;
import org.junit.Test;
import org.w3c.dom.Node;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class IntervalTreeTest {
    IntervalNode node1 = new IntervalNode(new Interval(0, 25), 10, 1);
    IntervalNode node2 = new IntervalNode(new Interval(0, 15), 15, 2);
    IntervalNode node3 = new IntervalNode(new Interval(0, 35), 20, 0);
    IntervalNode node4 = new IntervalNode(new Interval(1, 8), 25, 4);
    IntervalNode node5 = new IntervalNode(new Interval(11, 18), 30, 5);
    IntervalNode node6 = new IntervalNode(new Interval(15, 38), 35, 6);
    IntervalNode node7 = new IntervalNode(new Interval(25, 38), 40, 7);
    IntervalNode node8 = new IntervalNode(new Interval(6, 38), 45, 8);
    IntervalNode node9 = new IntervalNode(new Interval(22, 38), 55, 9);
    IntervalNode node10 = new IntervalNode(new Interval(27, 38), 60, 10);
    private IntervalTree buildTree(){
        IntervalTree IntervalTree = new IntervalTree();




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

        return IntervalTree;
    }
    @Test
    public void testInsert() {
        var IntervalTree = buildTree();
        var root = IntervalTree.getRoot();
        assert root != null;
    }
    @Test
    public void testInsertRecursive() {
        IntervalTree IntervalTree = new IntervalTree();
        IntervalNode node1 = new IntervalNode(new Interval(3, 40), 10, 1);
        IntervalNode node2 = new IntervalNode(new Interval(41, 100), 15, 2);
        IntervalNode node3 = new IntervalNode(new Interval(41, 62), 20, 3);
        IntervalNode node4 = new IntervalNode(new Interval(8, 67), 25, 4);

        IntervalTree.insert(node1);
        IntervalTree.insert(node2);
        IntervalTree.insert(node3);
        IntervalTree.insert(node4);
    }
    @Test
    public void randomNodeDeletion(){//This one will fail
        // Arrange
        var tree = buildTree();
        var randomNode = tree.getRandomNode();
        var copyNode = new IntervalNode(randomNode.getInterval(), randomNode.getWeight(), randomNode.getID());
        tree.delete(randomNode);
        assertEquals(randomNode.getInterval(), copyNode.getInterval());
    }
    @Test
    public void rootNodeDeletion(){//This one will fail
        // Arrange
        var tree = buildTree();
        var randomNode = tree.getRoot();
        var copyNode = new IntervalNode(randomNode.getInterval(), randomNode.getWeight(), randomNode.getID());
        tree.delete(randomNode);
        assertEquals(randomNode.getInterval(), copyNode.getInterval());
    }
    @Test
    public void getMaxEndtimeNode() {
        // Arrange
        var tree = buildTree();
        // Act
        var maxEndTimeNode = tree.getMaxEndTimeNode();
        // Assert
        assertEquals(maxEndTimeNode.getMaxEndTime(), 38);
    }
}
