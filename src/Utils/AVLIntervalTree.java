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
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static Utils.Randomizer.random;

public class AVLIntervalTree implements IIntervalTree<AVLIntervalNode> {

    private AVLIntervalNode root;
    private int nodeCount;

    public AVLIntervalTree() {
        this.root = null;
    }

    public AVLIntervalNode getRoot() {
        return root;
    }

    static int height(AVLIntervalNode node) {
        return node == null ? -1 : node.getHeight();
    }

    public void insert(IntervalNode node) {
        AVLIntervalNode newNode = new AVLIntervalNode(node.getInterval(), node.getWeight(), node.getID());
        this.root = insertRecursive(this.root, newNode);
        this.nodeCount++;
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
            if (current.getLeft() != null) {
                current.getLeft().setParent(current);
            }
        } else if (newNode.getInterval().getStartTime() == current.getInterval().getStartTime()) {
            if (newNode.getID() < current.getID()) {
                current.setLeft(insertRecursive(current.getLeft(), newNode));
                if (current.getLeft() != null) {
                    current.getLeft().setParent(current);
                }
            } else {
                current.setRight(insertRecursive(current.getRight(), newNode));
                if (current.getRight() != null) {
                    current.getRight().setParent(current);
                }
            }
        } else {
            current.setRight(insertRecursive(current.getRight(), newNode));
            if (current.getRight() != null) {
                current.getRight().setParent(current);
            }
        }
        updateMaxEndTime(current);
        return rebalance(current);
    }

    public AVLIntervalNode delete(IntervalNode node) {
        var nodeToDelete = findNode(node); // to retrieve the AVLTreeNode to delete
        if (nodeToDelete == null) {
            return null;
        }

        var deleteClone = clone(nodeToDelete);

        this.root = deleteRecursive(this.root, nodeToDelete);

        nodeToDelete = deleteClone;
        decoupleNode(nodeToDelete);
        this.nodeCount--;

        return nodeToDelete;
    }

    private AVLIntervalNode deleteRecursive(AVLIntervalNode current, AVLIntervalNode nodeToDelete) {
        if (current == null) {
            return null;
        }

        if (nodeToDelete.getInterval().getStartTime() < current.getInterval().getStartTime()) {
            current.setLeft(deleteRecursive(current.getLeft(), nodeToDelete));
            if (current.getLeft() != null) {
                current.getLeft().setParent(current);
            }
        } else if (nodeToDelete.getInterval().getStartTime() > current.getInterval().getStartTime()) {
            current.setRight(deleteRecursive(current.getRight(), nodeToDelete));
            if (current.getRight() != null) {
                current.getRight().setParent(current);
            }
        } else if (nodeToDelete.getID() != current.getID()) {
            if (nodeToDelete.getID() < current.getID()) {
                current.setLeft(deleteRecursive(current.getLeft(), nodeToDelete));
                if (current.getLeft() != null) {
                    current.getLeft().setParent(current);
                }
            } else {
                current.setRight(deleteRecursive(current.getRight(), nodeToDelete));
                if (current.getRight() != null) {
                    current.getRight().setParent(current);
                }
            }
        } else {
            //AVLIntervalNode deleteClone = clone(nodeToDelete);
            if (current.getLeft() == null || current.getRight() == null) {
                AVLIntervalNode temp = current.getLeft() != null ? current.getLeft() : current.getRight();
                if (temp != null) {
                    temp.setParent(current.getParent());
                }
                current = temp;
            } else {
                AVLIntervalNode temp = findMinNode(current.getRight());
//                current.getLeft().setParent(deleteClone);
//                current.getRight().setParent(deleteClone);
//                current.setLeft(current.getLeft());
//                current.setRight(current.getRight());
                current.setInterval(temp.getInterval());
                current.setID(temp.getID());
                current.setWeight(temp.getWeight());
                current.setRight(deleteRecursive(current.getRight(), temp));
                if (current.getRight() != null) {
                    current.getRight().setParent(current);
                }
            }
        }

        if (current == null) {
            return null;
        }
        AVLIntervalNode balancedNode = rebalance(current);
        updateMaxEndTime(balancedNode);

        return balancedNode;
    }

    private void decoupleNode(AVLIntervalNode nodeToDelete) {
        if (nodeToDelete.hasLeft()) nodeToDelete.setLeft(null);
        if (nodeToDelete.hasRight()) nodeToDelete.setRight(null);
        if (nodeToDelete.hasParent()) nodeToDelete.setParent(null);

    }


    public AVLIntervalNode findMinNode(AVLIntervalNode node) {
        while (node.getLeft() != null) {
            node = node.getLeft();
        }
        return node;
    }

    public AVLIntervalNode getRandomNode() {
        List<AVLIntervalNode> nodes = new ArrayList<>();
        inOrderTraversal(root, nodes);
        if (nodes.isEmpty()) {
            return null;
        }
        return nodes.get(random.nextInt(nodes.size()));
    }
    @Override
    public List<AVLIntervalNode> getRandomNodes(int nodesToRemove) {
        List<AVLIntervalNode> nodes = new ArrayList<>();
        inOrderTraversal(root, nodes);

        if (nodesToRemove >= nodes.size()) {
            return new ArrayList<>(nodes); 
        }

        Collections.shuffle(nodes, random); 
        return nodes.subList(0, nodesToRemove); 
    }

    private void inOrderTraversal(AVLIntervalNode node, List<AVLIntervalNode> nodes) {
        if (node != null) {
            inOrderTraversal(node.getLeft(), nodes);
            nodes.add(node);
            inOrderTraversal(node.getRight(), nodes);
        }
    }


    private void updateMaxEndTime(AVLIntervalNode node) {
        int leftMaxEndTime = (node.getLeft() != null) ? node.getLeft().getMaxEndTime() : 0;
        int rightMaxEndTime = (node.getRight() != null) ? node.getRight().getMaxEndTime() : 0;
        node.setMaxEndTime(Math.max(node.getInterval().getEndTime(), Math.max(leftMaxEndTime, rightMaxEndTime)));
    }

    private int getBalance(AVLIntervalNode node) {
        return (node == null) ? 0 : height(node.getRight()) - height(node.getLeft());
    }

    private AVLIntervalNode rightRotate(AVLIntervalNode y) {
        AVLIntervalNode x = y.getLeft();
        AVLIntervalNode z = x.getRight();

        x.setRight(y);
        y.setLeft(z);

        if (z != null) {
            z.setParent(y);
        }

        x.setParent(y.getParent());
        y.setParent(x);

        updateHeight(y);
        updateHeight(x);

        updateMaxEndTime(y);
        updateMaxEndTime(x);
        // Update maxEndTime for all ancestors
        AVLIntervalNode current = x;
        while (current != null) {
            updateMaxEndTime(current);
            current = current.getParent();
        }

        return x;
    }

    private AVLIntervalNode leftRotate(AVLIntervalNode x) {
        AVLIntervalNode y = x.getRight();
        AVLIntervalNode z = y.getLeft();

        y.setLeft(x);
        x.setRight(z);

        if (z != null) {
            z.setParent(x);
        }

        y.setParent(x.getParent());
        x.setParent(y);

        updateHeight(x);
        updateHeight(y);

        updateMaxEndTime(x);
        updateMaxEndTime(y);
        // Update maxEndTime for all ancestors
        AVLIntervalNode current = y;
        while (current != null) {
            updateMaxEndTime(current);
            current = current.getParent();
        }

        return y;
    }


    private void findOverlappingNodes(AVLIntervalNode root, Interval newInterval, List<AVLIntervalNode> overlappingNodes) {
        if (root == null) {
            return;
        }

        // Check for overlaps
        if (root.getInterval().getStartTime() < newInterval.getEndTime() && newInterval.getStartTime() < root.getInterval().getEndTime()) {
            overlappingNodes.add(root);
        }

        // If the new interval starts before the current interval, check the left child
        if (root.getLeft() != null && root.getLeft().getMaxEndTime() >= newInterval.getStartTime()) {
            findOverlappingNodes(root.getLeft(), newInterval, overlappingNodes);
        }

        // Always check the right child
        findOverlappingNodes(root.getRight(), newInterval, overlappingNodes);
    }

    private int findMinStartTime(AVLIntervalNode node) {
        while (node.getLeft() != null) {
            node = node.getLeft();
        }
        return node.getInterval().getStartTime();
    }
    public boolean isBalanced() {
        return isBalanced(root);
    }

    private boolean isBalanced(AVLIntervalNode node) {
        if (node == null) {
            return true;
        }

        int balanceFactor = getBalance(node);

        if (balanceFactor < -1 || balanceFactor > 1) {
            return false;
        }

        return isBalanced(node.getLeft()) && isBalanced(node.getRight());
    }
   @Override
    public AVLIntervalTree deepCopy() {
        AVLIntervalTree newTree = new AVLIntervalTree();
        newTree.root = copyNode(this.root); // Start copying from the root
        newTree.setNodeCount(this.getNodeCount());

       return newTree;
    }

    /**
     * Recursively copies a node and its children.
     *
     * @param node The current node in the original tree.
     * @return The copied node in the new tree.
     */
    private AVLIntervalNode copyNode(AVLIntervalNode node) {
        if (node == null) {
            return null;
        }

        // Copy the current node
        AVLIntervalNode newNode = new AVLIntervalNode(
                node.getInterval(), node.getWeight(), node.getID()
        );
        newNode.setHeight(node.getHeight());
        newNode.setMaxEndTime(node.getMaxEndTime());

        // Recursively copy the left and right children
        newNode.setLeft(copyNode(node.getLeft()));
        newNode.setRight(copyNode(node.getRight()));

        // After copying the children, update the maxEndTime based on children values
        updateMaxEndTime(newNode);

        return newNode;
    }
    public AVLIntervalNode findNode(IntervalNode node) {
        return findNodeInternal(root, node.getInterval(), node.getID());
    }

    @Override
    public AVLIntervalNode getMaxEndTimeNode() {
        AVLIntervalNode newNode = getMaxEndTimeNodeInternal(root);
        if (newNode == null) {
            return null;
        }
        AVLIntervalNode copyNode = new AVLIntervalNode(newNode.getInterval(), newNode.getWeight(), newNode.getID());
        decoupleNode(copyNode);
        return copyNode;
    }

    private AVLIntervalNode getMaxEndTimeNodeInternal(AVLIntervalNode node) {
        if (node == null) {
            return null;
        }

        if (node.getRight() != null && node.getRight().getMaxEndTime() == node.getMaxEndTime()) {
            return getMaxEndTimeNodeInternal(node.getRight());
        }

        if (node.getLeft() != null && node.getLeft().getMaxEndTime() == node.getMaxEndTime()) {
            return getMaxEndTimeNodeInternal(node.getLeft());
        }

        return node;
    }

    private AVLIntervalNode findNodeInternal(AVLIntervalNode current, Interval interval, int id) {
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
    public void updateHeight(AVLIntervalNode node) {
        node.setHeight(1 + Math.max(height(node.getLeft()), height(node.getRight())));
    }
    private AVLIntervalNode rebalance(AVLIntervalNode node) {
        updateHeight(node);
        int balance = getBalance(node);
        if (balance > 1) {
            if (height(node.getRight().getRight()) > height(node.getRight().getLeft())) {
                node = leftRotate(node);
            } else {
                node.setRight(rightRotate(node.getRight()));
                node = leftRotate(node);
            }
        } else if (balance < -1) {
            if (height(node.getLeft().getLeft()) > height(node.getLeft().getRight()))
                node = rightRotate(node);
            else {
                node.setLeft(leftRotate(node.getLeft()));
                node = rightRotate(node);
            }
        }
        return node;
    }
    private AVLIntervalNode clone(AVLIntervalNode node){
        var replClone = new AVLIntervalNode(node.getInterval(), node.getWeight(), node.getID());
        replClone.setParent(node.getParent());
        replClone.setLeft(node.getLeft());
        replClone.setRight(node.getRight());
        return replClone;
    }
    public int getNodeCount() {
        return this.nodeCount;
    }
    public void setNodeCount(int nodeCount) {
        this.nodeCount = nodeCount;
    }
}