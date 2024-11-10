package IO;

import localsearch.LocalSearchResult;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

public class LocalSearchResultsWriter {

    /**
     * Writes the LocalSearchResult data to a CSV file.
     *
     * @param results Map where the key is the filename, and the value is the LocalSearchResult.
     * @param filename Name of the CSV file to write to.
     */
    public static void writeToCsv(Map<String, LocalSearchResult> results, String filename) {
        // Define the folder for results
        String resultsFolder = "LocalSearchResults";

        // Ensure the Results folder exists
        try {
            Files.createDirectories(Paths.get(resultsFolder)); // This will create the Results folder if it doesn't exist
        } catch (IOException e) {
            System.err.println("Error creating directory: " + e.getMessage());
            return; // Exit if we can't create the directory
        }

        // Generate the filename with the enum value and subfolder path
        String outputCsvFile = Paths.get(resultsFolder, filename).toString();


        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputCsvFile))) {
            // Write CSV header
            writer.write("filename,duration,totalBusyTime");
            writer.newLine(); // Move to the next line after header

            // Write each entry from the map
            for (Map.Entry<String, LocalSearchResult> entry : results.entrySet()) {
                String filenameKey = entry.getKey();
                LocalSearchResult result = entry.getValue();

                // Format the line: filename, duration, totalBusyTime
                String line = String.format("%s,%d,%d", filenameKey, result.getDuration(), result.getTotalBusyTime());
                writer.write(line);
                writer.newLine(); // Move to the next line
            }

            System.out.println("Data successfully written to " + filename);

        } catch (IOException e) {
            System.err.println("Error writing to CSV file: " + e.getMessage());
        }
    }
}
