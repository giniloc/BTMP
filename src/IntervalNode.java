enum Color {
    RED, BLACK
}
public class IntervalNode {

    private Interval interval;
    private int weight;
    private IntervalNode left;
    private IntervalNode right;
    private IntervalNode parent;
    private int ID;
    private int maxEndTime;
    Color color;

    public IntervalNode(Interval interval, int weight, int ID) {
        this.interval = interval;
        this.weight = weight;
        this.color = Color.RED; //new nodes are standard RED
        this.maxEndTime = interval.getEndTime();
        this.left = null;
        this.right = null;
        this.parent = null;
        this.ID = ID;
    }

    public Interval getInterval() {
        return interval;
    }

    public int getWeight() {
        return weight;
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

    public void setColor(Color color) {
        this.color = color;
    }

    public IntervalNode getParent() {
        return parent;
    }

    public void setParent(IntervalNode y) {
        this.parent = y;
    }

    public String getColor() {
        if (color == Color.RED) {
            return "RED";
        } else {
            return "BLACK";
        }
    }
    public int getID() {
        return ID;
    }
}
