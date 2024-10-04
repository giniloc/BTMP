package IO;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import Utils.IntervalNode;
import Utils.IntervalTree;
import Utils.Solution;

public class SolutionWriter {

    public static void writeSolutionToFile(Solution solution, String testInstanceName, String heuristicName, int totalTime) {
        try {
            String directoryPath = "SolutionsBestCapacity"; // Change this to "Solutions" to write all solutions for BCHT Heuristic
            File directory = new File(directoryPath);
            if (!directory.exists()) {
                directory.mkdir();
            }
            // Sanitize test instance name  (remove special characters)
            String sanitizedTestInstanceName = testInstanceName.replaceAll("[^a-zA-Z0-9.-]", "_");

            String fileName = directoryPath + File.separator + sanitizedTestInstanceName + "_" + heuristicName + ".txt";

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
                List<IntervalTree> intervalTrees = solution.getIntervalTrees();

                for (int i = 0; i < intervalTrees.size(); i++) {
                    writer.write("Server " + (i + 1) + ":");
                    writer.newLine();
                    writeTasksForServer(intervalTrees.get(i).getRoot(), writer);
                    writer.newLine();  // Extra newline to separate servers
                }
                writer.write("Total busy time: " + totalTime);
                System.out.println("Solution successfully written to " + fileName);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeTasksForServer(IntervalNode node, BufferedWriter writer) throws IOException {
        if (node == null) {
            return;
        }
        // Inorder traversal
        writeTasksForServer(node.getLeft(), writer);

        writer.write("Request ID: " + node.getID());
        writer.newLine();

        writeTasksForServer(node.getRight(), writer);
    }
}
