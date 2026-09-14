package com.minikernel.app.model;

public class SimulatedThread {
    private final int tid;
    private final int parentPid;
    private final String name;
    private ThreadState state;

    public SimulatedThread(int tid, int parentPid, String name) {
        this.tid = tid;
        this.parentPid = parentPid;
        this.name = name;
        this.state = ThreadState.NEW;
    }

    public int getTid() { return tid; }
    public int getParentPid() { return parentPid; }
    public String getName() { return name; }
    public ThreadState getState() { return state; }
    public void setState(ThreadState state) { this.state = state; }
}
