package Utils;
import java.io.Serializable;

public class Interval implements Serializable {

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
