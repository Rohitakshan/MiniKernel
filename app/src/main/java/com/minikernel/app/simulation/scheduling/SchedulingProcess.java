package com.minikernel.app.simulation.scheduling;

/** Immutable input data for one simulated CPU-scheduling process. */
public class SchedulingProcess {
    private final int pid;
    private final String name;
    private final int arrivalTime;
    private final int burstTime;

    public SchedulingProcess(int pid, String name, int arrivalTime, int burstTime) {
        if (arrivalTime < 0) throw new IllegalArgumentException("Arrival time cannot be negative");
        if (burstTime <= 0) throw new IllegalArgumentException("Burst time must be positive");
        this.pid = pid;
        this.name = name == null ? "Process" : name;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
    }

    public int getPid() { return pid; }
    public String getName() { return name; }
    public int getArrivalTime() { return arrivalTime; }
    public int getBurstTime() { return burstTime; }
}
