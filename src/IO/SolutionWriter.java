package IO;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import Utils.*;

public class SolutionWriter {
    static Path baseDirectory = Paths.get("./TestInstances");
    public static void writeSolutionToFile(Solution solution, String testInstanceName, String heuristicName, int totalTime) {
        try {
            String directoryPath = "Solutions" + heuristicName; // Change this to "Solutions" to write all solutions for BCHT Heuristic
            File directory = new File(directoryPath);
            if (!directory.exists()) {
                directory.mkdir();
            }
            //TODO GIL --> inputReader krijgt nu ook subFolder ./TestInstances binnen. Ik piets die er hier weer af

            // Sanitize test instance name  (remove special characters)
            String sanitizedTestInstanceName = testInstanceName
                    .substring(0, testInstanceName.length() - 4) //remove .txt
                    .substring(baseDirectory.toString().length()+1).replaceAll("[^a-zA-Z0-9.-]", "_");

            String fileName = directoryPath + File.separator + sanitizedTestInstanceName + "_" + heuristicName + "afterLS" + ".txt";

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
                List<IIntervalTree> intervalTrees = solution.getIntervalTrees();

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


    private static void writeTasksForServer(IIntervalNode node, BufferedWriter writer) throws IOException {
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
