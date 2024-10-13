/**
 * This class represents an AVLInterval Tree.
 *
 * The implementation of the AVL Tree and its nodes is based on code from GeeksforGeeks.
 * Original Source: https://www.geeksforgeeks.org/insertion-in-an-avl-tree/
 *
 * Copyright belongs to GeeksforGeeks, and the code has been adapted for use in this project.
 */
package Utils;

import java.util.ArrayList;
import java.util.List;

public class AVLIntervalTree implements IIntervalTree<AVLIntervalNode> {

    private AVLIntervalNode root;

    public AVLIntervalTree() {
        this.root = null;
    }

    public AVLIntervalNode getRoot() {
        return root;
    }

    public void setRoot(AVLIntervalNode root) {
        this.root = root;
    }
    static int height(AVLIntervalNode node) {
        if (node == null) {
            return 0;
        }
        return node.getHeight();
    }

    public void insert(IntervalNode node) {
        AVLIntervalNode newNode = new AVLIntervalNode(node.getInterval(), node.getWeight(), node.getID());
        this.root = insertRecursive(this.root, newNode);
    }

    public List<AVLIntervalNode> findAllOverlapping(Interval newInterval) {
        List<AVLIntervalNode> overlappingNodes = new ArrayList<>();
        findOverlappingNodes(this.root, newInterval, overlappingNodes);
        return overlappingNodes;
    }

    public int calculateExtraBusyTime(Interval newInterval) {
        if (root == null) {
            return 0;
        }
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

    private AVLIntervalNode insertRecursive(AVLIntervalNode current, AVLIntervalNode newNode) {
        if (current == null) {
            return newNode;
        }

        // Insert the new node into the left or right subtree
        if (newNode.getInterval().getStartTime() < current.getInterval().getStartTime()) {
            current.setLeft(insertRecursive(current.getLeft(), newNode));
        } else {
            current.setRight(insertRecursive(current.getRight(), newNode));
        }

        // Update heights of the ancestors
        current.setHeight(1+ Math.max(height(current.getLeft()), height(current.getRight())));

        //update maxEndTime
        int leftMaxEndTime = (current.getLeft() != null) ? current.getLeft().getMaxEndTime() : 0;
        int rightMaxEndTime = (current.getRight() != null) ? current.getRight().getMaxEndTime() : 0;
        current.setMaxEndTime(Math.max(current.getInterval().getEndTime(), Math.max(leftMaxEndTime, rightMaxEndTime)));
        // Calculate balance factor
        int balance = getBalance(current);

        // Balance the tree if necessary
        // Left Left case
        if (balance > 1 && newNode.getInterval().getStartTime() < current.getLeft().getInterval().getStartTime()) {
            return rightRotate(current);
        }

        // Right Right case
        if (balance < -1 && newNode.getInterval().getStartTime() > current.getRight().getInterval().getStartTime()) {
            return leftRotate(current);
        }

        // Left Right case
        if (balance > 1 && newNode.getInterval().getStartTime() > current.getLeft().getInterval().getStartTime()) {
            current.setLeft(leftRotate(current.getLeft()));
            return rightRotate(current);
        }

        // Right Left case
        if (balance < -1 && newNode.getInterval().getStartTime() < current.getRight().getInterval().getStartTime()) {
            current.setRight(rightRotate(current.getRight()));
            return leftRotate(current);
        }

        return current;
    }
    private int getBalance(AVLIntervalNode node) {
        return node == null ? 0 : height(node.getLeft()) - height(node.getRight());
    }
    private AVLIntervalNode rightRotate(AVLIntervalNode y) {
        AVLIntervalNode x = y.getLeft();
        AVLIntervalNode z = x.getRight();

        // Perform rotation
        x.setRight(y);
        y.setLeft(z);

        // Update heights
        y.setHeight(1+ Math.max(height(y.getLeft()), height(y.getRight())));
        x.setHeight(1+ Math.max(height(x.getLeft()), height(x.getRight())));

        return x;
    }

    private AVLIntervalNode leftRotate(AVLIntervalNode y) {
        AVLIntervalNode x = y.getRight();
        AVLIntervalNode z = x.getLeft(); //In this scenario z can be null

        // Perform rotation
        x.setLeft(y);
        y.setRight(z);

        // Update heights
        y.setHeight(1+ Math.max(height(y.getLeft()), height(y.getRight())));
        x.setHeight(1 + Math.max(height(x.getLeft()), height(x.getRight())));

        return x;
    }


    private void findOverlappingNodes(AVLIntervalNode root, Interval newInterval, List<AVLIntervalNode> overlappingNodes) {
        if (root == null) {
            return;
        }

        // Check for overlaps
        if (doIntervalsOverlap(root.getInterval(), newInterval)) {
            overlappingNodes.add(root);
        }

        // If the new interval starts before the current interval, check the left child
        if (root.getLeft() != null && root.getLeft().getMaxEndTime() >= newInterval.getStartTime()) {
            findOverlappingNodes(root.getLeft(), newInterval, overlappingNodes);
        }

        // Always check the right child
        findOverlappingNodes(root.getRight(), newInterval, overlappingNodes);
    }

    // Helper function to check if two intervals overlap
    private boolean doIntervalsOverlap(Interval interval1, Interval interval2) {
        return interval1.getStartTime() < interval2.getEndTime() && interval2.getStartTime() < interval1.getEndTime();
    }

    private int findMinStartTime(AVLIntervalNode node) {
        while (node.getLeft() != null) {
            node = node.getLeft();
        }
        return node.getInterval().getStartTime();
    }
}
