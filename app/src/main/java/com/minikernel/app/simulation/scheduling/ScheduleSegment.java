package com.minikernel.app.simulation.scheduling;

/** One contiguous CPU execution interval. */
public class ScheduleSegment {
    private final int pid;
    private final String name;
    private final int startTime;
    private final int endTime;

    public ScheduleSegment(int pid, String name, int startTime, int endTime) {
        this.pid = pid;
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public int getPid() { return pid; }
    public String getName() { return name; }
    public int getStartTime() { return startTime; }
    public int getEndTime() { return endTime; }
}
