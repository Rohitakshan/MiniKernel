package com.minikernel.app.simulation;

import com.minikernel.app.model.ProcessState;
import com.minikernel.app.model.SimulatedProcess;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Owns the state of the simulated process table and enforces valid lifecycle transitions.
 */
public class ProcessManager {
    private static final int FIRST_PID = 1001;
    private static ProcessManager instance;

    private final List<SimulatedProcess> processes = new ArrayList<>();
    private int nextPid = FIRST_PID;

    public ProcessManager() {
    }

    public static synchronized ProcessManager getInstance() {
        if (instance == null) {
            instance = new ProcessManager();
        }
        return instance;
    }

    public SimulatedProcess createProcess(String name) {
        String cleanName = name == null ? "" : name.trim();
        if (cleanName.isEmpty()) {
            cleanName = "Process " + nextPid;
        }

        SimulatedProcess process = new SimulatedProcess(nextPid++, cleanName);
        processes.add(process);
        return process;
    }

    public List<SimulatedProcess> getProcesses() {
        return Collections.unmodifiableList(processes);
    }

    public boolean transitionToReady(SimulatedProcess process) {
        if (process == null) {
            return false;
        }

        ProcessState state = process.getState();
        if (state == ProcessState.NEW || state == ProcessState.WAITING) {
            process.setState(ProcessState.READY);
            return true;
        }
        return false;
    }

    public boolean transitionToRunning(SimulatedProcess process) {
        if (process != null && process.getState() == ProcessState.READY) {
            process.setState(ProcessState.RUNNING);
            return true;
        }
        return false;
    }

    public boolean transitionToWaiting(SimulatedProcess process) {
        if (process != null && process.getState() == ProcessState.RUNNING) {
            process.setState(ProcessState.WAITING);
            return true;
        }
        return false;
    }

    public boolean terminate(SimulatedProcess process) {
        if (process != null && process.getState() == ProcessState.RUNNING) {
            process.setState(ProcessState.TERMINATED);
            return true;
        }
        return false;
    }

    public boolean deleteTerminated(SimulatedProcess process) {
        if (process != null && process.getState() == ProcessState.TERMINATED) {
            return processes.remove(process);
        }
        return false;
    }
}
