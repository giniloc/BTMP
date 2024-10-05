package Utils;

public class RBIntervalNode extends IntervalNode {
    private Color color;
    private RBIntervalNode parent;
    private RBIntervalNode left;
    private RBIntervalNode right;

    public RBIntervalNode(Interval interval, int weight, int vmId, Color color) {
        super(interval, weight, vmId);
        this.color = color;
    }

    public RBIntervalNode getParent() {
        return parent;
    }

    public void setParent(RBIntervalNode parent) {
        this.parent = parent;
    }

    public RBIntervalNode getLeft() {
        return left;
    }

    public void setLeft(RBIntervalNode left) {
        this.left = left;
    }

    public RBIntervalNode getRight() {
        return right;
    }

    public void setRight(RBIntervalNode right) {
        this.right = right;
    }

    public void updateMaxEndTime() {
        // Same logic as IntervalNode, update the maxEndTime
        int leftMax = (left != null) ? left.getMaxEndTime() : Integer.MIN_VALUE;
        int rightMax = (right != null) ? right.getMaxEndTime() : Integer.MIN_VALUE;
        setMaxEndTime(Math.max(getInterval().getEndTime(), Math.max(leftMax, rightMax)));
    }

    public boolean isRed() {
        return color == Color.RED;
    }

    public void setRed(boolean b) {
        this.color = b ? Color.RED : Color.BLACK;
    }
}
