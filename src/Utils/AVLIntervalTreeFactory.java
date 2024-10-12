package Utils;

public class AVLIntervalTreeFactory implements IIntervalTreeFactory<AVLIntervalTree> {
    @Override
    public AVLIntervalTree create() {
        return new AVLIntervalTree();
    }
}
