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

    //region Insert

    /**
     * Insert a new node into the RBIntervalTree
     * @param node intervalNode to be inserten in the tree
     */
    public void insert(IntervalNode node) {
        //indien IInterval node => (RBIntervalNode)node
        var redBlackNode = new RBIntervalNode (node);
        //TODO If we work with refs than we have to make sure we insert only RED nodes
        //if a deleted node was BLACK and we reinsert it, it will result in an unbalanced tree because we expect inserted nodes to be new nodes => RED nodes
        redBlackNode.setColor(RED);
        this.root = insertRecursive(this.root, redBlackNode);
        insertFixup(redBlackNode);
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
    //endregion

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

    //region Find nodes
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

    private void inorderTraversal(RBIntervalNode node, List<RBIntervalNode> nodes) {
        if (node != null) {
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

    private RBIntervalNode findMin(RBIntervalNode node) {
        while (node.getLeft() != null) {
            node = node.getLeft();
        }
        return node;
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
    //endregion

    //region Rotations
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
    //endregion

    //region Delete
    /**
     * delete a node from the tree and rebalance if needed
     *
     * @param  node node to be deleted in the tree. Need to search for the node first!
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

        //spliced out nodes still has his original parent so we can recalculate the maxTime of this parent + replace method counts on it becasuse it needs to know

        if (deletedColor == RED && (replacement == null || replacement.getColor() == RED)){
            //replace(replacement, x);
            //X will alwyas be NIL
            spliceOut(replacement);
            updateMaxTime(replacement); // replacement node is spliced out but still has his original parent so we can recalculate the maxTime of this parent
            replace(nodeToDelete, replacement);
            if (replacement != null) updateMaxTime(replacement);
            else updateMaxTime(nodeToDelete.getParent());
            decoupleNode(nodeToDelete);
            // Done
            return;
        } else if (deletedColor == RED && (replacement != null && replacement.getColor() == BLACK)) {
            replace(replacement, x);
            updateMaxTime(replacement);
            replace(nodeToDelete, replacement);
            updateMaxTime(replacement);
            replacement.setColor(RED);
            //GOTO CASE
        } else if (deletedColor == BLACK && (replacement != null && replacement.getColor() == RED)) {
            replacement.setColor(BLACK);
            //splice out replacement node
            spliceOut(replacement);
            updateMaxTime(replacement);
            replace(nodeToDelete, replacement);
            updateMaxTime(replacement);

            decoupleNode(nodeToDelete);
            // Done
            return;
        } else if (deletedColor == BLACK && (replacement == null || replacement.getColor() == BLACK) && x != null && x.isRootNode()) {
            replace(nodeToDelete, replacement);
            if (replacement != null) updateMaxTime(replacement);
            else updateMaxTime(nodeToDelete.getParent());

            decoupleNode(nodeToDelete);
            // Done
            return;
        } else if (deletedColor == BLACK && (replacement == null || replacement.getColor() == BLACK) && (x == null || !x.isRootNode())) {
            replace(replacement, x);
            updateMaxTime(replacement);
            replace(nodeToDelete, replacement);
            if (replacement != null) updateMaxTime(replacement);
            else updateMaxTime(nodeToDelete.getParent());
            //GOTO CASE
        }

        if (this.getRoot() == null) {
            decoupleNode(nodeToDelete);
            return;
        }

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

//        if (!isBalanced()){
//            //printTree();
//            System.out.println("Auch!");
//        }

        // make sure the nodeToDelete is no longer attached to any node of the tree
        decoupleNode(nodeToDelete);
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

    private void decoupleNode(RBIntervalNode node){
        if (node.hasLeft() || node.hasRight() || node.getParent() != null){
            node.setRight(null);
            node.setLeft(null);
            node.setParent(null);
            //we do not change the color here. If we reuse this node to insert in a tree, make sure you set the color to RED before you start inserting!
        }
    }

    /**
     * splice out a node. Node.parent is NOT set to null, so we still know where in the tree the spliced out node came from
     * @param node
     */
    private void spliceOut(RBIntervalNode node) {
        if (node == null) return;

        if (node.isLeafNode()) {
            if (node.isLeftChild()) node.getParent().setLeft(null);
            else node.getParent().setRight(null);
        }
        else {
            //node to splice out will only have a right child, otherwise this would not have been the replacement node
            node.getRight().setParent(node.getParent());
            node.getParent().setRight(node.getRight());
        }
    }

    private void replace(RBIntervalNode u, RBIntervalNode v){
        if (u == v) return;

        // if we need to replace the root node by null => tree becomes empty
        if (v == null){
            // node u is the root node and needs to be replaced by null => empty tree
            if (u.isRootNode())
                setRoot(null);
            else
                spliceOut(u);
            return;
        }

        // don't create loops
        if (v.getParent().getLeft() != v){
            v.setLeft(u.getLeft());
            if (v.hasLeft()) v.getLeft().setParent(v);
        }

        // don't create loops
        if (v.getParent().getRight() != v){
            v.setRight(u.getRight());
            if (v.hasRight()) v.getRight().setParent(v);
        }

        v.setParent(u.getParent());

        if (!u.isRootNode())
            if (u.isLeftChild()) u.getParent().setLeft(v);
            else u.getParent().setRight(v);

        if (v.getParent() == null)
            setRoot(v); // new root is now its replacement node v
    }

    /**
     * @param parent node that will be used as the parent of the NIL node
     * */
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

    private void updateMaxTime(RBIntervalNode node){
        if (node == null) return;

        node.updateMaxEndTime();
        updateMaxTime(node.getParent());
    }
    //endregion

    //region OldDelete
//    private RBIntervalNode createNilNode(RBIntervalNode parent){
//        var nullNode = new RBIntervalNode(new Interval(0,0),0,-1);
//        nullNode.setColor(BLACK);
//        nullNode.setParent(parent);
//
//        return nullNode;
//    }
//
//    private boolean isNilNode(RBIntervalNode node){
//        return node.getID() == -1;
//    }
//
//    private RBIntervalNode clone(RBIntervalNode node){
//        var replClone = new RBIntervalNode(node);
//        replClone.setParent(node.getParent());
//        replClone.setLeft(node.getLeft());
//        replClone.setRight(node.getRight());
//        replClone.setColor(node.getColor());
//
//        return replClone;
//    }
//
//    private void updateMaxTime(RBIntervalNode node){
//        if (node == null) return;
//
//        node.updateMaxEndTime();
//        updateMaxTime(node.getParent());
//    }
//
//    /**
//     * delete a node from the tree and rebalance if needed
//     *
//     * @param  node node to be deleted in the tree. Need to search for the node first!
//     */
//    public void delete(IntervalNode node) {
//        var nodeToDelete = findNode(node);
//
//        if (nodeToDelete == null) {
//            return;
//        }
//
//        RBIntervalNode x, replacement, replClone = null;
//        var deletedColor = nodeToDelete.getColor();
//        boolean replacementIsLeftChild = false;
//        boolean nodeToDeleteIsLeftChild = nodeToDelete.isLeftChild();
//
//        if(nodeToDelete.isLeafNode()) {
//            x = null;
//            replacement = x;
//        }
//        else if (nodeToDelete.hasLeft() && !nodeToDelete.hasRight()) {
//            x = nodeToDelete.getLeft();
//            replacement = x;
//            replacementIsLeftChild = true;
//        } else if (!nodeToDelete.hasLeft() && nodeToDelete.hasRight()) {
//            x = nodeToDelete.getRight();
//            replacement = x;
//            replacementIsLeftChild = false;
//        } else {
//            // 2 non-null childs => find successor
//            // find the in-order replacement node (smallest in the right subtree)
//            replacement = findMin(nodeToDelete.getRight());
//            x = replacement.getRight();
//            replacementIsLeftChild = replacement.isLeftChild();
//        }
//
//        if (replacement != null) {
//            replClone = clone(replacement);
//        }
//
//
//        if (deletedColor == RED && (replacement == null || replacement.getColor() == RED)){
//            replace(replacement, x);
//            updateMaxTime(x);
//            replace(nodeToDelete, replacement);
//            updateMaxTime(replacement);
//            // Done
//            return;
//        } else if (deletedColor == RED && (replacement != null && replacement.getColor() == BLACK)) {
//            replace(replacement, x);
//            updateMaxTime(x);
//            replace(nodeToDelete, replacement);
//            updateMaxTime(replacement);
//            replacement.setColor(RED);
//            //GOTO CASE
//        } else if (deletedColor == BLACK && (replacement != null && replacement.getColor() == RED)) {
//            replacement.setColor(BLACK);
//            //splice out replacement node
//            if(replacement.isLeftChild()) replacement.getParent().setLeft(null);
//            else replacement.getParent().setRight(null);
//            updateMaxTime(replacement);
//
//            if (nodeToDelete.isRootNode()) replaceRootNode(nodeToDelete, replacement);
//            else replace(nodeToDelete, replacement);
//            updateMaxTime(replacement);
//            // Done
//            return;
//        } else if (deletedColor == BLACK && (replacement == null || replacement.getColor() == BLACK) && x != null && x.isRootNode()) {
//            if (nodeToDelete.isRootNode()) replaceRootNode(nodeToDelete, replacement);
//            else replace(nodeToDelete, replacement);
//            updateMaxTime(replacement);
//            // Done
//            return;
//        } else if (deletedColor == BLACK && (replacement == null || replacement.getColor() == BLACK) && (x == null || !x.isRootNode())) {
//            replace(replacement, x);
//            updateMaxTime(x);
//            if (nodeToDelete.isRootNode()) replaceRootNode(nodeToDelete, replacement);
//            else replace(nodeToDelete, replacement);
//            updateMaxTime(replacement);
//            //GOTO CASE
//        }
//
//
//
//        if (this.getRoot() == null) return;
//
//        // Fixup needs a Black nilNode if x == null
//        if  (x == null) {
//            if (replClone == null){
//                x = createNilNode(nodeToDelete.getParent());
//                if (nodeToDeleteIsLeftChild)
//                    x.getParent().setLeft(x);
//                else  x.getParent().setRight(x);
//            }
//            else {
//                if (replClone.getParent() != nodeToDelete)
//                    x = createNilNode(replClone.getParent());
//                else
//                    x = createNilNode(replacement);
//
//                if (replacementIsLeftChild)
//                    x.getParent().setLeft(x);
//                else  x.getParent().setRight(x);
//            }
//        }
//
//        // inside deleteFixup, x can be set to another node (see case 2) but this is another x scoped to this method. On return of the method, x will still point to the same node!
//        deleteFixup(x);
//
//        //remove nilNode from tree
//        if (isNilNode(x)){
//            if (x.isLeftChild()) x.getParent().setLeft(null);
//            else x.getParent().setRight(null);
//        }
//
//        // make sure the nodeToDelete is no longer attached to any node of the tree
//        if (nodeToDelete.hasLeft() || nodeToDelete.hasRight() || nodeToDelete.getParent() != null){
//            nodeToDelete.setRight(null);
//            nodeToDelete.setLeft(null);
//            nodeToDelete.setParent(null);
//        }
//    }
//
//    private void deleteFixup(RBIntervalNode x){
//        if (x == null) return;
//
//        //CASE 0
//        if (x.getColor() == RED){
//            x.setColor(BLACK);
//            return;
//        }
//
//        var w = x.getSibling();
//
//        //CASE 1
//        if (x.getColor() == BLACK && w != null && w.getColor() == RED){
//            w.setColor(BLACK);
//            x.getParent().setColor(RED);
//            if (x.isLeftChild()){
//                leftRotate(x.getParent());
//                w = x.getParent().getRight();
//            } else {
//                rightRotate(x.getParent());
//                w = x.getParent().getLeft();
//            }
//        }
//
//        //CASE 2 (no left or right => black)
//        if (x.getColor() == BLACK && (w != null && w.getColor() == BLACK) && (!w.hasLeft() || w.getLeft().getColor() == BLACK) && (!w.hasRight() || w.getRight().getColor() == BLACK)){
//            w.setColor(RED);
//            x = x.getParent();
//            if(x.getColor() == RED){
//                deleteFixup(x);
//                return;
//                // the same as x.setColor(BLACK);
//            }
//            if(x.getColor() == BLACK && x.isRootNode()){ return;}
//            else { deleteFixup(x);}
//        }
//
//        //CASE 3
//        if (x.getColor() == BLACK && w != null && w.getColor() == BLACK){
//            if ((x.isLeftChild() && (w.hasLeft() && w.getLeft().getColor() == RED) && (!w.hasRight() || w.getRight().getColor() == BLACK)) ||
//                    (!x.isLeftChild() && (w.hasRight() && w.getRight().getColor() == RED) && (!w.hasLeft() || w.getLeft().getColor() == BLACK))
//            ){
//                //Color w's child black
//                if (x.isLeftChild()){
//                    w.getLeft().setColor(BLACK);
//                } else {
//                    w.getRight().setColor(BLACK);
//                }
//                w.setColor(RED);
//                if (x.isLeftChild()){
//                    rightRotate(w);
//                    w = x.getParent().getRight();
//                } else {
//                    leftRotate(w);
//                    w = x.getParent().getLeft();
//                }
//                // proceed to case 4
//            }
//        }
//
//        //CASE 4
//        if (
//                (x.getColor() == BLACK && w!= null && w.getColor() == BLACK) &&
//                        ((x.isLeftChild() && (w.hasRight() && w.getRight().getColor() == RED) ) ||
//                                (!x.isLeftChild() && (w.hasLeft() && w.getLeft().getColor() == RED)))
//        ){
//            w.setColor(x.getParent().getColor());
//            x.getParent().setColor(BLACK);
//            if (x.isLeftChild()){
//                w.getRight().setColor(BLACK);
//            } else {
//                w.getLeft().setColor(BLACK);
//            }
//            if (x.isLeftChild()){
//                leftRotate(x.getParent());
//            } else {
//                rightRotate(x.getParent());
//            }
//        }
//    }
//
//
//    // Transplant nodes in place of z
//    private void transplant(RBIntervalNode u, RBIntervalNode v) {
//        if (u.getParent() == null) {
//            root = v;
//        } else if (u.isLeftChild()) {
//            u.getParent().setLeft(v);
//        } else {
//            u.getParent().setRight(v);
//        }
//
//        if (v == null) return;
//
//        v.setParent(u.getParent());
//    }
//
//    /**
//     * u node to be replaced
//     * v node that will replace u
//     */
//    private void replace(RBIntervalNode u, RBIntervalNode v) {
//        if (u == v) return;
//
//        if (v == null){
//            // only allowed on leaf nodes
//            if (!u.isLeafNode()){
//                //TODO throw exception - fault in algorithm
//                return;
//            }
//            transplant(u,v);
//            return;
//        }
//
//        if (v.getParent().getLeft() != v){
//            v.setLeft(u.getLeft());
//            if (u.hasLeft()) u.getLeft().setParent(v);
//        }
//
//        if (v.getParent().getRight() != v){
//            v.setRight(u.getRight());
//            if (u.hasRight()) u.getRight().setParent(v);
//        }
//        // change parent after left and right!!!
//        v.setParent(u.getParent());
//
//        if (u.isRootNode()) {
//            setRoot(v); // new root is now its replacement node v
//            return;
//        }
//
//        if (u.isLeftChild()){
//            u.getParent().setLeft(v);
//        } else {
//            u.getParent().setRight(v);
//        }
//        //v.updateMaxEndTime();
//
//    }
//
//    private void replaceRootNode(RBIntervalNode u, RBIntervalNode v){
//        // if we need to replace the root node by null => tree becomes empty
//        if (v == null){
//            setRoot(null);
//            return;
//        }
//
//        v.setLeft(u.getLeft());
//        v.setRight(u.getRight());
//        v.setParent(u.getParent());
//
//        if (v.hasLeft()) v.getLeft().setParent(v);
//        if (v.hasRight()) v.getRight().setParent(v);
//
//        setRoot(v); // new root is now its replacement node v
//    }

    //endregion

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

    //region PrintTree

    public void printTree() {
        if (this.getRoot() == null) {
            System.out.println("The tree is empty.");
        } else {
            System.out.println("Tree " + this.hashCode());
            printTree(this.getRoot(), "", true);
        }

        System.out.println("-------------------------------------------");
        System.out.println();
    }

    // Helper method to print each node recursively
    private void printTree(IntervalNode node, String prefix, boolean isLeft) {
        if (node != null) {
            System.out.println(prefix + (isLeft ? "L " : "R ") + node);

            // Recur on left and right children
            printTree(node.getLeft(), prefix + (isLeft ? "│   " : "    "), true);
            printTree(node.getRight(), prefix + (isLeft ? "│   " : "    "), false);
        }
    }

    //endregion

    //region DeepCopy
    @Override
    public RBIntervalTree deepCopy() {
        RBIntervalTree newTree = new RBIntervalTree();
        newTree.root = copyNode(this.root, null); // Start copying from the root
        return newTree;
    }

    /**
     * Recursively copies a node and its children.
     *
     * @param node The current node in the original tree.
     * @param parent The parent node in the new tree.
     * @return The copied node in the new tree.
     */
    private RBIntervalNode copyNode(RBIntervalNode node, RBIntervalNode parent) {
        if (node == null) {
            return null;
        }

        RBIntervalNode newNode = new RBIntervalNode(node);
        newNode.setColor(node.getColor());
        newNode.setMinStartTime(node.getMinStartTime());
        newNode.setMaxEndTime(node.getMaxEndTime());
        newNode.setParent(parent);

        newNode.setLeft(copyNode(node.getLeft(), newNode));
        newNode.setRight(copyNode(node.getRight(), newNode));

        return newNode;
    }

    //endregion
}