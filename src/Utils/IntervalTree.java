/**
 * This class represents a node in the Interval Tree.
 *
 * The implementation of the Interval Tree and its nodes is based on code from GeeksforGeeks.
 * Original Source: https://www.geeksforgeeks.org/interval-tree/
 *
 * Copyright belongs to GeeksforGeeks, and the code has been adapted for use in this project.
 */
package Utils;
import java.util.ArrayList;
import java.util.List;


public class IntervalTree {
    private IntervalNode root;

    public IntervalTree() {
        this.root = null;
    }

    public IntervalNode getRoot() {
        return root;
    }

    public void setRoot(IntervalNode root) {
        this.root = root;
    }

    public void insert(IntervalNode node) {
        IntervalNode newNode = new IntervalNode(node.getInterval(), node.getWeight(), node.getID());
        this.root = insertRecursive(this.root, newNode);
    }
    private IntervalNode insertRecursive(IntervalNode current, IntervalNode node) {
        if (current == null) {
            return node;
        }

        // Compare starttime
        if (node.getInterval().getStartTime() < current.getInterval().getStartTime()) {
            current.setLeft(insertRecursive(current.getLeft(), node));
        } else {
            current.setRight(insertRecursive(current.getRight(), node));
        }

        // Update maxendtime for the node, this is needed for searching the tree
        current.setMaxEndTime(Math.max(current.getMaxEndTime(), node.getInterval().getEndTime()));


        return current;
    }

//    public static void inOrder(IntervalNode root) {
//        if (root == null) {
//            return;
//        }
//        inOrder(root.getLeft());
//        System.out.println(root);
//        inOrder(root.getRight());
//    }
//    public IntervalNode isOverlapping(IntervalNode root, Interval newInterval) {
//        // Base case
//        if (root == null) {
//            return null;
//        }
//        if (doIntervalsOverlap(root.getInterval(), newInterval)) {
//            return root;
//        }
//        // if the new interval starts before the root interval, check the left subtree
//        if (root.getLeft() != null && root.getLeft().getMaxEndTime() >= newInterval.getStartTime()) {
//            return isOverlapping(root.getLeft(), newInterval);
//            return isOverlapping(root.getLeft(), newInterval);
//        }
//
//        // else search the right subtree
//        return isOverlapping(root.getRight(), newInterval);
//    }

    // Helper function to check if two intervals overlap
    private boolean doIntervalsOverlap(Interval interval1, Interval interval2) {
        return (interval1.getStartTime() < interval2.getEndTime() && interval2.getStartTime() < interval1.getEndTime());
    }

    public List<IntervalNode> findAllOverlapping(IntervalNode root, Interval newInterval) {
        List<IntervalNode> overlappingNodes = new ArrayList<>();
        findOverlappingNodes(root, newInterval, overlappingNodes);
        return overlappingNodes;
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
        return (root.getMaxEndTime() - root.getInterval().getStartTime());
    }

}