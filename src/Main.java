public class Main {
    public static void main(String[] args) {
        InputReader inputReader = new InputReader("C:\\Users\\Gil\\IdeaProjects\\BTMP\\TestInstances\\n50 t50 LonLr\\cap100_n50_t50_LonLr_1.txt");
        System.out.println("Number of VM requests: " + inputReader.getNumberOfVMRequests());
        System.out.println("Server capacity: " + inputReader.getServerCapacity());

    }
}