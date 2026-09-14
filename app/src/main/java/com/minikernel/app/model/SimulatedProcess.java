package com.minikernel.app.model;

public class SimulatedProcess {
    private final int pid;
    private final String name;
    private ProcessState state;

    public SimulatedProcess(int pid, String name) {
        this.pid = pid;
        this.name = name;
        this.state = ProcessState.NEW;
    }

    public int getPid() {
        return pid;
    }

    public String getName() {
        return name;
    }

    public ProcessState getState() {
        return state;
    }

    public void setState(ProcessState state) {
        this.state = state;
    }
}
