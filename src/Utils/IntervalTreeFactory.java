package Utils;

// Factory for IntervalTree
public class IntervalTreeFactory implements IIntervalTreeFactory<IntervalTree> {
    @Override
    public IntervalTree create() {
        return new IntervalTree();  // Return an instance of IntervalTree
    }
}
