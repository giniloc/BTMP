package IO;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class Validator {

    public Validator() {
    }

    public boolean validate(String inputFilePath, String outputFilePath) {
        Set<Integer> requestIds = new HashSet<>();

        // Lees het inputbestand om request IDs te verzamelen
        try (BufferedReader br = new BufferedReader(new FileReader(inputFilePath))) {
            String line;
            boolean firstLine = true; // Om de eerste lijn over te slaan
            while ((line = br.readLine()) != null) {
                // Sla lege regels over
                if (line.trim().isEmpty()) {
                    continue;
                }

                // Sla de eerste lijn over
                if (firstLine) {
                    firstLine = false;
                    continue;
                }

                // Veronderstel dat je de request IDs op basis van tabs leest
                String[] parts = line.split("\t"); // Gebruik \t als delimiter
                int requestId = Integer.parseInt(parts[0].trim()); // Neem aan dat de request ID de eerste kolom is
                requestIds.add(requestId);
            }
        } catch (IOException e) {
            System.err.println("Fout bij het lezen van het inputbestand: " + e.getMessage());
            return false;
        } catch (NumberFormatException e) {
            System.err.println("Fout bij het converteren van een nummer: " + e.getMessage());
            return false;
        }

        // check if the output file contains all request IDs
        Set<Integer> foundRequestIds = new HashSet<>();
        Set<Integer> outputDuplicateRequestIds = new HashSet<>();

        try (BufferedReader br = new BufferedReader(new FileReader(outputFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("Request ID:")) {
                    String[] parts = line.split(":");
                    if (parts.length > 1) {
                        int requestId = Integer.parseInt(parts[1].trim());

                        // Controleer op dubbele request IDs in het outputbestand
                        if (!foundRequestIds.add(requestId)) {
                            outputDuplicateRequestIds.add(requestId);
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Fout bij het lezen van het outputbestand: " + e.getMessage());
            return false;
        } catch (NumberFormatException e) {
            System.err.println("Fout bij het converteren van een nummer in het outputbestand: " + e.getMessage());
            return false;
        }

        // check if there are duplicate request IDs in the output file
        if (!outputDuplicateRequestIds.isEmpty()) {
            System.out.println("Dubbele Request IDs gevonden in het outputbestand: " + outputDuplicateRequestIds);
            return false;
        }

        // check if all request IDs are present in the output file
        for (int requestId : requestIds) {
            if (!foundRequestIds.contains(requestId)) {
                System.out.println("Oplossing mist Request ID: " + requestId);
                return false;
            }
        }

        System.out.println("Validatie succesvol! Alle request IDs zijn aanwezig en er zijn geen dubbele ID's.");
        return true;
    }
}