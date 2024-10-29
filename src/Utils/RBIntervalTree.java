package Utils;

import java.util.ArrayList;
import java.util.List;


public class RBIntervalTree implements IIntervalTree<RBIntervalNode> {

    private RBIntervalNode root;

    public RBIntervalTree() {
        this.root = null;
    }

    public RBIntervalNode getRoot() {
        return root;
    }

    // Insert a new node into the RBIntervalTree
    public void insert(IntervalNode node) {
        //indien IInterval node => (RBIntervalNode)node
        var redBlackNode = new RBIntervalNode (node);
        this.root = insertRecursive(this.root, redBlackNode);
        fixInsertion(redBlackNode);
    }
    public RBIntervalNode delete(RBIntervalNode nodeToDelete) {
        return deleteRecursive(this.root, nodeToDelete);
    }

    public List<RBIntervalNode> findAllOverlapping(Interval newInterval) {
        List<RBIntervalNode> overlappingNodes = new ArrayList<>();
        findOverlappingNodes(this.root, newInterval, overlappingNodes);
        return overlappingNodes;
    }

    public int calculateExtraBusyTime(Interval newInterval) {
        // Check if the new interval ends after the current maxEndTime
        if (newInterval.getEndTime() > root.getMaxEndTime()) {
            return newInterval.getEndTime() - root.getMaxEndTime();
        } else {
            return 0;
        }
    }

    public int calculateTotalBusyTime() {
        if (root == null) {
            return 0;
        }
        int minStartTime = findMinStartTime(root);

        int maxEndTime = root.getMaxEndTime();

        return maxEndTime - minStartTime;
    }



    // Helper function to check if two intervals overlap
    private void findOverlappingNodes(RBIntervalNode root, Interval newInterval, List<RBIntervalNode> overlappingNodes) {
        if (root == null) {
            return;
        }

        // Check if current node's interval overlaps with the new interval
        if (doIntervalsOverlap(root.getInterval(), newInterval)) {
            overlappingNodes.add(root);
        }

        // If the maxEndTime of the left child is greater than or equal to the start time of the new interval,
        // check the left subtree
        if (root.getLeft() != null && root.getLeft().getMaxEndTime() >= newInterval.getStartTime()) {
            findOverlappingNodes(root.getLeft(), newInterval, overlappingNodes);
        }

        // Always check the right subtree
        findOverlappingNodes(root.getRight(), newInterval, overlappingNodes);
    }

    // Helper function to perform left rotation
    private RBIntervalNode leftRotate(RBIntervalNode x) {
        RBIntervalNode y = x.getRight();
        x.setRight(y.getLeft());
        if (y.getLeft() != null) {
            y.getLeft().setParent(x);
        }
        y.setParent(x.getParent());
        if (x.getParent() == null) {
            this.root = y;
        } else if (x == x.getParent().getLeft()) {
            x.getParent().setLeft(y);
        } else {
            x.getParent().setRight(y);
        }
        y.setLeft(x);
        x.setParent(y);

        // Update maxEndTime
        x.updateMaxEndTime();
        y.updateMaxEndTime();

        return y;
    }

    // Helper function to perform right rotation
    private RBIntervalNode rightRotate(RBIntervalNode y) {
        RBIntervalNode x = y.getLeft();
        y.setLeft(x.getRight());
        if (x.getRight() != null) {
            x.getRight().setParent(y);
        }
        x.setParent(y.getParent());
        if (y.getParent() == null) {
            this.root = x;
        } else if (y == y.getParent().getLeft()) {
            y.getParent().setLeft(x);
        } else {
            y.getParent().setRight(x);
        }
        x.setRight(y);
        y.setParent(x);

        // Update maxEndTime
        y.updateMaxEndTime();
        x.updateMaxEndTime();

        return x;
    }

    // Recursive function to insert the new node in the correct position
    private RBIntervalNode insertRecursive(RBIntervalNode current, RBIntervalNode newNode) {
        if (current == null) {
            return newNode;
        }

        // Compare start time for insertion position
        if (newNode.getInterval().getStartTime() < current.getInterval().getStartTime()) {
            current.setLeft(insertRecursive(current.getLeft(), newNode));
            current.getLeft().setParent(current);
        } else {
            current.setRight(insertRecursive(current.getRight(), newNode));
            current.getRight().setParent(current);
        }

        // Update the maxEndTime for the current node
        current.updateMaxEndTime();
        return current;
    }

    // Fix the Red-Black Tree properties after insertion
    private void fixInsertion(RBIntervalNode node) {
        RBIntervalNode parent, grandparent;

        while (node != this.root && node.isRed() && node.getParent().isRed()) {
            parent = node.getParent();
            grandparent = parent.getParent();

            if (parent == grandparent.getLeft()) {
                RBIntervalNode uncle = grandparent.getRight();
                if (uncle != null && uncle.isRed()) {
                    // Case 1: Recolor
                    grandparent.setRed(true);
                    parent.setRed(false);
                    uncle.setRed(false);
                    node = grandparent; //this is to fix double red problems. The while loop will continue from the grandparent.
                } else {
                    if (node == parent.getRight()) {
                        // Case 2: Left rotate
                        node = parent;
                        leftRotate(node);
                    }
                    // Case 3: Right rotate
                    parent.setRed(false);
                    grandparent.setRed(true);
                    rightRotate(grandparent);
                }
            } else {
                RBIntervalNode uncle = grandparent.getLeft();
                if (uncle != null && uncle.isRed()) {
                    // Case 1: Recolor
                    grandparent.setRed(true);
                    parent.setRed(false);
                    uncle.setRed(false);
                    node = grandparent; //this is to fix double red problems. The while loop will continue from the grandparent.
                } else {
                    if (node == parent.getLeft()) {
                        // Case 2: Right rotate
                        node = parent;
                        rightRotate(node);
                    }
                    // Case 3: Left rotate
                    parent.setRed(false);
                    grandparent.setRed(true);
                    leftRotate(grandparent);
                }
            }
        }
        this.root.setRed(false);  // Root must always be black
    }
    private RBIntervalNode deleteRecursive(RBIntervalNode current, RBIntervalNode nodeToDelete) {
        if (current == null) {
            return null;
        }
        // BST deletion process
        if (nodeToDelete.getInterval().getStartTime() < current.getInterval().getStartTime()) {
            current.setLeft(deleteRecursive(current.getLeft(), nodeToDelete));
        } else if (nodeToDelete.getInterval().getStartTime() > current.getInterval().getStartTime()) {
            current.setRight(deleteRecursive(current.getRight(), nodeToDelete));
        } else if (nodeToDelete.getID() != current.getID()) {
            // If startTime is the same but ID is different, keep searching
            if (nodeToDelete.getID() < current.getID()) {
                current.setLeft(deleteRecursive(current.getLeft(), nodeToDelete));
            } else {
                current.setRight(deleteRecursive(current.getRight(), nodeToDelete));
            }
        } else {

            // Case 1: No children (leaf node)
            if (current.getLeft() == null && current.getRight() == null) {
                // Check if it's a black node causing "double black"
                if (!current.isRed()) {
                    fixDoubleBlack(current);
                }
                return null; // Node is deleted, a red node can be safely deleted
            }

            // Case 2: One child
            if (current.getLeft() == null || current.getRight() == null) {
                RBIntervalNode child = (current.getLeft() != null) ? current.getLeft() : current.getRight();
                if (!current.isRed()) {
                    if (child != null && child.isRed()) {
                        // Case: Red child replacing black parent
                        child.setRed(false);  // Recolor the child black
                    } else {
                        fixDoubleBlack(current);  // Handle double black situation
                    }
                }
                child.setParent(current.getParent());
                return child; // Bypass current node
            }

            // Case 3: Two children, find the in-order successor (smallest in the right subtree)
            RBIntervalNode successor = findMin(current.getRight());
            current.setInterval(successor.getInterval()); // Copy data from successor
            current.setID(successor.getID());
            current.setWeight(successor.getWeight());
            current.setRight(
                    deleteRecursive(current.getRight(), successor)); // Delete successor
        }

        // Return the (possibly updated) node
        return current;
    }


    // Handle the "double black" situation
    private void fixDoubleBlack(RBIntervalNode x) {
        if (x == root) {
            return;
        }
        RBIntervalNode sibling = x.getSibling();
        RBIntervalNode parent = x.getParent();

        if (sibling == null) {
            // No sibling, push double black upwards
            fixDoubleBlack(parent);
        } else {
            if (sibling.isRed()) {
                // Sibling is red
                parent.setRed(true);  // Parent becomes red
                sibling.setRed(false);  // Sibling becomes black

                if (sibling.isOnLeft()) {
                    rightRotate(parent);
                } else {
                    leftRotate(parent);
                }
                fixDoubleBlack(x);
            } else {
                if (sibling.hasRedChild()) {
                    if (sibling.getLeft() != null && sibling.getLeft().isRed()) {
                        if (sibling.isOnLeft()) {
                            sibling.getLeft().setColor(sibling.getColor());
                            sibling.setColor(parent.getColor());
                            rightRotate(parent);
                        } else {
                            sibling.getLeft().setColor(parent.getColor());
                            rightRotate(sibling);
                            leftRotate(parent);
                        }
                    } else {
                        if (sibling.isOnLeft()) {
                            sibling.getRight().setColor(parent.getColor());
                            leftRotate(sibling);
                            rightRotate(parent);
                        } else {
                            sibling.getRight().setColor(sibling.getColor());
                            sibling.setColor(parent.getColor());
                            leftRotate(parent);
                        }
                    }
                    parent.setRed(false);
                } else {
                    sibling.setRed(true);
                    if (!parent.isRed()) {
                        fixDoubleBlack(parent);
                    } else {
                        parent.setRed(false);
                    }
                }
            }
        }
    }

    private RBIntervalNode findMin(RBIntervalNode node) {
        while (node.getLeft() != null) {
            node = node.getLeft();
        }
        return node;
    }


    private boolean doIntervalsOverlap(Interval interval1, Interval interval2) {
        return interval1.getStartTime() < interval2.getEndTime() && interval2.getStartTime() < interval1.getEndTime();
    }

    private int findMinStartTime(RBIntervalNode node) {
        // the min start time is the start time of the leftmost node in the tree
        while (node.getLeft() != null) {
            node = node.getLeft();
        }
        return node.getInterval().getStartTime();
    }

}
