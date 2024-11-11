import Utils.*;
import org.junit.Test;
import static org.junit.Assert.*;

public class AVLIntervalTreeTest {
    //region Same as RBTRee
    private AVLIntervalNode node1 = new AVLIntervalNode(new Interval(13, 25), 10, 1);
    private AVLIntervalNode node2 = new AVLIntervalNode(new Interval(8, 15), 15, 2);
    private AVLIntervalNode node3 = new AVLIntervalNode(new Interval(17, 35), 20, 3);
    private AVLIntervalNode node4 = new AVLIntervalNode(new Interval(1, 8), 25, 4);
    private AVLIntervalNode node5 = new AVLIntervalNode(new Interval(11, 18), 30, 5);
    private AVLIntervalNode node6 = new AVLIntervalNode(new Interval(15, 38), 35, 6);
    private AVLIntervalNode node7 = new AVLIntervalNode(new Interval(25, 38), 40, 7);
    private AVLIntervalNode node8 = new AVLIntervalNode(new Interval(6, 50), 45, 8);
    private AVLIntervalNode node9 = new AVLIntervalNode(new Interval(22, 40), 55, 9);
    private AVLIntervalNode node10 = new AVLIntervalNode(new Interval(27, 39), 60, 10);

    private AVLIntervalTree buildAVLTree(){
        var tree = new AVLIntervalTree();

        tree.insert(node1);
        tree.insert(node2);
        tree.insert(node3);
        tree.insert(node4);
        tree.insert(node5);
        tree.insert(node6);
        tree.insert(node7);
        tree.insert(node8);
        tree.insert(node9);
        tree.insert(node10);

        return tree;
    }

    //Example 1 - delete node 6
    @Test
    public void deleteRedLeafNode(){
        // Arrange
        var tree = buildAVLTree();

        // Act
        tree.delete(node8);

        // Assert
        assertEquals(1,tree.getRoot().getID());
        assertTrue(tree.getRoot().getLeft().getLeft().isLeafNode());
        //assertEquals(BLACK, tree.getRoot().getLeft().getLeft().getColor());
        assertTrue(tree.getRoot().getLeft().getRight().isLeafNode());
        //assertEquals(BLACK, tree.getRoot().getLeft().getRight().getColor());
        assertEquals(2,tree.getRoot().getLeft().getID());
        //assertEquals(RED,tree.getRoot().getLeft().getColor());
        assertNull(tree.findNode(node8));
        assertEquals(40, tree.getRoot().getMaxEndTime());
        assertTrue(tree.isBalanced());
    }

    // delete node 22
    @Test
    public void deleteRedLeafNode2(){
        // Arrange
        var tree = buildAVLTree();

        // Act
        tree.delete(node9);

        // Assert
        assertEquals(1,tree.getRoot().getID());
        assertFalse(tree.getRoot().getRight().getRight().hasLeft());
        assertTrue(tree.getRoot().getRight().getRight().hasRight());
        assertNull(tree.findNode(node9));
        assertEquals(50, tree.getRoot().getMaxEndTime());
        assertEquals(39, tree.getRoot().getRight().getMaxEndTime());
        assertTrue(tree.isBalanced());
    }

    // delete node 27
    @Test
    public void deleteRedLeafNode3(){
        // Arrange
        var tree = buildAVLTree();

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
        var tree = buildAVLTree();

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
        var tree = buildAVLTree();

        // Act
        tree.delete(node4);

        // Assert
        assertEquals(1,tree.getRoot().getID());
        assertTrue(tree.getRoot().getLeft().getLeft().isLeafNode());
        assertTrue(tree.getRoot().getLeft().getRight().isLeafNode());
        //assertEquals(BLACK, tree.getRoot().getLeft().getLeft().getColor());
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
        var tree = buildAVLTree();

        // Act
        tree.delete(node2);

        // Assert
        var replacement = tree.findNode(node5);
        assertEquals(1,tree.getRoot().getID());
        assertTrue(replacement.isLeafNode());
        //assertEquals(BLACK, replacement.getColor());
        //assertEquals(RED, replacement.getParent().getColor());
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
        var tree = buildAVLTree();

        // Act
        tree.delete(node5); //11-18

        // Assert
        assertEquals(1,tree.getRoot().getID());
        assertEquals(8, tree.getRoot().getLeft().getID());
        //assertEquals(RED, tree.getRoot().getLeft().getColor());
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
        var tree = buildAVLTree();

        // Act
        tree.delete(node3);

        // Assert
        assertEquals(1,tree.getRoot().getID());
        assertTrue(tree.getRoot().getRight().hasLeft());
        assertTrue(tree.getRoot().getRight().hasRight());
        assertTrue(tree.getRoot().getRight().getLeft().isLeafNode());
        assertEquals(9,tree.getRoot().getRight().getID());
        //assertEquals(RED, tree.getRoot().getRight().getColor());
        assertNull(tree.findNode(node3));
        assertEquals(50, tree.getRoot().getMaxEndTime());
        assertEquals(40, tree.getRoot().getRight().getMaxEndTime());
        assertTrue(tree.isBalanced());
    }

    //Example 4 - delete 25
    @Test
    public void deleteBlackNodeWithLeftAndRightChildByRedNode(){
        // Arrange
        var tree = buildAVLTree();

        // Act
        tree.delete(node7);

        // Assert
        var replacement = tree.findNode(node10);
        assertEquals(1,tree.getRoot().getID());
        assertTrue(replacement.hasLeft());
        assertFalse(replacement.hasRight());
        assertEquals(tree.getRoot().getRight(), replacement.getParent());
        //assertEquals(BLACK, replacement.getColor());
        assertNull(tree.findNode(node7));
        assertEquals(50, tree.getRoot().getMaxEndTime());
        assertEquals(40, tree.getRoot().getRight().getMaxEndTime());
        assertTrue(tree.isBalanced());
    }

    //Example 5 - delete 8 (fixup case 0)
    @Test
    public void deleteRedNodeByBlackReplacementWithFixupCaseZero(){
        // Arrange
        var tree = buildAVLTree();

        var node = new RBIntervalNode(new Interval(12,18), 40, 99);
        tree.insert(node);

        // Act
        tree.delete(node2);

        // Assert
        var replacement = tree.findNode(node5);
        assertEquals(1,tree.getRoot().getID());
        assertTrue(replacement.hasLeft());
        assertTrue(replacement.hasRight());
        //assertEquals(RED, replacement.getColor());
        //assertEquals(BLACK, replacement.getRight().getColor());
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
        var tree = buildAVLTree();

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
        var tree = buildAVLTree();
        var nodeToDelete = tree.getRoot();

        // Act
        tree.delete(nodeToDelete);

        var newRoot = tree.findNode(node6);

        // Assert
        assertEquals(6,tree.getRoot().getID());
        assertEquals(newRoot,tree.getRoot());
        var node = tree.findNode(node9);
        assertEquals(node,newRoot.getRight());
        //assertEquals(RED,newRoot.getRight().getColor());
        assertEquals(newRoot,node.getParent());

        assertEquals(3,node.getLeft().getID());
        assertEquals(7,node.getRight().getID());
        //assertEquals(BLACK,node.getLeft().getColor());
        //assertEquals(BLACK,node.getRight().getColor());
        assertEquals(node,node.getLeft().getParent());
        assertEquals(node,node.getRight().getParent());

        assertEquals(null,node.getLeft().getRight());
        //assertEquals(RED,node.getLeft().getRight().getColor());

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
        var tree = buildAVLTree();

        // Act
        tree.delete(tree.findNode(node7));

        var replacement = tree.findNode(node10);
        //assertEquals(BLACK, replacement.getColor());
        assertTrue(replacement.hasLeft());
        assertFalse(replacement.hasRight());
        assertEquals(replacement, replacement.getLeft().getParent());
        assertTrue(tree.isBalanced());

        tree.delete(tree.findNode(node2));

        replacement = tree.findNode(node5);
        //assertEquals(BLACK, replacement.getColor());
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
        var tree = buildAVLTree();

        // First bring the tree in the test situation
        tree.delete(tree.findNode(node2));

        // Act
        tree.delete(tree.findNode(node8));

        // Assert
        assertEquals(1,tree.getRoot().getID());
        assertEquals(5,tree.getRoot().getLeft().getID());
        //assertEquals(BLACK,tree.getRoot().getLeft().getColor());
        //assertEquals(RED,tree.getRoot().getLeft().getLeft().getColor());
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
        var tree = buildAVLTree();

        // First bring the tree in the test situation
        tree.delete(tree.findNode(node2));
        tree.delete(tree.findNode(node8));
        tree.delete(tree.findNode(node4));

        // Act
        tree.delete(tree.findNode(node5));

        // Assert
        assertEquals(3,tree.getRoot().getID());
        assertEquals(1,tree.getRoot().getLeft().getID());
        //assertEquals(BLACK,tree.getRoot().getLeft().getColor());
        //assertEquals(BLACK,tree.getRoot().getRight().getColor());
        assertFalse(tree.getRoot().getLeft().hasLeft());
        assertTrue(tree.getRoot().getLeft().hasRight());
        assertEquals(40, tree.getRoot().getMaxEndTime());
        assertEquals(40, tree.getRoot().getRight().getMaxEndTime());
        assertEquals(38, tree.getRoot().getLeft().getMaxEndTime());
        assertTrue(tree.isBalanced());
    }
    @Test
    public void randomNodeDeletion(){
        // Arrange
        var tree = buildAVLTree();
        var randomNode = tree.getRandomNode();
        var copyNode = new AVLIntervalNode(randomNode.getInterval(), randomNode.getWeight(), randomNode.getID());
        tree.delete(randomNode);
        assertTrue(tree.isBalanced());
        assertEquals(randomNode.getInterval(), copyNode.getInterval());
    }
    @Test
    public void rootNodeDeletion(){
        // Arrange
        var tree = buildAVLTree();
        var randomNode = tree.getRoot();
        var copyNode = new AVLIntervalNode(randomNode.getInterval(), randomNode.getWeight(), randomNode.getID());
        var n = tree.delete(randomNode);
        assertTrue(tree.isBalanced());
        assertEquals(n.getInterval(), copyNode.getInterval());
    }

//endregion
}
