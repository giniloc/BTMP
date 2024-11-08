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

    private static final int SERVER_CAPACITY = 100;  // Constant capacity of each server

    public static void main(String[] args) throws IOException {
        String inputFilePath = "TestInstances/n50 t50 LonLr/cap100_n50_t50_LonLr_1.txt";
        String solutionFilePath = "SolutionsBCHTRB\\n50_t50_LonLr_cap100_n50_t50_LonLr_1.txt_BCHTRBafterLS.txt";

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
        Set<Integer> assignedRequestIds = new HashSet<>();
        solutionAssignments.values().forEach(assignedRequestIds::addAll);

        for (int requestId : requests.keySet()) {
            if (!assignedRequestIds.contains(requestId)) {
                System.out.println("Error: Request ID " + requestId + " is not assigned in the solution.");
            }
        }

        boolean capacityViolated = false;
        int totalBusyTime = 0;

        for (int serverId : solutionAssignments.keySet()) {
            List<Request> serverRequests = new ArrayList<>();
            for (int requestId : solutionAssignments.get(serverId)) {
                serverRequests.add(requests.get(requestId));
            }

            int earliestStartTime = 999999; // This cant be Max value because of approximation errors
            int latestEndTime = -999999;
            TreeMap<Integer, Integer> timeline = new TreeMap<>();

            for (Request request : serverRequests) {
                earliestStartTime = Math.min(earliestStartTime, request.startTime);
                latestEndTime = Math.max(latestEndTime, request.endTime);

                timeline.put(request.startTime, timeline.getOrDefault(request.startTime, 0) + request.weight);
                timeline.put(request.endTime, timeline.getOrDefault(request.endTime, 0) - request.weight);
            }

            int serverBusyTime = latestEndTime - earliestStartTime;
            if (serverBusyTime > 0) {
                totalBusyTime += serverBusyTime;
            }

            int currentWeight = 0;
            for (Map.Entry<Integer, Integer> entry : timeline.entrySet()) {
                currentWeight += entry.getValue();
                if (currentWeight > SERVER_CAPACITY) {
                    System.out.println("Error: Server " + serverId + " exceeds capacity at time " + entry.getKey() +
                            " with load " + currentWeight + " (Capacity: " + SERVER_CAPACITY + ")");
                    capacityViolated = true;
                    break;
                }
            }
        }

        if (!capacityViolated) {
            System.out.println("Validation completed successfully. All capacity constraints respected.");
        } else {
            System.out.println("Validation failed due to capacity violations.");
        }

        // Print de totalBusyTime aan het einde van de validatie
        System.out.println("Total busy time: " + totalBusyTime);
    }
}

