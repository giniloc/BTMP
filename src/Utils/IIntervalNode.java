package Utils;

public interface IIntervalNode {
    Interval getInterval();
    int getWeight();
    int getID();
    IIntervalNode getLeft();
    IIntervalNode getRight();
}
