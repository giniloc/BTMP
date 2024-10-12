package Utils;

public interface IIntervalTreeFactory<T extends IIntervalTree<? extends IIntervalNode>> {
    T create();  // Factory method to create instances of T
}
