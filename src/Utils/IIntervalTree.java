package Utils;

import java.util.List;

public interface IIntervalTree<T extends IIntervalNode> {
    T getRoot();
    void setRoot(T root);
    void insert(IntervalNode node);
    List<T> findAllOverlapping(Interval newInterval);
    int calculateExtraBusyTime(Interval newInterval);
    int calculateTotalBusyTime();
}
