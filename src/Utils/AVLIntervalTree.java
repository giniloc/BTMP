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
import java.util.Random;

public class AVLIntervalTree implements IIntervalTree<AVLIntervalNode> {

    private AVLIntervalNode root;

    public AVLIntervalTree() {
        this.root = null;
    }

    public AVLIntervalNode getRoot() {
        return root;
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

        if (newNode.getInterval().getStartTime() < current.getInterval().getStartTime()) {
            current.setLeft(insertRecursive(current.getLeft(), newNode));
        }
        else if (newNode.getInterval().getStartTime() == current.getInterval().getStartTime()) {
            if (newNode.getID() < current.getID()) {
                current.setLeft(insertRecursive(current.getLeft(), newNode));
            }
            else {
                current.setRight(insertRecursive(current.getRight(), newNode));
            }
        }
        // Ga naar rechts als de StartTime groter is
        else {
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
    public void delete(AVLIntervalNode node) {
        if (node == null) {
            return;
        }
        this.root = deleteRecursive(this.root, node);
    }

    private AVLIntervalNode deleteRecursive(AVLIntervalNode current, AVLIntervalNode nodeToDelete) {
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
            // Node to delete found
            if((current.getLeft() == null) || (current.getRight() == null)){
                AVLIntervalNode temp = current.getLeft() != null ? current.getLeft() : current.getRight();
                if (temp == null) { // no children
                    temp = current;
                    current = null;
                } else { // one child
                    current = temp;
                }
            } else { // two children
                AVLIntervalNode temp = findMinNode(current.getRight());
                current.setInterval(temp.getInterval());
                current.setID(temp.getID());
                current.setWeight(temp.getWeight());
                current.setRight(deleteRecursive(current.getRight(), temp));
            }
        }
        if (current == null) {
            return current;
        }
        current.setHeight(1 + Math.max(height(current.getLeft()), height(current.getRight())));
        updateMaxEndTime(current);
        int balance = getBalance(current);

        if (balance > 1 && getBalance(current.getLeft()) >= 0) {
            return rightRotate(current);
        }
        if (balance < -1 && getBalance(current.getRight()) <= 0) {
            return leftRotate(current);
        }
        if (balance > 1 && getBalance(current.getLeft()) < 0) {
            current.setLeft(leftRotate(current.getLeft()));
            return rightRotate(current);
        }
        if (balance < -1 && getBalance(current.getRight()) > 0) {
            current.setRight(rightRotate(current.getRight()));
            return leftRotate(current);
        }
        return current;
    }

    private AVLIntervalNode findMinNode(AVLIntervalNode node) {
        while (node.getLeft() != null) {
            node = node.getLeft();
        }
        return node;
    }
    public AVLIntervalNode getRandomNode() {
        List<AVLIntervalNode> nodes = new ArrayList<>();
        inorderTraversal(root, nodes);
        if (nodes.isEmpty()) {
            return null;
        }
        Random random = new Random();
        return nodes.get(random.nextInt(nodes.size()));
    }

    private void inorderTraversal(AVLIntervalNode node, List<AVLIntervalNode> nodes) {
        if (node != null) {
            inorderTraversal(node.getLeft(), nodes);
            nodes.add(node);
            inorderTraversal(node.getRight(), nodes);
        }
    }


    private void updateMaxEndTime(AVLIntervalNode node) {
        int leftMaxEndTime = (node.getLeft() != null) ? node.getLeft().getMaxEndTime() : 0;
        int rightMaxEndTime = (node.getRight() != null) ? node.getRight().getMaxEndTime() : 0;
        node.setMaxEndTime(Math.max(node.getInterval().getEndTime(), Math.max(leftMaxEndTime, rightMaxEndTime)));
    }

    private int getBalance(AVLIntervalNode node) {
        return node == null ? 0 : height(node.getLeft()) - height(node.getRight());
    }
    private AVLIntervalNode rightRotate(AVLIntervalNode y) {
        if (y == null) {
            return null;
        }
        AVLIntervalNode x = y.getLeft();
        if (x == null) {
            return null;
        }
        AVLIntervalNode z = x.getRight();

        // Perform rotation
        x.setRight(y);
        y.setLeft(z);

        // Update heights
        y.setHeight(1+ Math.max(height(y.getLeft()), height(y.getRight())));
        x.setHeight(1+ Math.max(height(x.getLeft()), height(x.getRight())));

        updateMaxEndTime(y);
        updateMaxEndTime(x);

        return x;
    }

    private AVLIntervalNode leftRotate(AVLIntervalNode x) {
        if (x == null) {
            return null;
        }
        AVLIntervalNode y = x.getRight();
        if (y == null) {
            return null;
        }
        AVLIntervalNode z = y.getLeft(); //In this scenario z can be null

        // Perform rotation
        y.setLeft(x);
        x.setRight(z);

        // Update heights
        x.setHeight(1 + Math.max(height(x.getLeft()), height(x.getRight())));
        y.setHeight(1 + Math.max(height(y.getLeft()), height(y.getRight())));

        updateMaxEndTime(x);
        updateMaxEndTime(y);

        return y;
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
