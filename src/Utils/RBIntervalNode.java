package Utils;

public class RBIntervalNode extends IntervalNode {
    private Color color;


    public RBIntervalNode(Interval interval, int weight, int vmId) {
        super(interval, weight, vmId);
        this.color = Color.RED;
    }

    // Constructor to create an RBIntervalNode from an existing IntervalNode
    public RBIntervalNode(IntervalNode intervalNode) {
        super(intervalNode.getInterval(), intervalNode.getWeight(), intervalNode.getID());
        this.color = Color.RED;  // Default for a new RBNode
    }
    @Override
    public RBIntervalNode getParent() {
        return (RBIntervalNode) super.getParent();
    }

    @Override
    public void setParent(IntervalNode parent) {
        super.setParent(parent);
    }

    @Override
    public RBIntervalNode getLeft() {
        return (RBIntervalNode) super.getLeft();
    }

    @Override
    public void setLeft(IntervalNode left) {
        super.setLeft(left);
    }

    @Override
    public RBIntervalNode getRight() {
        return (RBIntervalNode) super.getRight();
    }

    @Override
    public void setRight(IntervalNode right) {
        super.setRight(right);
    }

    public void updateMaxEndTime() {
        // Same logic as IntervalNode, update the maxEndTime
        int leftMax = (super.getLeft() != null) ? super.getLeft().getMaxEndTime() : Integer.MIN_VALUE;
        int rightMax = (super.getRight() != null) ? super.getRight().getMaxEndTime() : Integer.MIN_VALUE;
        setMaxEndTime(Math.max(getInterval().getEndTime(), Math.max(leftMax, rightMax)));
    }

    public boolean isRed() {
        return color == Color.RED;
    }

    public void setRed(boolean b) {
        this.color = b ? Color.RED : Color.BLACK;
    }

//    public boolean isOnLeft() {
//        return this == this.getParent().getLeft();
//    }


    public boolean hasRedChild() {
        return getLeft() != null && getLeft().isRed() || getRight() != null && getRight().isRed();
    }

    public void setColor(Color color) {
        this.color = color;
    }
    public Color getColor() {
        return color;
    }

    public boolean hasSibling() {
        return this.getParent() != null && (this.getParent().getLeft() != this && this.getParent().getRight() != this);
    }

    public RBIntervalNode getSibling() {
        if (this.getParent() == null) {
            return null;
        }
        return this.isLeftChild() ? this.getParent().getRight() : this.getParent().getLeft();
    }

    @Override
    public String toString() {
        return super.toString() + ", Color: " + color;
    }

}
