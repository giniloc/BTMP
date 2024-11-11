import Utils.*;
import org.junit.Test;
import static Utils.Color.BLACK;
import static Utils.Color.RED;
import static org.junit.Assert.*;

public class RBIntervalTreeTest {

    private RBIntervalNode node1 = new RBIntervalNode(new Interval(13, 25), 10, 1);
    private RBIntervalNode node2 = new RBIntervalNode(new Interval(8, 15), 15, 2);
    private RBIntervalNode node3 = new RBIntervalNode(new Interval(17, 35), 20, 3);
    private RBIntervalNode node4 = new RBIntervalNode(new Interval(1, 8), 25, 4);
    private RBIntervalNode node5 = new RBIntervalNode(new Interval(11, 18), 30, 5);
    private RBIntervalNode node6 = new RBIntervalNode(new Interval(15, 38), 35, 6);
    private RBIntervalNode node7 = new RBIntervalNode(new Interval(25, 38), 40, 7);
    private RBIntervalNode node8 = new RBIntervalNode(new Interval(6, 50), 45, 8);
    private RBIntervalNode node9 = new RBIntervalNode(new Interval(22, 40), 55, 9);
    private RBIntervalNode node10 = new RBIntervalNode(new Interval(27, 39), 60, 10);

    private RBIntervalTree buildTree(){
        RBIntervalTree rbIntervalTree = new RBIntervalTree();

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

        return rbIntervalTree;
    }


    //Example 1 - delete node 6
    @Test
    public void deleteRedLeafNode(){
        // Arrange
        var tree = buildTree();

        // Act
        tree.delete(node8);

        // Assert
        assertEquals(1,tree.getRoot().getID());
        assertTrue(tree.getRoot().getLeft().getLeft().isLeafNode());
        assertEquals(BLACK, tree.getRoot().getLeft().getLeft().getColor());
        assertTrue(tree.getRoot().getLeft().getRight().isLeafNode());
        assertEquals(BLACK, tree.getRoot().getLeft().getRight().getColor());
        assertEquals(2,tree.getRoot().getLeft().getID());
        assertEquals(RED,tree.getRoot().getLeft().getColor());
        assertNull(tree.findNode(node8));
        assertEquals(50, tree.getRoot().getMaxEndTime());
        assertTrue(tree.isBalanced());
    }

    // delete node 22
    @Test
    public void deleteRedLeafNode2(){
        // Arrange
        var tree = buildTree();

        // Act
        tree.delete(node9);

        // Assert
        assertEquals(1,tree.getRoot().getID());
        assertFalse(tree.getRoot().getRight().getRight().hasLeft());
        assertTrue(tree.getRoot().getRight().getRight().hasRight());
        assertNull(tree.findNode(node9));
        assertEquals(50, tree.getRoot().getMaxEndTime());
        assertEquals(40, tree.getRoot().getRight().getMaxEndTime());
        assertTrue(tree.isBalanced());
    }

    // delete node 27
    @Test
    public void deleteRedLeafNode3(){
        // Arrange
        var tree = buildTree();

        // Act
        tree.delete(node10);

        // Assert
        assertEquals(1,tree.getRoot().getID());
        assertTrue(tree.getRoot().getRight().getRight().hasLeft());
        assertFalse(tree.getRoot().getRight().getRight().hasRight());
        assertNull(tree.findNode(node10));
        assertEquals(50, tree.getRoot().getMaxEndTime());
        assertEquals(40, tree.getRoot().getRight().getMaxEndTime());
        assertTrue(tree.isBalanced());
    }

    // Delete node 15
    @Test
    public void deleteBlackLeafNode2(){
        // Arrange
        var tree = buildTree();

        // Act
        tree.delete(node6);

        // Assert
        assertEquals(1,tree.getRoot().getID());
        assertTrue(tree.getRoot().getRight().hasLeft());
        assertTrue(tree.getRoot().getRight().hasRight());
        assertNull(tree.findNode(node6));
        assertEquals(50, tree.getRoot().getMaxEndTime());
        assertEquals(40, tree.getRoot().getRight().getMaxEndTime());
        assertTrue(tree.isBalanced());
    }


    // Example 2 - delete node 1
    @Test
    public void deleteNodeWithOnlyRightRedChild(){
        // Arrange
        var tree = buildTree();

        // Act
        tree.delete(node4);

        // Assert
        assertEquals(1,tree.getRoot().getID());
        assertTrue(tree.getRoot().getLeft().getLeft().isLeafNode());
        assertTrue(tree.getRoot().getLeft().getRight().isLeafNode());
        assertEquals(BLACK, tree.getRoot().getLeft().getLeft().getColor());
        assertNull(tree.findNode(node4));
        assertEquals(50, tree.getRoot().getMaxEndTime());
        assertEquals(50, tree.getRoot().getMaxEndTime());
        assertEquals(40, tree.getRoot().getRight().getMaxEndTime());
        assertTrue(tree.isBalanced());
    }

    //Example 8 (executes fixup cases 3 and 4) - delete node 8
    @Test
    public void deleteRedNodeWithLeftAndRightChildByBlackNode(){
        // Arrange
        var tree = buildTree();

        // Act
        tree.delete(node2);

        // Assert
        var replacement = tree.findNode(node5);
        assertEquals(1,tree.getRoot().getID());
        assertTrue(replacement.isLeafNode());
        assertEquals(BLACK, replacement.getColor());
        assertEquals(RED, replacement.getParent().getColor());
        assertEquals(tree.getRoot().getLeft(), replacement.getParent());
        assertNull(tree.findNode(node2));
        assertEquals(50, tree.getRoot().getMaxEndTime());
        assertEquals(40, tree.getRoot().getRight().getMaxEndTime());
        assertTrue(tree.isBalanced());
    }

    //Example 10 (executes fixup cases 3 and 4)
    @Test
    public void deleteLeafNodeWithRebalance(){
        // Arrange
        var tree = buildTree();

        // Act
        tree.delete(node5); //11-18

        // Assert
        assertEquals(1,tree.getRoot().getID());
        assertEquals(8, tree.getRoot().getLeft().getID());
        assertEquals(RED, tree.getRoot().getLeft().getColor());
        assertTrue(tree.getRoot().getLeft().hasLeft());
        assertTrue(tree.getRoot().getLeft().hasRight());
        assertEquals(4, tree.getRoot().getLeft().getLeft().getID());
        assertEquals(2, tree.getRoot().getLeft().getRight().getID());
        assertNull(tree.findNode(node5));
        assertEquals(50, tree.getRoot().getMaxEndTime());
        assertEquals(40, tree.getRoot().getRight().getMaxEndTime());
        assertTrue(tree.isBalanced());
    }

    //Example 3 - delete 17
    @Test
    public void deleteRedNodeWithLeftAndRightChildByRedNode(){
        // Arrange
        var tree = buildTree();

        // Act
        tree.delete(node3);

        // Assert
        assertEquals(1,tree.getRoot().getID());
        assertTrue(tree.getRoot().getRight().hasLeft());
        assertTrue(tree.getRoot().getRight().hasRight());
        assertTrue(tree.getRoot().getRight().getLeft().isLeafNode());
        assertEquals(9,tree.getRoot().getRight().getID());
        assertEquals(RED, tree.getRoot().getRight().getColor());
        assertNull(tree.findNode(node3));
        assertEquals(50, tree.getRoot().getMaxEndTime());
        assertEquals(40, tree.getRoot().getRight().getMaxEndTime());
        assertTrue(tree.isBalanced());
    }

    //Example 4 - delete 25
    @Test
    public void deleteBlackNodeWithLeftAndRightChildByRedNode(){
        // Arrange
        var tree = buildTree();

        // Act
        tree.delete(node7);

        // Assert
        var replacement = tree.findNode(node10);
        assertEquals(1,tree.getRoot().getID());
        assertTrue(replacement.hasLeft());
        assertFalse(replacement.hasRight());
        assertEquals(tree.getRoot().getRight(), replacement.getParent());
        assertEquals(BLACK, replacement.getColor());
        assertNull(tree.findNode(node7));
        assertEquals(50, tree.getRoot().getMaxEndTime());
        assertEquals(40, tree.getRoot().getRight().getMaxEndTime());
        assertTrue(tree.isBalanced());
    }

    //Example 5 - delete 8 (fixup case 0)
    @Test
    public void deleteRedNodeByBlackReplacementWithFixupCaseZero(){
        // Arrange
        var tree = buildTree();

        var node = new RBIntervalNode(new Interval(12,18), 40, 99);
        tree.insert(node);

        // Act
        tree.delete(node2);

        // Assert
        var replacement = tree.findNode(node5);
        assertEquals(1,tree.getRoot().getID());
        assertTrue(replacement.hasLeft());
        assertTrue(replacement.hasRight());
        assertEquals(RED, replacement.getColor());
        assertEquals(BLACK, replacement.getRight().getColor());
        assertNull(tree.findNode(node2));
        assertEquals(50, tree.getRoot().getMaxEndTime());
        assertEquals(40, tree.getRoot().getRight().getMaxEndTime());
        assertEquals(50, tree.getRoot().getLeft().getMaxEndTime());
        assertTrue(tree.isBalanced());
    }

    //Example 6 ??? -> insert rebalances the tree => fixup already done => fall into previous cases
    @Test
    public void deleteRedNodeWithBlackLeftAndRightLeafChild(){
        // Arrange
        var tree = buildTree();

        var node = new RBIntervalNode(new Interval(26,30), 30, 99);
        tree.insert(node);
        node = new RBIntervalNode(new Interval(28,10), 20, 100);
        tree.insert(node);


        // Act
        tree.delete(node10);

        // Assert
        assertEquals(1,tree.getRoot().getID());
        assertNull(tree.findNode(node10));
        assertEquals(50, tree.getRoot().getMaxEndTime());
        assertEquals(40, tree.getRoot().getRight().getMaxEndTime());
        assertEquals(50, tree.getRoot().getLeft().getMaxEndTime());
        assertTrue(tree.isBalanced());
    }

    // Example 7 - delete 13 (root node)
    @Test
    public void deleteRootNode(){
        // Arrange
        var tree = buildTree();
        var nodeToDelete = tree.getRoot();

        // Act
        tree.delete(nodeToDelete);

        var newRoot = tree.findNode(node6);

        // Assert
        assertEquals(6,tree.getRoot().getID());
        assertEquals(newRoot,tree.getRoot());
        var node = tree.findNode(node7);
        assertEquals(node,newRoot.getRight());
        assertEquals(RED,newRoot.getRight().getColor());
        assertEquals(newRoot,node.getParent());

        assertEquals(3,node.getLeft().getID());
        assertEquals(10,node.getRight().getID());
        assertEquals(BLACK,node.getLeft().getColor());
        assertEquals(BLACK,node.getRight().getColor());
        assertEquals(node,node.getLeft().getParent());
        assertEquals(node,node.getRight().getParent());

        assertEquals(9,node.getLeft().getRight().getID());
        assertEquals(RED,node.getLeft().getRight().getColor());

        assertNull(tree.findNode(node1));
        assertEquals(50, tree.getRoot().getMaxEndTime());
        assertEquals(40, tree.getRoot().getRight().getMaxEndTime());
        assertEquals(50, tree.getRoot().getLeft().getMaxEndTime());
        assertTrue(tree.isBalanced());
    }

    //delete all node except root
    @Test
    public void deleteAllNodesExceptRoot(){
        // Arrange
        var tree = buildTree();

        // Act
        tree.delete(tree.findNode(node7));

        var replacement = tree.findNode(node10);
        assertEquals(BLACK, replacement.getColor());
        assertTrue(replacement.hasLeft());
        assertFalse(replacement.hasRight());
        assertEquals(replacement, replacement.getLeft().getParent());
        assertTrue(tree.isBalanced());

        tree.delete(tree.findNode(node2));

        replacement = tree.findNode(node5);
        assertEquals(BLACK, replacement.getColor());
        assertTrue(replacement.isLeafNode());
        assertEquals(8, replacement.getParent().getID());
        assertTrue(tree.isBalanced());

        tree.delete(tree.findNode(node4));
        assertTrue(tree.isBalanced());
        tree.delete(tree.findNode(node8));
        assertTrue(tree.isBalanced());
        tree.delete(tree.findNode(node9));
        assertTrue(tree.isBalanced());
        tree.delete(tree.findNode(node3));
        assertTrue(tree.isBalanced());
        tree.delete(tree.findNode(node5));
        assertTrue(tree.isBalanced());
        tree.delete(tree.findNode(node10));
        assertTrue(tree.isBalanced());
        tree.delete(tree.findNode(node6));
        assertTrue(tree.isBalanced());


        // Assert
        assertEquals(1,tree.getRoot().getID());
        assertNull(tree.getRoot().getLeft());
        assertNull(tree.getRoot().getRight());
    }

    // Example 6
    @Test
    public void deleteFixupCase2(){
        // Arrange
        var tree = buildTree();

        // First bring the tree in the test situation
        tree.delete(tree.findNode(node2));

        // Act
        tree.delete(tree.findNode(node8));

        // Assert
        assertEquals(1,tree.getRoot().getID());
        assertEquals(5,tree.getRoot().getLeft().getID());
        assertEquals(BLACK,tree.getRoot().getLeft().getColor());
        assertEquals(RED,tree.getRoot().getLeft().getLeft().getColor());
        assertEquals(4,tree.getRoot().getLeft().getLeft().getID());
        assertFalse(tree.getRoot().getLeft().hasRight());
        assertEquals(40, tree.getRoot().getMaxEndTime());
        assertEquals(40, tree.getRoot().getRight().getMaxEndTime());
        assertEquals(18, tree.getRoot().getLeft().getMaxEndTime());
        assertTrue(tree.isBalanced());
    }

    @Test
    public void deleteFixupCase1And2(){
        // Arrange
        var tree = buildTree();

        // First bring the tree in the test situation
        tree.delete(tree.findNode(node2));
        tree.delete(tree.findNode(node8));
        tree.delete(tree.findNode(node4));

        // Act
        tree.delete(tree.findNode(node5));

        // Assert
        assertEquals(3,tree.getRoot().getID());
        assertEquals(1,tree.getRoot().getLeft().getID());
        assertEquals(BLACK,tree.getRoot().getLeft().getColor());
        assertEquals(BLACK,tree.getRoot().getRight().getColor());
        assertFalse(tree.getRoot().getLeft().hasLeft());
        assertTrue(tree.getRoot().getLeft().hasRight());
        assertEquals(40, tree.getRoot().getMaxEndTime());
        assertEquals(40, tree.getRoot().getRight().getMaxEndTime());
        assertEquals(38, tree.getRoot().getLeft().getMaxEndTime());
        assertTrue(tree.isBalanced());
    }

    @Test
    public void findExistingNodeByIntervalTest() {
        // Arrange
        var tree = buildTree();

        //Act
        var node = tree.findNode(node5);

        assertEquals(5,node.getID());
    }

    @Test
    public void findNonExistingNodeByIntervalTest() {
        // Arrange
        var tree = buildTree();

        //Act
        var node = tree.findNode(new IntervalNode(new Interval(11,19), 10, 199));

        assertEquals(null,node);
    }

    @Test
    public void insertNodeWithDuplicateInterval(){
        //Arrange
        RBIntervalTree rbIntervalTree = new RBIntervalTree();

        RBIntervalNode node1 = new RBIntervalNode(new Interval(13, 25), 10, 1);
        RBIntervalNode node2 = new RBIntervalNode(new Interval(8, 15), 15, 2);
        RBIntervalNode node3 = new RBIntervalNode(new Interval(17, 35), 20, 3);
        RBIntervalNode node4 = new RBIntervalNode(new Interval(1, 8), 25, 4);
        RBIntervalNode node5 = new RBIntervalNode(new Interval(11, 18), 30, 5);

        rbIntervalTree.insert(node1);
        rbIntervalTree.insert(node2);
        rbIntervalTree.insert(node3);
        rbIntervalTree.insert(node4);
        rbIntervalTree.insert(node5);

        //Act
        rbIntervalTree.insert(new RBIntervalNode(new Interval(1, 8), 20, 99));

        //Assert
        assertTrue(rbIntervalTree.isBalanced());
    }

    @Test
    public void insertNodeWithAllDuplicateStarttimes(){
        //Arrange
        RBIntervalTree rbIntervalTree = new RBIntervalTree();

        RBIntervalNode node1 = new RBIntervalNode(new Interval(13, 25), 10, 1);
        RBIntervalNode node2 = new RBIntervalNode(new Interval(13, 15), 15, 2);
        RBIntervalNode node3 = new RBIntervalNode(new Interval(13, 35), 20, 3);
        RBIntervalNode node4 = new RBIntervalNode(new Interval(13, 20), 25, 4);
        RBIntervalNode node5 = new RBIntervalNode(new Interval(13, 18), 30, 5);

        rbIntervalTree.insert(node1);
        rbIntervalTree.insert(node2);
        rbIntervalTree.insert(node3);
        rbIntervalTree.insert(node4);
        rbIntervalTree.insert(node5);

        //Act
        rbIntervalTree.insert(new RBIntervalNode(new Interval(13, 50), 20, 0));

        //Assert
        assertTrue(rbIntervalTree.isBalanced());
    }

    @Test
    public void reinsertNode(){
        //Arrange
        RBIntervalTree tree = new RBIntervalTree();

        RBIntervalNode node1 = new RBIntervalNode(new Interval(13, 48), 10, 1);
        RBIntervalNode node2 = new RBIntervalNode(new Interval(2, 44), 20, 3);
        RBIntervalNode node3 = new RBIntervalNode(new Interval(1, 77), 25, 4);

        tree.insert(node1);
        tree.insert(node2);
        tree.insert(node3);

        //Act
        tree.delete(node2);
        tree.insert(node2);

        //Assert
        assertTrue(tree.isBalanced());
    }

    @Test
    public void isBalancedMethodTest(){
        //Arrange
        RBIntervalTree tree = new RBIntervalTree();

        RBIntervalNode node1 = new RBIntervalNode(new Interval(10, 20), 10, 1);
        RBIntervalNode node2 = new RBIntervalNode(new Interval(15, 25), 20, 2);
        RBIntervalNode node3 = new RBIntervalNode(new Interval(30, 40), 30, 3);
        RBIntervalNode node4 = new RBIntervalNode(new Interval(5, 15), 15, 4);
        RBIntervalNode node5 = new RBIntervalNode(new Interval(25, 35), 40, 5);
        RBIntervalNode node6 = new RBIntervalNode(new Interval(35, 45), 25, 6);
        RBIntervalNode node7 = new RBIntervalNode(new Interval(50, 60), 50, 7);
        RBIntervalNode node8 = new RBIntervalNode(new Interval(45, 55), 55, 8);
        RBIntervalNode node9 = new RBIntervalNode(new Interval(65, 75), 65, 9);

        tree.insert(node1);
        tree.insert(node2);
        tree.insert(node3);
        tree.insert(node4);
        tree.insert(node5);
        tree.insert(node6);
        tree.insert(node7);
        tree.insert(node8);
        tree.insert(node9);

        //Act
        tree.delete(tree.findNode(node2));
        tree.insert(node2);
        tree.delete(tree.findNode(node7));
        tree.insert(node7);
        tree.delete(tree.findNode(node4));

        //Assert
        assertTrue(tree.isBalanced());
    }
    @Test
    public void insertNodeWithAllDuplicateIntervals(){
        //Arrange
        RBIntervalTree rbIntervalTree = new RBIntervalTree();

        RBIntervalNode node1 = new RBIntervalNode(new Interval(13, 20), 10, 1);
        RBIntervalNode node2 = new RBIntervalNode(new Interval(13, 20), 15, 2);
        RBIntervalNode node3 = new RBIntervalNode(new Interval(13, 20), 20, 3);
        RBIntervalNode node4 = new RBIntervalNode(new Interval(13, 20), 25, 4);
        RBIntervalNode node5 = new RBIntervalNode(new Interval(13, 20), 30, 5);

        rbIntervalTree.insert(node1);
        rbIntervalTree.insert(node2);
        rbIntervalTree.insert(node3);
        rbIntervalTree.insert(node4);
        rbIntervalTree.insert(node5);

        //Act
        RBIntervalNode copynode = rbIntervalTree.findNode(node3);

        //Assert
        assertTrue(rbIntervalTree.isBalanced());
        assertTrue(copynode.equals(node3));//This equal method is overridden in RBIntervalNode class
    }

    @Test
    public void deleteRoot(){
        //Arrange
        RBIntervalTree rbIntervalTree = new RBIntervalTree();

        RBIntervalNode node1 = new RBIntervalNode(new Interval(24, 63), 10, 1);
        RBIntervalNode node2 = new RBIntervalNode(new Interval(3, 40), 15, 2);
        RBIntervalNode node3 = new RBIntervalNode(new Interval(45, 72), 20, 3);
        RBIntervalNode node4 = new RBIntervalNode(new Interval(41, 62), 25, 4);

        rbIntervalTree.insert(node1);
        rbIntervalTree.insert(node2);
        rbIntervalTree.insert(node3);
        rbIntervalTree.insert(node4);

        //Act
        rbIntervalTree.delete(node1);

        //Assert
        assertTrue(rbIntervalTree.isBalanced());
    }

    @Test
    public void deleteNodeFromSingleNodeTree(){
        //Arrange
        RBIntervalTree rbIntervalTree = new RBIntervalTree();

        RBIntervalNode node1 = new RBIntervalNode(new Interval(24, 63), 10, 1);

        rbIntervalTree.insert(node1);

        //Act
        rbIntervalTree.delete(node1);

        //Assert
        assertTrue(rbIntervalTree.isBalanced());
        assertNull(rbIntervalTree.getRoot());
    }

    @Test
    public void deepCopyTest(){
        var tree = buildTree();
        var copyTree = tree.deepCopy();

        assertTrue(copyTree.isBalanced());
    }

    @Test
    public void rootNodeDeletion(){
        // Arrange
        var tree = buildTree();
        var randomNode = tree.getRoot();
        var copyNode = new RBIntervalNode(randomNode.getInterval(), randomNode.getWeight(), randomNode.getID());
        copyNode.setColor(randomNode.getColor());

        //act
        tree.delete(randomNode);

        //assert
        assertTrue(tree.isBalanced());
        assertEquals(randomNode.getInterval(), copyNode.getInterval());
    }
}