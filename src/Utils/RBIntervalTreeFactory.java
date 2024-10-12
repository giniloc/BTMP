package Utils;

// Factory for RBIntervalTree
public class RBIntervalTreeFactory implements IIntervalTreeFactory<RBIntervalTree> {
    @Override
    public RBIntervalTree create() {
        return new RBIntervalTree();  // Return an instance of RBIntervalTree
    }
}
