package Utils;

import java.io.Serializable;

public interface IIntervalNode{
    Interval getInterval();
    int getWeight();
    int getID();
    IIntervalNode getLeft();
    IIntervalNode getRight();
}
