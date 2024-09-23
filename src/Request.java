public class Request {

    private int vmId;
    private int startTime;
    private int endTime;
    private int weight;


    public Request(int vmId, int startTime, int endTime, int weight) {
        this.vmId = vmId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.weight = weight;
    }

    public int getVmId() {
        return vmId;
    }

    public int getStartTime() {
        return startTime;
    }

    public int getEndTime() {
        return endTime;
    }

    public int getWeight() {
        return weight;
    }

    @Override
    public String toString() {
        return "Request{" +
                "vmId=" + vmId +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", weight=" + weight +
                '}';
    }

}
