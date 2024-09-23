import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InputReader {

    private int numberOfVMRequests;   // Aantal VM-aanvragen
    private int serverCapacity;       // Capaciteit van de server
    private List<Request> vmRequests; // Lijst met VM-aanvragen

    public InputReader(String testInstance) {
        vmRequests = new ArrayList<>();
        readInputFile(testInstance);
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

                int vmId = Integer.parseInt(data[0]);         // VM ID
                int startTime = Integer.parseInt(data[1]);    // Starttijd van de VM
                int endTime = Integer.parseInt(data[2]);      // Eindtijd van de VM
                int capacityRequest = Integer.parseInt(data[3]); // RequestWeight

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



}
