package Utils;

import java.io.Serializable;
import java.util.List;

public interface IIntervalTree<T extends IIntervalNode> extends Serializable {
    T getRoot();
    void insert(IntervalNode node);
    List<T> findAllOverlapping(Interval newInterval);
    int calculateExtraBusyTime(Interval newInterval);
    int calculateTotalBusyTime();
    void delete(IntervalNode node);
    T getRandomNode();
    IIntervalTree<T> deepCopy();

}
