package com.minikernel.app.simulation.memory;

public class MemoryBlock {
    private final int start;
    private int size;
    private boolean allocated;
    private String processName;
    private int pid;

    public MemoryBlock(int start, int size) {
        this.start = start;
        this.size = size;
        this.allocated = false;
        this.processName = "Free";
        this.pid = -1;
    }

    public int getStart() { return start; }
    public int getSize() { return size; }
    public boolean isAllocated() { return allocated; }
    public String getProcessName() { return processName; }
    public int getPid() { return pid; }

    public void allocate(String processName, int pid) {
        this.allocated = true;
        this.processName = processName;
        this.pid = pid;
    }

    public void free() {
        this.allocated = false;
        this.processName = "Free";
        this.pid = -1;
    }
}
