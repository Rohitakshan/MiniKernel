package com.minikernel.app.simulation.syscall;

public class SyscallStep {
    private final String stage;
    private final String action;
    private final String detail;

    public SyscallStep(String stage, String action, String detail) {
        this.stage = stage;
        this.action = action;
        this.detail = detail;
    }

    public String getStage() { return stage; }
    public String getAction() { return action; }
    public String getDetail() { return detail; }
}
