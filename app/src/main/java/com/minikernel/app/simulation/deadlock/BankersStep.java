package com.minikernel.app.simulation.deadlock;

public class BankersStep {
    public enum Type { CHECK, SAFE, RELEASE, UNSAFE, COMPLETE }

    private final Type type;
    private final int processIndex;
    private final String message;
    private final int[] work;
    private final boolean[] finish;

    public BankersStep(Type type, int processIndex, String message, int[] work, boolean[] finish) {
        this.type = type;
        this.processIndex = processIndex;
        this.message = message;
        this.work = work.clone();
        this.finish = finish.clone();
    }

    public Type getType() { return type; }
    public int getProcessIndex() { return processIndex; }
    public String getMessage() { return message; }
    public int[] getWork() { return work.clone(); }
    public boolean[] getFinish() { return finish.clone(); }
}
