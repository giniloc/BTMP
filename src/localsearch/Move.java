package localsearch;

import Utils.IIntervalNode;
import Utils.IIntervalTree;
import Utils.IntervalNode;

public class Move<T extends IntervalNode> {
    private boolean isDelete;
    private IIntervalTree<T> tree;
    private T node;

    public Move(boolean isDelete, IIntervalTree<T> tree, T node) {
        this.isDelete = isDelete;
        this.tree = tree;
        this.node = node;
    }

    public IIntervalTree<T> getTree() {
        return tree;
    }

    public T getNode() {
        return node;
    }

    public boolean isDelete() {
        return isDelete;
    }
}
