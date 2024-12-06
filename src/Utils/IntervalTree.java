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
import java.util.Random;

import static Utils.Randomizer.random;

public class IntervalTree implements IIntervalTree<IntervalNode> {
    private IntervalNode root;
    private int nodeCount;
    public IntervalTree() {
        this.root = null;
    }

    public IntervalNode getRoot() {
        return root;
    }

    public void insert(IntervalNode node) {
        IntervalNode newNode = new IntervalNode(node.getInterval(), node.getWeight(), node.getID());
        this.root = insertRecursive(this.root, newNode, null);
        this.nodeCount++;
    }

    public IntervalNode delete(IntervalNode node) {
        var deleteClone = new IntervalNode(node.getInterval(), node.getWeight(), node.getID());

        this.root = deleteRecursive(this.root, node);

        node = deleteClone;
        decoupleNode(node);
        this.nodeCount--;

        return node;
    }

    @Override
    public IntervalNode getRandomNode() {
        List<IntervalNode> nodes = new ArrayList<>();
        inOrderTraversal(root, nodes);

        if (nodes.isEmpty()) {
            return null;
        }
        return nodes.get(random.nextInt(nodes.size()));
    }

    public List<IntervalNode> findAllOverlapping(Interval newInterval) {
        List<IntervalNode> overlappingNodes = new ArrayList<>();
        findOverlappingNodes(root, newInterval, overlappingNodes);
        return overlappingNodes;
    }

    public int calculateExtraBusyTime(Interval newInterval) {
        if (root == null) return newInterval.getEndTime() - newInterval.getStartTime();
        if (newInterval.getEndTime() > root.getMaxEndTime()) {
            // The extra busy time is the difference between the new interval's end time and the current maxEndTime
            return newInterval.getEndTime() - root.getMaxEndTime();
        } else {
            // If the new interval's end time is less than or equal to the maxEndTime, there is no extra busy time
            return 0;
        }
    }

    public int calculateTotalBusyTime() {
        if (root == null) return 0;
        return (root.getMaxEndTime() - root.getMinStartTime());
    }



    // privates


    private IntervalNode insertRecursive(IntervalNode current, IntervalNode node, IntervalNode parent) {
        if (current == null) {
            node.setParent(parent);
            return node;
        }

        // Compare starttime
        if (node.getInterval().getStartTime() < current.getInterval().getStartTime()) {
            current.setLeft(insertRecursive(current.getLeft(), node, current));
        } else if(node.getInterval().getStartTime() > current.getInterval().getStartTime()) {
            current.setRight(insertRecursive(current.getRight(), node, current));
        } else { //Nodes with the same starttime
            if (node.getID() < current.getID()) {
                current.setLeft(insertRecursive(current.getLeft(), node, current));
            } else {
                current.setRight(insertRecursive(current.getRight(), node, current));
            }
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
            IntervalNode successor = findMinNode(current.getRight());
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

    public IntervalNode findMinNode(IntervalNode node) {
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
        if (root.getInterval().getStartTime() < newInterval.getEndTime() && newInterval.getStartTime() < root.getInterval().getEndTime()) {
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
    private void inOrderTraversal(IntervalNode node, List<IntervalNode> nodes) {
        if (node == null) {
            return;
        }

        nodes.add(node);
        inOrderTraversal(node.getLeft(), nodes);
        inOrderTraversal(node.getRight(), nodes);
    }
   @Override
    public IntervalTree deepCopy() {
        IntervalTree copy = new IntervalTree();
        copy.root = deepCopyRecursive(this.root, null);
        return copy;
    }

    private IntervalNode deepCopyRecursive(IntervalNode current, IntervalNode parent) {
        if (current == null) {
            return null;
        }

        // Create a new node with the same interval, weight, and ID
        IntervalNode copiedNode = new IntervalNode(current.getInterval(), current.getWeight(), current.getID());
        copiedNode.setParent(parent); // Set the parent for the copied node

        // Recursively copy left and right children
        copiedNode.setLeft(deepCopyRecursive(current.getLeft(), copiedNode));
        copiedNode.setRight(deepCopyRecursive(current.getRight(), copiedNode));

        // Copy other properties (e.g., maxEndTime, minStartTime)
        copiedNode.setMaxEndTime(current.getMaxEndTime());
        copiedNode.setMinStartTime(current.getMinStartTime());

        return copiedNode;
    }
    public IntervalNode findNode(IntervalNode node) {
        return findNodeInternal(root, node.getInterval(), node.getID());
    }

    @Override
    public IntervalNode getMaxEndTimeNode() {
        IntervalNode newNode = getMaxEndTimeNodeInternal(root);
        if (newNode == null) {
            return null;
        }
        IntervalNode copyNode = new IntervalNode(newNode.getInterval(), newNode.getWeight(), newNode.getID());
        decoupleNode(copyNode);
        return copyNode;
    }
    private IntervalNode getMaxEndTimeNodeInternal(IntervalNode root) {
        if (root == null) {
            return null;
        }

        IntervalNode maxNode = root;
        IntervalNode leftMax = getMaxEndTimeNodeInternal(root.getLeft());
        if (leftMax != null && leftMax.getInterval().getEndTime() > maxNode.getInterval().getEndTime()) {
            maxNode = leftMax;
        }
        IntervalNode rightMax = getMaxEndTimeNodeInternal(root.getRight());
        if (rightMax != null && rightMax.getInterval().getEndTime() > maxNode.getInterval().getEndTime()) {
            maxNode = rightMax;
        }
        return maxNode;
    }
    private IntervalNode findNodeInternal(IntervalNode current, Interval interval, int id) {
        if (current == null) {
            return null;
        }
        if (current.getInterval().getStartTime() == interval.getStartTime() &&
                current.getInterval().getEndTime() == interval.getEndTime() &&
                current.getID() == id) {
            return current;
        }
        if (interval.getStartTime() < current.getInterval().getStartTime()) {
            return findNodeInternal(current.getLeft(), interval, id);
        } else if (interval.getStartTime() > current.getInterval().getStartTime()) {
            return findNodeInternal(current.getRight(), interval, id);
        } // If the start times are equal, check the IDs
        //Here we assume that insertion is also based on ID
        else if (id < current.getID()) {
            return findNodeInternal(current.getLeft(), interval, id);
        } else {
            return findNodeInternal(current.getRight(), interval, id);
        }
    }
    private void decoupleNode(IntervalNode node) {
         node.setLeft(null);
         node.setRight(null);
         node.setParent(null);
    }
    public int getNodeCount(){
        return nodeCount;
    }
}