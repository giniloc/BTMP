package localsearch;

public class LocalSearchResult {
    private long duration;
    private int totalBusyTime;

    /**
     * constructor
     * @param duration duration of the local search algorithm [ms]
     * @param totalBusyTime total busy time [s]
     */
    public LocalSearchResult(long duration, int totalBusyTime){
        this.duration = duration;
        this.totalBusyTime = totalBusyTime;
    }

    /**
     * elapsed time (duration) of the local search algorithm [ms]
     */
    public long getDuration(){
        return this.duration;
    }

    /**
     * total busy time [s]
     */

    public int getTotalBusyTime(){
        return this.totalBusyTime;
    }
}
