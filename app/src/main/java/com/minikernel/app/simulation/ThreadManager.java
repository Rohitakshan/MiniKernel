package com.minikernel.app.simulation;

import com.minikernel.app.model.SimulatedProcess;
import com.minikernel.app.model.SimulatedThread;
import com.minikernel.app.model.ThreadState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Owns the state of the simulated thread table and enforces valid lifecycle transitions. */
public class ThreadManager {
    private static final int FIRST_TID = 2001;
    private static ThreadManager instance;

    private final Map<Integer, List<SimulatedThread>> threadsByProcess = new LinkedHashMap<>();
    private int nextTid = FIRST_TID;

    public ThreadManager() {
    }

    public static synchronized ThreadManager getInstance() {
        if (instance == null) {
            instance = new ThreadManager();
        }
        return instance;
    }

    public SimulatedThread createThread(SimulatedProcess process, String name) {
        if (process == null) return null;

        String cleanName = name == null ? "" : name.trim();
        if (cleanName.isEmpty()) {
            cleanName = "Thread " + nextTid;
        }

        SimulatedThread thread = new SimulatedThread(nextTid++, process.getPid(), cleanName);
        threadsByProcess.computeIfAbsent(process.getPid(), key -> new ArrayList<>()).add(thread);
        return thread;
    }

    public List<SimulatedThread> getThreads(SimulatedProcess process) {
        if (process == null) return Collections.emptyList();
        return getThreadsForPid(process.getPid());
    }

    public List<SimulatedThread> getThreadsForPid(int pid) {
        List<SimulatedThread> threads = threadsByProcess.get(pid);
        if (threads == null) return Collections.emptyList();
        return Collections.unmodifiableList(threads);
    }

    public boolean transitionToReady(SimulatedThread thread) {
        if (thread == null) return false;
        ThreadState state = thread.getState();
        if (state == ThreadState.NEW || state == ThreadState.WAITING) {
            thread.setState(ThreadState.READY);
            return true;
        }
        return false;
    }

    public boolean transitionToRunning(SimulatedThread thread) {
        if (thread != null && thread.getState() == ThreadState.READY) {
            thread.setState(ThreadState.RUNNING);
            return true;
        }
        return false;
    }

    public boolean transitionToWaiting(SimulatedThread thread) {
        if (thread != null && thread.getState() == ThreadState.RUNNING) {
            thread.setState(ThreadState.WAITING);
            return true;
        }
        return false;
    }

    public boolean terminate(SimulatedThread thread) {
        if (thread != null && thread.getState() == ThreadState.RUNNING) {
            thread.setState(ThreadState.TERMINATED);
            return true;
        }
        return false;
    }

    public boolean deleteTerminated(SimulatedThread thread) {
        if (thread == null || thread.getState() != ThreadState.TERMINATED) return false;
        List<SimulatedThread> threads = threadsByProcess.get(thread.getParentPid());
        if (threads == null) return false;
        boolean removed = threads.remove(thread);
        if (threads.isEmpty()) threadsByProcess.remove(thread.getParentPid());
        return removed;
    }
}
