package Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static Utils.Color.BLACK;
import static Utils.Color.RED;


public class RBIntervalTree implements IIntervalTree<RBIntervalNode> {

    private RBIntervalNode root;

    public RBIntervalTree() {
        this.root = null;
    }

    public RBIntervalNode getRoot() {
        return root;
    }

    private void setRoot(RBIntervalNode root) {
        this.root = root;
    }

    // Insert a new node into the RBIntervalTree
    public void insert(IntervalNode node) {
        //indien IInterval node => (RBIntervalNode)node
        var redBlackNode = new RBIntervalNode (node);
        this.root = insertRecursive(this.root, redBlackNode);
        // fixInsertion(redBlackNode);
        insertFixup(redBlackNode);
    }

    public int calculateExtraBusyTime(Interval newInterval) {
        if (root == null) {
            return 0;
        }

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

    public List<RBIntervalNode> findAllOverlapping(Interval newInterval) {
        List<RBIntervalNode> overlappingNodes = new ArrayList<>();
        findOverlappingNodes(this.root, newInterval, overlappingNodes);
        return overlappingNodes;
    }

    // Helper function to check if two intervals overlap
    private void findOverlappingNodes(RBIntervalNode root, Interval newInterval, List<RBIntervalNode> overlappingNodes) {
        if (root == null) {
            return;
        }

        if (root.hasRight() && root.getRight().getParent() != root){
            return;
        }
        if (root.hasLeft() && root.getLeft().getParent() != root){
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
    private RBIntervalNode rightRotate(RBIntervalNode x) {
        RBIntervalNode y = x.getLeft();
        x.setLeft(y.getRight());
        if (y.getRight() != null) {
            y.getRight().setParent(x);
        }
        y.setParent(x.getParent());
        if (x.getParent() == null) {
            this.root = y;
        } else if (x == x.getParent().getLeft()) {
            x.getParent().setLeft(y);
        } else {
            x.getParent().setRight(y);
        }
        y.setRight(x);
        x.setParent(y);

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
        if (newNode.getInterval().getStartTime() < current.getInterval().getStartTime() ||
                (newNode.getInterval().getStartTime() == current.getInterval().getStartTime() && newNode.getID() < current.getID())) {
            current.setLeft(insertRecursive(current.getLeft(), newNode));
            current.getLeft().setParent(current);

        } else {
            current.setRight(insertRecursive(current.getRight(), newNode));
            current.getRight().setParent(current);
        }
        current.updateMaxEndTime();
        return current;
    }


    // Fix the Red-Black Tree properties after insertion
//    private void fixInsertion(RBIntervalNode node) {
//        RBIntervalNode parent, grandparent;
//
//        while (node != this.root && node.isRed() && node.getParent().isRed()) {
//            parent = node.getParent();
//            grandparent = parent.getParent();
//
//            if (parent == grandparent.getLeft()) {
//                RBIntervalNode uncle = grandparent.getRight();
//                if (uncle != null && uncle.isRed()) {
//                    // Case 1: Recolor
//                    grandparent.setRed(true);
//                    parent.setRed(false);
//                    uncle.setRed(false);
//                    node = grandparent; //this is to fix double red problems. The while loop will continue from the grandparent.
//                } else {
//                    if (node == parent.getRight()) {
//                        // Case 2: Left rotate
//                        node = parent;
//                        leftRotate(node);
//                    }
//                    // Case 3: Right rotate
//                    parent.setRed(false);
//                    grandparent.setRed(true);
//                    rightRotate(grandparent);
//                }
//            } else {
//                RBIntervalNode uncle = grandparent.getLeft();
//                if (uncle != null && uncle.isRed()) {
//                    // Case 1: Recolor
//                    grandparent.setRed(true);
//                    parent.setRed(false);
//                    uncle.setRed(false);
//                    node = grandparent; //this is to fix double red problems. The while loop will continue from the grandparent.
//                } else {
//                    if (node == parent.getLeft()) {
//                        // Case 2: Right rotate
//                        node = parent;
//                        rightRotate(node);
//                    }
//                    // Case 3: Left rotate
//                    parent.setRed(false);
//                    grandparent.setRed(true);
//                    leftRotate(grandparent);
//                }
//            }
//        }
//        this.root.setRed(false);  // Root must always be black
//    }
    private void insertFixup(RBIntervalNode node) {
        while (node.getParent() != null && node.getParent().isRed()) {
            if (node.getParent() == node.getParent().getParent().getLeft()) {
                RBIntervalNode uncle = node.getParent().getParent().getRight();

                // Case 1: The uncle is red
                if (uncle != null && uncle.isRed()) {
                    node.getParent().setRed(false);
                    uncle.setRed(false);
                    node.getParent().getParent().setRed(true);
                    node = node.getParent().getParent();
                } else {
                    // Case 2: The node is a right child
                    if (node == node.getParent().getRight()) {
                        node = node.getParent();
                        leftRotate(node);
                    }

                    // Case 3: The node is a left child
                    node.getParent().setRed(false);
                    node.getParent().getParent().setRed(true);
                    rightRotate(node.getParent().getParent());
                }
            } else {
                RBIntervalNode uncle = node.getParent().getParent().getLeft();

                // Case 1: The uncle is red
                if (uncle != null && uncle.isRed()) {
                    node.getParent().setRed(false);
                    uncle.setRed(false);
                    node.getParent().getParent().setRed(true);
                    node = node.getParent().getParent();
                } else {
                    // Case 2: The node is a left child
                    if (node == node.getParent().getLeft()) {
                        node = node.getParent();
                        rightRotate(node);
                    }

                    // Case 3: The node is a right child
                    node.getParent().setRed(false);
                    node.getParent().getParent().setRed(true);
                    leftRotate(node.getParent().getParent());
                }
            }
            if (node == root) {
                break;
            }
        }
        root.setRed(false);  // The root must always be black
    }


    public RBIntervalNode getRandomNode() {
        List<RBIntervalNode> nodes = new ArrayList<>();
        inorderTraversal(root, nodes);
        if (nodes.isEmpty()) {
            return null;
        }
        Random random = new Random();
        return nodes.get(random.nextInt(nodes.size()));
    }

    private void inorderTraversal(RBIntervalNode node, List<RBIntervalNode> nodes) {
        if (node != null) {
            if (nodes.contains(node)) {
                System.out.println("Loop detected at node: " + node);
                return; // Beëindig recursie bij detectie van een lus
            }
            inorderTraversal(node.getLeft(), nodes);
            nodes.add(node);
            inorderTraversal(node.getRight(), nodes);
        }
    }

    public RBIntervalNode findNode(IntervalNode node) {
        return findNodeInternal(root, node.getInterval(), node.getID());
    }

    private RBIntervalNode findNodeInternal(RBIntervalNode current, Interval interval, int id) {
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

    private RBIntervalNode createNilNode(RBIntervalNode parent){
        var nullNode = new RBIntervalNode(new Interval(0,0),0,-1);
        nullNode.setColor(BLACK);
        nullNode.setParent(parent);

        return nullNode;
    }

    private boolean isNilNode(RBIntervalNode node){
        return node.getID() == -1;
    }

    private RBIntervalNode clone(RBIntervalNode node){
        var replClone = new RBIntervalNode(node);
        replClone.setParent(node.getParent());
        replClone.setLeft(node.getLeft());
        replClone.setRight(node.getRight());
        replClone.setColor(node.getColor());

        return replClone;
    }

    /**
     * node : node to be deleted in the tree. Need to search for the node first!
     */
    public void delete(IntervalNode node) {
        var nodeToDelete = findNode(node);

        if (nodeToDelete == null) {
            return;
        }

        RBIntervalNode x, replacement, replClone = null;
        var deletedColor = nodeToDelete.getColor();
        boolean replacementIsLeftChild = false;
        boolean nodeToDeleteIsLeftChild = nodeToDelete.isLeftChild();

        if(nodeToDelete.isLeafNode()) {
            x = null;
            replacement = x;
        }
        else if (nodeToDelete.hasLeft() && !nodeToDelete.hasRight()) {
            x = nodeToDelete.getLeft();
            replacement = x;
            replacementIsLeftChild = true;
        } else if (!nodeToDelete.hasLeft() && nodeToDelete.hasRight()) {
            x = nodeToDelete.getRight();
            replacement = x;
            replacementIsLeftChild = false;
        } else {
            // 2 non-null childs => find successor
            // find the in-order replacement node (smallest in the right subtree)
            replacement = findMin(nodeToDelete.getRight());
            x = replacement.getRight();
            replacementIsLeftChild = replacement.isLeftChild();
        }

        if (replacement != null) {
            replClone = clone(replacement);
        }


        if (deletedColor == RED && (replacement == null || replacement.getColor() == RED)){
            replace(replacement, x);
            replace(nodeToDelete, replacement);
            // Done
            return;
        } else if (deletedColor == RED && (replacement != null && replacement.getColor() == BLACK)) {
            replace(replacement, x);
            replace(nodeToDelete, replacement);
//            if (x != null && x == replacement) {
//                x = clone(x);}
            replacement.setColor(RED);
            //GOTO CASE
        } else if (deletedColor == BLACK && (replacement != null && replacement.getColor() == RED)) {
            replacement.setColor(BLACK);
            replace(nodeToDelete, replacement);
            // Done
            return;
        } else if (deletedColor == BLACK && (replacement == null || replacement.getColor() == BLACK) && x != null && x.isRootNode()) {
            replace(nodeToDelete, replacement);
            // Done
            return;
        } else if (deletedColor == BLACK && (replacement == null || replacement.getColor() == BLACK) && (x == null || !x.isRootNode())) {
            replace(replacement, x);
            replace(nodeToDelete, replacement);
            //GOTO CASE
        }

        if (this.getRoot() == null) return;

        // Fixup needs a Black nilNode if x == null
        if  (x == null) {
            if (replClone == null){
                x = createNilNode(nodeToDelete.getParent());
                if (nodeToDeleteIsLeftChild)
                    x.getParent().setLeft(x);
                else  x.getParent().setRight(x);
            }
            else {
                if (replClone.getParent() != nodeToDelete)
                    x = createNilNode(replClone.getParent());
                else
                    x = createNilNode(replacement);

                if (replacementIsLeftChild)
                    x.getParent().setLeft(x);
                else  x.getParent().setRight(x);
            }
        }

        // inside deleteFixup, x can be set to another node (see case 2) but this is another x scoped to this method. On return of the method, x will still point to the same node!
        deleteFixup(x);

        //remove nilNode from tree
        if (isNilNode(x)){
            if (x.isLeftChild()) x.getParent().setLeft(null);
            else x.getParent().setRight(null);
        }
    }

    private void deleteFixup(RBIntervalNode x){
        if (x == null) return;

        //CASE 0
        if (x.getColor() == RED){
            x.setColor(BLACK);
            return;
        }

        var w = x.getSibling();

        //CASE 1
        if (x.getColor() == BLACK && w != null && w.getColor() == RED){
            w.setColor(BLACK);
            x.getParent().setColor(RED);
            if (x.isLeftChild()){
                leftRotate(x.getParent());
                w = x.getParent().getRight();
            } else {
                rightRotate(x.getParent());
                w = x.getParent().getLeft();
            }
        }

        //CASE 2 (no left or right => black)
        if (x.getColor() == BLACK && (w != null && w.getColor() == BLACK) && (!w.hasLeft() || w.getLeft().getColor() == BLACK) && (!w.hasRight() || w.getRight().getColor() == BLACK)){
            w.setColor(RED);
            x = x.getParent();
            if(x.getColor() == RED){
                deleteFixup(x);
                return;
                // the same as x.setColor(BLACK);
            }
            if(x.getColor() == BLACK && x.isRootNode()){ return;}
            else { deleteFixup(x);}
        }

        //CASE 3
        if (x.getColor() == BLACK && w != null && w.getColor() == BLACK){
            if ((x.isLeftChild() && (w.hasLeft() && w.getLeft().getColor() == RED) && (!w.hasRight() || w.getRight().getColor() == BLACK)) ||
                    (!x.isLeftChild() && (w.hasRight() && w.getRight().getColor() == RED) && (!w.hasLeft() || w.getLeft().getColor() == BLACK))
            ){
                //Color w's child black
                if (x.isLeftChild()){
                    w.getLeft().setColor(BLACK);
                } else {
                    w.getRight().setColor(BLACK);
                }
                w.setColor(RED);
                if (x.isLeftChild()){
                    rightRotate(w);
                    w = x.getParent().getRight();
                } else {
                    leftRotate(w);
                    w = x.getParent().getLeft();
                }
                // proceed to case 4
            }
        }

        //CASE 4
        if (
                (x.getColor() == BLACK && w!= null && w.getColor() == BLACK) &&
                        ((x.isLeftChild() && (w.hasRight() && w.getRight().getColor() == RED) ) ||
                                (!x.isLeftChild() && (w.hasLeft() && w.getLeft().getColor() == RED)))
        ){
            w.setColor(x.getParent().getColor());
            x.getParent().setColor(BLACK);
            if (x.isLeftChild()){
                w.getRight().setColor(BLACK);
            } else {
                w.getLeft().setColor(BLACK);
            }
            if (x.isLeftChild()){
                leftRotate(x.getParent());
            } else {
                rightRotate(x.getParent());
            }
        }
    }


    // Transplant nodes in place of z
    private void transplant(RBIntervalNode u, RBIntervalNode v) {
        if (u.getParent() == null) {
            root = v;
        } else if (u.isLeftChild()) {
            u.getParent().setLeft(/*isNilNode(v) ? null :*/ v);
        } else {
            u.getParent().setRight(/*isNilNode(v) ? null :*/ v);
        }

        if (v == null /*|| isNilNode(v)*/) return;

        v.setParent(u.getParent());
    }

    /**
     * u node to be replaced
     * v node that will replace u
     */
    private void replace(RBIntervalNode u, RBIntervalNode v) {
        if (u == v) return;

        if (v == null){
            // only allowed on leaf nodes
            if (!u.isLeafNode()){
                //TODO throw exception - fault in algorithm
                return;
            }
            transplant(u,v);
            return;
        }

        if (v.getParent().getLeft() != v){
            v.setLeft(u.getLeft());
            if (u.hasLeft()) u.getLeft().setParent(v);
        }

        if (v.getParent().getRight() != v){
            v.setRight(u.getRight());
            if (u.hasRight()) u.getRight().setParent(v);
        }
        // change parent after left and right!!!
        v.setParent(u.getParent());

        if (u.isRootNode()) {
            setRoot(v); // new root is now its replacement node v
            return;
        }

        if (u.isLeftChild()){
            u.getParent().setLeft(v);
        } else {
            u.getParent().setRight(v);
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

    public boolean isBalanced() {
        if (root == null) return true; // an empty tree is balanced

        int blackCount = 0;
        RBIntervalNode current = root;
        while (current != null) {
            if (!current.isRed()) {
                blackCount++;
            }
            current = current.getLeft();
        }

        return isBalanced(root, blackCount, 0);
    }

    private boolean isBalanced(RBIntervalNode node, int blackCount, int currentBlackCount) {
        if (node == null) {
            return currentBlackCount == blackCount;
        }

        if (!node.isRed()) {
            currentBlackCount++;
        }
        if (node.isRed()) {
            if ((node.getLeft() != null && node.getLeft().isRed()) ||
                    (node.getRight() != null && node.getRight().isRed())) {
                return false; // a red node cannot have a red child
            }
        }
        // Check if the left and right subtrees have the same number of black nodes
        return isBalanced(node.getLeft(), blackCount, currentBlackCount) &&
                isBalanced(node.getRight(), blackCount, currentBlackCount);
    }
}