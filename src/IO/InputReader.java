package IO;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import Utils.Request;

import java.util.List;
public class InputReader {

    private static int numberOfVMRequests;
    private static int serverCapacity;
    private List<Request> vmRequests;
    private String testInstance;

    public InputReader(String testInstance) {
        this.vmRequests = new ArrayList<>();
        this.testInstance = testInstance;
        String relativePath = "TestInstances/" + testInstance;
        readInputFile(relativePath);
    }

    private void readInputFile(String fileName) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String firstLine = br.readLine();
            if (firstLine != null) {
                String[] firstLineData = firstLine.split("\\s+");
                numberOfVMRequests = Integer.parseInt(firstLineData[0]);
                serverCapacity = Integer.parseInt(firstLineData[1]);
            }

            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split("\\s+");

                int vmId = Integer.parseInt(data[0]);
                int startTime = Integer.parseInt(data[1]);
                int endTime = Integer.parseInt(data[2]);
                int capacityRequest = Integer.parseInt(data[3]);

                Request request = new Request(vmId, startTime, endTime, capacityRequest);
                vmRequests.add(request);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getNumberOfVMRequests() {
        return numberOfVMRequests;
    }

    public int getServerCapacity() {
        return serverCapacity;
    }
    public List<Request> getRequests() {
        return this.vmRequests;
    }


    public void PrintVMRequests() {
        for (Request request : vmRequests) {
            System.out.println(request);
        }
    }
    public String getTestInstance() {
        return this.testInstance;
    }

}

