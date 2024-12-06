package Utils;

import java.util.List;

public interface IIntervalTree<T extends IntervalNode> {
    T getRoot();
    void insert(IntervalNode node);
    List<T> findAllOverlapping(Interval newInterval);
    int calculateExtraBusyTime(Interval newInterval);
    int calculateTotalBusyTime();
    T delete(IntervalNode node);
    T getRandomNode();
    List<T> getRandomNodes(int nodesToRemove);
    IIntervalTree<T> deepCopy();

    T findNode(IntervalNode node);

    T getMaxEndTimeNode();

    int getNodeCount();


}
