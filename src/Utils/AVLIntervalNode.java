/**
 * This class represents an AVLInterval Node.
 *
 * The implementation of the AVL Tree and its nodes is based on code from GeeksforGeeks.
 * Original Source: https://www.geeksforgeeks.org/insertion-in-an-avl-tree/
 *
 * Copyright belongs to GeeksforGeeks, and the code has been adapted for use in this project.
 */
package Utils;

public class AVLIntervalNode extends IntervalNode {
    private int height;

    public AVLIntervalNode(Interval interval, int weight, int ID) {
        super(interval, weight, ID);
        this.height = 0;
    }
    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
    @Override
    public AVLIntervalNode getParent() {
        return (AVLIntervalNode) super.getParent();
    }

    @Override
    public void setParent(IntervalNode parent) {
        super.setParent(parent);
    }

    @Override
    public AVLIntervalNode getLeft() {
        return (AVLIntervalNode) super.getLeft();
    }

    @Override
    public void setLeft(IntervalNode left) {
        super.setLeft(left);
    }

    @Override
    public AVLIntervalNode getRight() {
        return (AVLIntervalNode) super.getRight();
    }

    @Override
    public void setRight(IntervalNode right) {
        super.setRight(right);
    }

    public boolean hasParent() {
        return getParent() != null;
    }
}