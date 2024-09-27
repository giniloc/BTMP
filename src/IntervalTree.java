import java.util.ArrayList;
import java.util.List;

public class IntervalTree {
    private IntervalNode root;

    public IntervalTree() {
        this.root = null;
    }

    public IntervalNode getRoot() {
        return root;
    }

    public void setRoot(IntervalNode root) {
        this.root = root;
    }

    public IntervalNode insert(IntervalNode root, IntervalNode node) {
        if (root == null) {
            return node;
        }

        // Vergelijk de starttijd om te bepalen waar het node ingevoegd wordt
        if (node.getInterval().getStartTime() < root.getInterval().getStartTime()) {
            root.setLeft(insert(root.getLeft(), node));
        } else {
            root.setRight(insert(root.getRight(), node));
        }

        // Update de maxSubtreeWeight om het maximale eindpunt in de subtree bij te houden
        root.setMaxEndTime(Math.max(root.getMaxEndTime(), node.getInterval().getEndTime()));

        return root;
    }

    public static void inOrder(IntervalNode root) {
        if (root == null) {
            return;
        }
        inOrder(root.getLeft());
        System.out.println(root);
        inOrder(root.getRight());
    }
    public IntervalNode isOverlapping(IntervalNode root, Interval newInterval) {
        // Base case
        if (root == null) {
            return null;
        }
        if (doIntervalsOverlap(root.getInterval(), newInterval)) {
            return root;
        }
        // if the new interval starts before the root interval, check the left subtree
        if (root.getLeft() != null && root.getLeft().getMaxEndTime() >= newInterval.getStartTime()) {
            return isOverlapping(root.getLeft(), newInterval);
        }

        // else search the right subtree
        return isOverlapping(root.getRight(), newInterval);
    }

    // Helper function to check if two intervals overlap
    private boolean doIntervalsOverlap(Interval interval1, Interval interval2) {
        return (interval1.getStartTime() < interval2.getEndTime() && interval2.getStartTime() < interval1.getEndTime());
    }
    public List<IntervalNode> findAllOverlapping(IntervalNode root, Interval newInterval) {
        List<IntervalNode> overlappingNodes = new ArrayList<>();
        findOverlappingNodes(root, newInterval, overlappingNodes);
        return overlappingNodes;
    }

    private void findOverlappingNodes(IntervalNode root, Interval newInterval, List<IntervalNode> overlappingNodes) {
        //base case: if we reach a null node, there is no overlap
        if (root == null) {
            return;
        }

        // Check if the current node overlaps with the new interval
        if (doIntervalsOverlap(root.getInterval(), newInterval)) {
            overlappingNodes.add(root);
        }

        // if the maximum end time of the left child is greater than the start time of the new interval,
        // then there is overlap in the left subtree
        if (root.getLeft() != null && root.getLeft().getMaxEndTime() >= newInterval.getStartTime()) {
            findOverlappingNodes(root.getLeft(), newInterval, overlappingNodes);
        }

        // Also check the right subtree
        findOverlappingNodes(root.getRight(), newInterval, overlappingNodes);
    }
}
