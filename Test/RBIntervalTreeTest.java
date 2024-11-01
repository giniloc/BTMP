import Utils.*;
import org.junit.Test;

import static Utils.Color.BLACK;
import static Utils.Color.RED;
import static org.junit.Assert.*;

public class RBIntervalTreeTest {

    private RBIntervalTree buildTree(){
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
        var nodeToDelete = tree.findNode(new Interval(6,38));

        // Act
        tree.delete(nodeToDelete);

        // Assert
        assertEquals(1,tree.getRoot().getID());
        assertTrue(tree.getRoot().getLeft().getLeft().isLeafNode());
        assertEquals(BLACK, tree.getRoot().getLeft().getLeft().getColor());
        assertTrue(tree.getRoot().getLeft().getRight().isLeafNode());
        assertEquals(BLACK, tree.getRoot().getLeft().getRight().getColor());
        assertEquals(2,tree.getRoot().getLeft().getID());
        assertEquals(RED,tree.getRoot().getLeft().getColor());
        assertNull(tree.findNode(new Interval(6,38)));

        assertTrue(buildTree().isBalanced());
    }

    // delete node 22
    @Test
    public void deleteRedLeafNode2(){
        // Arrange
        var tree = buildTree();
        var nodeToDelete = tree.findNode(new Interval(22,38));

        // Act
        tree.delete(nodeToDelete);

        // Assert
        assertEquals(1,tree.getRoot().getID());
        assertFalse(tree.getRoot().getRight().getRight().hasLeft());
        assertTrue(tree.getRoot().getRight().getRight().hasRight());
        assertNull(tree.findNode(new Interval(22,38)));

        assertTrue(buildTree().isBalanced());
    }

    // delete node 27
    @Test
    public void deleteRedLeafNode3(){
        // Arrange
        var tree = buildTree();
        var nodeToDelete = tree.findNode(new Interval(27,38));

        // Act
        tree.delete(nodeToDelete);

        // Assert
        assertEquals(1,tree.getRoot().getID());
        assertTrue(tree.getRoot().getRight().getRight().hasLeft());
        assertFalse(tree.getRoot().getRight().getRight().hasRight());
        assertNull(tree.findNode(new Interval(27,38)));

        assertTrue(buildTree().isBalanced());
    }

    // Delete node 15
    @Test
    public void deleteBlackLeafNode2(){
        // Arrange
        var tree = buildTree();
        var nodeToDelete = tree.findNode(new Interval(15,38));

        // Act
        tree.delete(nodeToDelete);

        // Assert
        assertEquals(1,tree.getRoot().getID());
        assertTrue(tree.getRoot().getRight().hasLeft());
        assertTrue(tree.getRoot().getRight().hasRight());
        assertNull(tree.findNode(new Interval(15,38)));

        assertTrue(buildTree().isBalanced());
    }


    // Example 2 - delete node 1
    @Test
    public void deleteNodeWithOnlyRightRedChild(){
        // Arrange
        var tree = buildTree();
        var nodeToDelete = tree.findNode(new Interval(1,8));

        // Act
        tree.delete(nodeToDelete);

        // Assert
        assertEquals(1,tree.getRoot().getID());
        assertTrue(tree.getRoot().getLeft().getLeft().isLeafNode());
        assertTrue(tree.getRoot().getLeft().getRight().isLeafNode());
        assertEquals(BLACK, tree.getRoot().getLeft().getLeft().getColor());
        assertNull(tree.findNode(new Interval(1,8)));

        assertTrue(buildTree().isBalanced());
    }

    //Example 8 (executes fixup cases 3 and 4) - delete node 8
    @Test
    public void deleteRedNodeWithLeftAndRightChildByBlackNode(){
        // Arrange
        var tree = buildTree();
        var nodeToDelete = tree.findNode(new Interval(8,15));

        // Act
        tree.delete(nodeToDelete);

        // Assert
        var replacement = tree.findNode(new Interval(11,18));
        assertEquals(1,tree.getRoot().getID());
        assertTrue(replacement.isLeafNode());
        assertEquals(BLACK, replacement.getColor());
        assertEquals(RED, replacement.getParent().getColor());
        assertEquals(tree.getRoot().getLeft(), replacement.getParent());
        assertNull(tree.findNode(new Interval(8,15)));

        assertTrue(buildTree().isBalanced());
    }

    //Example 10 (executes fixup cases 3 and 4)
    @Test
    public void deleteLeafNodeWithRebalance(){
        // Arrange
        var tree = buildTree();
        var nodeToDelete = tree.findNode(new Interval(11,18));

        // Act
        tree.delete(nodeToDelete);

        // Assert
        assertEquals(1,tree.getRoot().getID());
        assertEquals(8, tree.getRoot().getLeft().getID());
        assertEquals(RED, tree.getRoot().getLeft().getColor());
        assertTrue(tree.getRoot().getLeft().hasLeft());
        assertTrue(tree.getRoot().getLeft().hasRight());
        assertEquals(4, tree.getRoot().getLeft().getLeft().getID());
        assertEquals(2, tree.getRoot().getLeft().getRight().getID());
        assertNull(tree.findNode(new Interval(11,18)));

        assertTrue(buildTree().isBalanced());
    }

    //Example 3 - delete 17
    @Test
    public void deleteRedNodeWithLeftAndRightChildByRedNode(){
        // Arrange
        var tree = buildTree();
        var nodeToDelete = tree.findNode(new Interval(17,35));

        // Act
        tree.delete(nodeToDelete);

        // Assert
        assertEquals(1,tree.getRoot().getID());
        assertTrue(tree.getRoot().getRight().hasLeft());
        assertTrue(tree.getRoot().getRight().hasRight());
        assertTrue(tree.getRoot().getRight().getLeft().isLeafNode());
        assertEquals(9,tree.getRoot().getRight().getID());
        assertEquals(RED, tree.getRoot().getRight().getColor());
        assertNull(tree.findNode(new Interval(17,35)));

        assertTrue(buildTree().isBalanced());
    }

    //Example 4 - delete 25
    @Test
    public void deleteBlackNodeWithLeftAndRightChildByRedNode(){
        // Arrange
        var tree = buildTree();
        var nodeToDelete = tree.findNode(new Interval(25,38));

        // Act
        tree.delete(nodeToDelete);

        // Assert
        var replacement = tree.findNode(new Interval(27,38));
        assertEquals(1,tree.getRoot().getID());
        assertTrue(replacement.hasLeft());
        assertFalse(replacement.hasRight());
        assertEquals(tree.getRoot().getRight(), replacement.getParent());
        assertEquals(BLACK, replacement.getColor());
        assertNull(tree.findNode(new Interval(25,38)));

        assertTrue(buildTree().isBalanced());
    }

    //Example 5 - delete 8 (fixup case 0)
    @Test
    public void deleteRedNodeByBlackReplacementWithFixupCaseZero(){
        // Arrange
        var tree = buildTree();

        var node = new RBIntervalNode(new Interval(12,18), 40, 99);
        tree.insert(node);

        var nodeToDelete = tree.findNode(new Interval(8,15));

        // Act
        tree.delete(nodeToDelete);

        // Assert
        var replacement = tree.findNode(new Interval(11,18));
        assertEquals(1,tree.getRoot().getID());
        assertTrue(replacement.hasLeft());
        assertTrue(replacement.hasRight());
        assertEquals(RED, replacement.getColor());
        assertEquals(BLACK, replacement.getRight().getColor());
        assertNull(tree.findNode(new Interval(8,15)));

        assertTrue(buildTree().isBalanced());
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

        var nodeToDelete = tree.findNode(new Interval(27,38));

        // Act
        tree.delete(nodeToDelete);

        // Assert
        var replacement = tree.findNode(new Interval(11,18));
        assertEquals(1,tree.getRoot().getID());
        assertNull(tree.findNode(new Interval(27,38)));

        assertTrue(buildTree().isBalanced());
    }

    // Example 7 - delete 13 (root node)
    @Test
    public void deleteRootNode(){
        // Arrange
        var tree = buildTree();
        var nodeToDelete = tree.getRoot();

        // Act
        tree.delete(nodeToDelete);

        var newRoot = tree.findNode(new Interval(15,38));

        // Assert
        assertEquals(6,tree.getRoot().getID());
        assertEquals(newRoot,tree.getRoot());
        var node = tree.findNode(new Interval(25,38));
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

        assertNull(tree.findNode(new Interval(13,25)));

        assertTrue(buildTree().isBalanced());
    }

    //delete all node except root
    @Test
    public void deleteAllNodesExceptRoot(){
        // Arrange
        var tree = buildTree();

        // Act
        tree.delete(tree.findNode(new Interval(25,38)));

        var replacement = tree.findNode(new Interval(27,38));
        assertEquals(BLACK, replacement.getColor());
        assertTrue(replacement.hasLeft());
        assertFalse(replacement.hasRight());
        assertEquals(replacement, replacement.getLeft().getParent());
        assertTrue(buildTree().isBalanced());

        tree.delete(tree.findNode(new Interval(8,15)));

        replacement = tree.findNode(new Interval(11,18));
        assertEquals(BLACK, replacement.getColor());
        assertTrue(replacement.isLeafNode());
        assertEquals(8, replacement.getParent().getID());
        assertTrue(buildTree().isBalanced());

        tree.delete(tree.findNode(new Interval(1,8)));
        assertTrue(buildTree().isBalanced());
        tree.delete(tree.findNode(new Interval(6,38)));
        assertTrue(buildTree().isBalanced());
        tree.delete(tree.findNode(new Interval(22,38)));
        assertTrue(buildTree().isBalanced());
        tree.delete(tree.findNode(new Interval(17,35)));
        assertTrue(buildTree().isBalanced());
        tree.delete(tree.findNode(new Interval(11,18)));
        assertTrue(buildTree().isBalanced());
        tree.delete(tree.findNode(new Interval(27,38)));
        assertTrue(buildTree().isBalanced());
        tree.delete(tree.findNode(new Interval(15,38)));
        assertTrue(buildTree().isBalanced());


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
        tree.delete(tree.findNode(new Interval(8,15)));

        // Act
        tree.delete(tree.findNode(new Interval(6,38)));

        // Assert
        assertEquals(1,tree.getRoot().getID());
        assertEquals(5,tree.getRoot().getLeft().getID());
        assertEquals(BLACK,tree.getRoot().getLeft().getColor());
        assertEquals(RED,tree.getRoot().getLeft().getLeft().getColor());
        assertEquals(4,tree.getRoot().getLeft().getLeft().getID());
        assertFalse(tree.getRoot().getLeft().hasRight());

        assertTrue(buildTree().isBalanced());
    }

    @Test
    public void deleteFixupCase1And2(){
        // Arrange
        var tree = buildTree();

        // First bring the tree in the test situation
        tree.delete(tree.findNode(new Interval(8,15)));
        tree.delete(tree.findNode(new Interval(6,38)));
        tree.delete(tree.findNode(new Interval(1,8)));

        // Act
        tree.delete(tree.findNode(new Interval(11,18)));

        // Assert
        assertEquals(3,tree.getRoot().getID());
        assertEquals(1,tree.getRoot().getLeft().getID());
        assertEquals(BLACK,tree.getRoot().getLeft().getColor());
        assertEquals(BLACK,tree.getRoot().getRight().getColor());
        assertFalse(tree.getRoot().getLeft().hasLeft());
        assertTrue(tree.getRoot().getLeft().hasRight());

        assertTrue(buildTree().isBalanced());
    }

    @Test
    public void findExistingNodeByIntervalTest() {
        // Arrange
        var tree = buildTree();

        //Act
        var node = tree.findNode(new Interval(11,18));

        assertEquals(5,node.getID());
    }

    @Test
    public void findNonExistingNodeByIntervalTest() {
        // Arrange
        var tree = buildTree();

        //Act
        var node = tree.findNode(new Interval(11,19));

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
        assertTrue(buildTree().isBalanced());
    }
}
