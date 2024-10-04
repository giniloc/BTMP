package Utils;
public class Interval {

    private int startTime;
    private int endTime;

    public Interval(int startTime, int endTime)
    {
        this.startTime = startTime;
        this.endTime = endTime;
    }
    public int getStartTime() {
        return startTime;
    }

    public int getEndTime() {
        return endTime;
    }

    public String toString() {
        return "[" + this.startTime + "," + this.endTime + "]";
    }
}
