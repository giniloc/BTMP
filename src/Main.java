import java.util.List;

public class Main {
    public static void main(String[] args) {
        InputReader inputReader = new InputReader("n50 t50 LonLr/cap100_n50_t50_LonLr_1.txt");
        System.out.println("Number of VM requests: " + inputReader.getNumberOfVMRequests());
        System.out.println("Server capacity: " + inputReader.getServerCapacity());
        //inputReader.PrintVMRequests();
        List<Request> requests = inputReader.getRequests();

        IntervalTree intervalTree = new IntervalTree();

//        for (Request request : requests) {
//            Interval interval = new Interval(request.getStartTime(), request.getEndTime());
//            IntervalNode node = new IntervalNode(interval, request.getWeight());
//            intervalTree.setRoot(intervalTree.insert(intervalTree.getRoot(), node));
//        }

        for (int i = 0; i < 5 && i < requests.size(); i++) {
            Request request = requests.get(i);
            Interval interval = new Interval(request.getStartTime(), request.getEndTime());
            IntervalNode node = new IntervalNode(interval, request.getWeight());
            intervalTree.setRoot(intervalTree.insert(intervalTree.getRoot(), node));
        }
        // Print tree in order
        IntervalTree.inOrder(intervalTree.getRoot());
        // Test interval overlap
        Interval newInterval = new Interval(10, 20); // Test case
        IntervalNode overlappingNode = intervalTree.isOverlapping(intervalTree.getRoot(), newInterval); //finds first overlapping node

        if (overlappingNode != null) {
            System.out.println("Overlapping interval found: " + overlappingNode.getInterval());
        } else {
            System.out.println("No overlapping interval found.");
        }

        Interval newInterval2 = new Interval(10, 20);
        List<IntervalNode> overlappingNodes = intervalTree.findAllOverlapping(intervalTree.getRoot(), newInterval2); //finds all overlapping nodes

        if (!overlappingNodes.isEmpty()) {
            System.out.println("Overlapping intervals found:");
            for (IntervalNode node : overlappingNodes) {
                System.out.println(node.getInterval());
            }
        } else {
            System.out.println("No overlapping intervals found.");
        }
    }
}
