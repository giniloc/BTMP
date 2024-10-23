/**
 * This class represents an Interval Tree.
 *
 * The implementation of the Interval Tree and its nodes is based on code from GeeksforGeeks.
 * Original Source: https://www.geeksforgeeks.org/interval-tree/
 *
 * Copyright belongs to GeeksforGeeks, and the code has been adapted for use in this project.
 */
package Utils;
import java.util.ArrayList;
import java.util.List;

public class IntervalTree implements IIntervalTree<IntervalNode> {
    private IntervalNode root;

    public IntervalTree() {
        this.root = null;
    }

    public IntervalNode getRoot() {
        return root;
    }

    public void insert(IntervalNode node) {
        this.root = insertRecursive(this.root, node, null);
    }
    public void delete(IntervalNode node) {
        this.root = deleteRecursive(this.root, node);
    }

    public List<IntervalNode> findAllOverlapping(Interval newInterval) {
        List<IntervalNode> overlappingNodes = new ArrayList<>();
        findOverlappingNodes((IntervalNode)root, newInterval, overlappingNodes);
        return overlappingNodes;
    }

    public int calculateExtraBusyTime(Interval newInterval) {
        // Check if the new interval ends after the current maxEndTime
        if (newInterval.getEndTime() > root.getMaxEndTime()) {
            // The extra busy time is the difference between the new interval's end time and the current maxEndTime
            return newInterval.getEndTime() - root.getMaxEndTime();
        } else {
            // If the new interval's end time is less than or equal to the maxEndTime, there is no extra busy time
            return 0;
        }
    }

    public int calculateTotalBusyTime() {
        return (root.getMaxEndTime() - root.getMinStartTime());
    }

    // privates

    // Helper function to check if two intervals overlap
    private boolean doIntervalsOverlap(Interval interval1, Interval interval2) {
        return (interval1.getStartTime() < interval2.getEndTime() && interval2.getStartTime() < interval1.getEndTime());
    }

    private IntervalNode insertRecursive(IntervalNode current, IntervalNode node, IntervalNode parent) {
        if (current == null) {
            node.setParent(parent);
            return node;
        }

        // Compare starttime
        if (node.getInterval().getStartTime() < current.getInterval().getStartTime()) {
            current.setLeft(insertRecursive(current.getLeft(), node, current));
        } else {
            current.setRight(insertRecursive(current.getRight(), node, current));
        }

        // Update maxendtime for the node, this is needed for searching the tree
        current.setMaxEndTime(Math.max(current.getMaxEndTime(), node.getInterval().getEndTime()));
        current.setMinStartTime(Math.min(current.getMinStartTime(), node.getInterval().getStartTime()));

        return current;
    }
    private IntervalNode deleteRecursive(IntervalNode current, IntervalNode nodeToDelete) {
        // Search for the node to delete
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

            // Case 1: node has no children
            if (current.getLeft() == null && current.getRight() == null) {
                return null; // Delete the node
            }

            // Case 2: node has 1 child
            if (current.getLeft() == null) {
                IntervalNode temp = current.getRight();
                temp.setParent(current.getParent());
                return temp; // Change node to right child
            } else if (current.getRight() == null) {
                IntervalNode temp = current.getLeft();
                temp.setParent(current.getParent());
                return temp; // Change node to left child
            }

            // Case 3: 2 children, find successor(leftmost node in that subtree) and replace node with it
            IntervalNode successor = findMin(current.getRight());
            current.setInterval(successor.getInterval());
            current.setWeight(successor.getWeight());
            current.setID(successor.getID());
            successor.setParent(current.getParent());
            current.setRight(deleteRecursive(current.getRight(), successor));
        }

        // Change the maxEndTime for the entire tree
        current.setMaxEndTime(Math.max(current.getInterval().getEndTime(),
                Math.max(getMaxEndTime(current.getLeft()), getMaxEndTime(current.getRight()))));
        current.setMinStartTime(Math.min(current.getInterval().getStartTime(),
                Math.min(getMinStartTime(current.getLeft()), getMinStartTime(current.getRight()))));

        return current;
    }

    private IntervalNode findMin(IntervalNode node) {
        while (node.getLeft() != null){
            node = node.getLeft();
        }
        return node;
    }
    private int getMaxEndTime(IntervalNode node) {
        if (node == null) {
            return Integer.MIN_VALUE;
        }
        return node.getMaxEndTime();
    }
    private int getMinStartTime(IntervalNode node) {
        if (node == null) {
            return Integer.MAX_VALUE;
        }
        return node.getMinStartTime();
    }

    private void findOverlappingNodes(IntervalNode root, Interval newInterval, List<IntervalNode> overlappingNodes) {
        //base case: if we reach a null node, there is no overlap
        if (root == null) {
            return;
        }

        // Check if the current node overlaps with the new interval
        if (doIntervalsOverlap(root.getInterval(), newInterval)) {
            overlappingNodes.add(root);
        }

        // if the maximum endtime of the left child is greater than the starttime of the new interval,
        // then there is overlap in the left subtree
        if (root.getLeft() != null && root.getLeft().getMaxEndTime() >= newInterval.getStartTime()) {
            findOverlappingNodes(root.getLeft(), newInterval, overlappingNodes);
        }

        // Also check the right subtree
        findOverlappingNodes(root.getRight(), newInterval, overlappingNodes);
    }
}