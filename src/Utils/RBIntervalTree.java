package Utils;

import java.util.ArrayList;
import java.util.List;


public class RBIntervalTree {

    private RBIntervalNode root;

    public RBIntervalTree() {
        this.root = null;
    }

    public RBIntervalNode getRoot() {
        return root;
    }

    public void setRoot(RBIntervalNode root) {
        this.root = root;
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

    // Insert a new node into the RBIntervalTree
    public void insert(RBIntervalNode node) {
        this.root = insertRecursive(this.root, node);
        fixInsertion(node);
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

    public List<RBIntervalNode> findAllOverlapping(Interval newInterval) {
        List<RBIntervalNode> overlappingNodes = new ArrayList<>();
        findOverlappingNodes(this.root, newInterval, overlappingNodes);
        return overlappingNodes;
    }

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

    // Helper function to check if two intervals overlap
    private boolean doIntervalsOverlap(Interval interval1, Interval interval2) {
        return interval1.getStartTime() < interval2.getEndTime() && interval2.getStartTime() < interval1.getEndTime();
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

    private int findMinStartTime(RBIntervalNode node) {
        // the min start time is the start time of the leftmost node in the tree
        while (node.getLeft() != null) {
            node = node.getLeft();
        }
        return node.getInterval().getStartTime();
    }


}
