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


    //safe cast version
//    @Override
//    public RBIntervalNode getLeft() {
//        IntervalNode leftNode = super.getLeft();
//        if (leftNode instanceof RBIntervalNode) {
//            return (RBIntervalNode) leftNode;
//        }
//        return null;  // Handle gracefully if not an RBIntervalNode
//    }

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
}
