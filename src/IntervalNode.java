public class IntervalNode {

    private Interval interval;
    private int weight;
    private IntervalNode left;
    private IntervalNode right;
    private int maxEndTime;

    public IntervalNode(Interval interval, int weight) {
        this.interval = interval;
        this.weight = weight;
        this.maxEndTime = interval.getEndTime();
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
}
