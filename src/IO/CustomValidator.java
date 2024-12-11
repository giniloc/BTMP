package IO;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class CustomValidator {
    static int SERVER_CAPACITY;

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

    public static void main(String[] args) {
        String inputFilePath = "TestInstances/n50 t50 LonLr/cap100_n50_t50_LonLr_1.txt";
        String solution = "{38;40}|{35;47}|{30;23}|{36;29}|{41;28}|{26;43}|{31;24}|{19;22}|{21;15}|{9;20;46}|{18;16}|{17;34;11}|{12;14;39;48}|{10;13}|{3;45;8}|{5;7}|{33;6;32}|{1;4;49;25;27}|{2;37;50}|{42;44}";

        Map<Integer, Request> requests = parseInputFile(inputFilePath);
        List<List<Integer>> solutionAssignments = parseSolution(solution);

        validateSolution(requests, solutionAssignments);
    }

    private static Map<Integer, Request> parseInputFile(String inputFilePath) {
        Map<Integer, Request> requests = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(inputFilePath))) {
            String firstLine = br.readLine();
            if (firstLine != null) {
                String[] parts = firstLine.split("\t");
                if (parts.length >= 2) {
                    SERVER_CAPACITY = Integer.parseInt(parts[1]);
                }
            }

            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\t");
                if (parts.length == 4) {
                    int id = Integer.parseInt(parts[0]);
                    int startTime = Integer.parseInt(parts[1]);
                    int endTime = Integer.parseInt(parts[2]);
                    int busyTime = Integer.parseInt(parts[3]);
                    requests.put(id, new Request(id - 1, startTime, endTime, busyTime)); // Adjusted for 0-based index
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return requests;
    }

    private static List<List<Integer>> parseSolution(String solution) {
        List<List<Integer>> serverAssignments = new ArrayList<>();
        String[] servers = solution.split("\\|");
        for (String server : servers) {
            server = server.replaceAll("[{}]", ""); // Remove braces
            List<Integer> serverRequests = new ArrayList<>();
            for (String requestId : server.split(";")) {
                serverRequests.add(Integer.parseInt(requestId) - 1); // Adjusted for 0-based index
            }
            serverAssignments.add(serverRequests);
        }
        return serverAssignments;
    }

    private static void validateSolution(Map<Integer, Request> requests, List<List<Integer>> solutionAssignments) {
        boolean capacityViolated = false;
        int totalBusyTime = 0;

        for (int serverId = 0; serverId < solutionAssignments.size(); serverId++) {
            List<Integer> requestIds = solutionAssignments.get(serverId);
            List<Request> serverRequests = new ArrayList<>();
            for (int requestId : requestIds) {
                serverRequests.add(requests.get(requestId));
            }

            int earliestStartTime = Integer.MAX_VALUE;
            int latestEndTime = Integer.MIN_VALUE;
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
                    System.out.println("Error: Server " + (serverId + 1) + " exceeds capacity at time " + entry.getKey() +
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

        System.out.println("Total busy time: " + totalBusyTime);
    }
}
