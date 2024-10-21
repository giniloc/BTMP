/**
 * This class represents a node in the Interval Tree.
 *
 * The implementation of the Interval Tree and its nodes is based on code from GeeksforGeeks.
 * Original Source: https://www.geeksforgeeks.org/interval-tree/
 *
 * Copyright belongs to GeeksforGeeks, and the code has been adapted for use in this project.
 */

package Utils;


public class IntervalNode implements IIntervalNode {

    private Interval interval;
    private int weight;
    private IntervalNode left;
    private IntervalNode right;
    private IntervalNode parent;
    private int ID;
    private int maxEndTime;

    private int minStartTime;




    public IntervalNode(Interval interval, int weight, int ID) {
        this.interval = interval;
        this.weight = weight;
        this.maxEndTime = interval.getEndTime();
        this.left = null;
        this.right = null;
        this.parent = null;
        this.ID = ID;
        this.minStartTime = interval.getStartTime();
    }

    public Interval getInterval() {
        return interval;
    }

    public int getWeight() {
        return weight;
    }
    public int getMinStartTime() {
        return minStartTime;
    }
    public void setMinStartTime(int minStartTime) {
        this.minStartTime = minStartTime;
    }

    public IntervalNode getLeft() {
        return left;
    }

    public IntervalNode getRight() {
        return right;
    }

    public void setLeft(IntervalNode left) {
        this.left = left;
    }

    public void setRight(IntervalNode right) {
        this.right = right;
    }

    public void setMaxEndTime(int maxEndTime) {
        this.maxEndTime = maxEndTime;
    }

    public int getMaxEndTime() {
        return maxEndTime;
    }
    @Override
    public String toString() {
        return "Interval [" + interval.getStartTime() + ", " + interval.getEndTime() + "], Capacity: " + weight;
    }
    public IntervalNode getParent() {
        return parent;
    }

    public void setParent(IntervalNode y) {
        this.parent = y;
    }
    public int getID() {
        return ID;
    }
    public void setInterval(Interval interval) {
        this.interval = interval;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public void setID(int id) {
        this.ID = id;
    }
}
