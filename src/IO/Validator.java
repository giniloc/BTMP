package IO;
import java.io.*;
import java.util.*;

public class Validator {

    static class Request {
        int id;
        int startTime;
        int endTime;
        int weight;

        public Request(int id, int startTime, int endTime, int weight) {
            this.id = id;
            this.startTime = startTime;
            this.endTime = endTime;
            this.weight = weight;
        }
    }

    private static final int SERVER_CAPACITY = 100;  // Constante capaciteit voor elke server

    public static void main(String[] args) throws IOException {
        String inputFilePath = "TestInstances/n200 t200 LonLr/cap100_n200_t200_LonLr_5.txt";
        String solutionFilePath = "SolutionsBCHTAVL/n200_t200_LonLr_cap100_n200_t200_LonLr_5.txt_BCHTAVL.txt";

        Map<Integer, Request> requests = parseInputFile(inputFilePath);
        Map<Integer, List<Integer>> solutionAssignments = parseSolutionFile(solutionFilePath);

        validateSolution(requests, solutionAssignments);
    }

    private static Map<Integer, Request> parseInputFile(String inputFilePath) throws IOException {
        Map<Integer, Request> requests = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(inputFilePath))) {
            String line;

            // Skip the first line
            br.readLine();

            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\t");
                if (parts.length == 4) {
                    int id = Integer.parseInt(parts[0]);
                    int startTime = Integer.parseInt(parts[1]);
                    int endTime = Integer.parseInt(parts[2]);
                    int busyTime = Integer.parseInt(parts[3]);
                    requests.put(id, new Request(id, startTime, endTime, busyTime));
                }
            }
        }
        return requests;
    }

    private static Map<Integer, List<Integer>> parseSolutionFile(String solutionFilePath) throws IOException {
        Map<Integer, List<Integer>> serverAssignments = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(solutionFilePath))) {
            String line;
            int currentServer = -1;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("Server")) {
                    currentServer = Integer.parseInt(line.split(" ")[1].replace(":", ""));
                    serverAssignments.putIfAbsent(currentServer, new ArrayList<>());
                } else if (line.startsWith("Request ID:")) {
                    int requestId = Integer.parseInt(line.split(" ")[2]);
                    serverAssignments.get(currentServer).add(requestId);
                }
            }
        }
        return serverAssignments;
    }

    private static void validateSolution(Map<Integer, Request> requests, Map<Integer, List<Integer>> solutionAssignments) {
        // Controleer of alle requests zijn toegewezen
        Set<Integer> assignedRequestIds = new HashSet<>();
        solutionAssignments.values().forEach(assignedRequestIds::addAll);

        for (int requestId : requests.keySet()) {
            if (!assignedRequestIds.contains(requestId)) {
                System.out.println("Error: Request ID " + requestId + " is not assigned in the solution.");
            }
        }

        // Controleer de capaciteitsbeperkingen per server met tijdlijn
        boolean capacityViolated = false;
        for (int serverId : solutionAssignments.keySet()) {
            List<Request> serverRequests = new ArrayList<>();
            for (int requestId : solutionAssignments.get(serverId)) {
                serverRequests.add(requests.get(requestId));
            }

            // Tijdlijn bijhouden: een map met de cumulatieve `weight`-wijzigingen bij elke tijdsstap
            TreeMap<Integer, Integer> timeline = new TreeMap<>();
            for (Request request : serverRequests) {
                timeline.put(request.startTime, timeline.getOrDefault(request.startTime, 0) + request.weight);
                timeline.put(request.endTime, timeline.getOrDefault(request.endTime, 0) - request.weight);
            }

            int currentWeight = 0;
            for (Map.Entry<Integer, Integer> entry : timeline.entrySet()) {
                currentWeight += entry.getValue();
                if (currentWeight > SERVER_CAPACITY) {
                    System.out.println("Error: Server " + serverId + " exceeds capacity at time " + entry.getKey() +
                            " with load " + currentWeight + " (Capacity: " + SERVER_CAPACITY + ")");
                    capacityViolated = true;
                    break; // Bij eerste overtreding stoppen
                }
            }
        }

        if (!capacityViolated) {
            System.out.println("Validation completed successfully. All capacity constraints respected.");
        } else {
            System.out.println("Validation failed due to capacity violations.");
        }
    }


}

