package com.minikernel.app.simulation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.minikernel.app.model.SimulatedProcess;
import com.minikernel.app.model.SimulatedThread;
import com.minikernel.app.model.ThreadState;

import org.junit.Test;

public class ThreadManagerTest {
    @Test
    public void threadLifecycleFollowsValidTransitions() {
        ProcessManager processManager = new ProcessManager();
        ThreadManager manager = new ThreadManager();
        SimulatedProcess process = processManager.createProcess("Test Process");
        SimulatedThread thread = manager.createThread(process, "Worker");

        assertEquals(ThreadState.NEW, thread.getState());
        assertTrue(manager.transitionToReady(thread));
        assertTrue(manager.transitionToRunning(thread));
        assertTrue(manager.transitionToWaiting(thread));
        assertTrue(manager.transitionToReady(thread));
        assertTrue(manager.transitionToRunning(thread));
        assertTrue(manager.terminate(thread));
        assertEquals(ThreadState.TERMINATED, thread.getState());
        assertTrue(manager.deleteTerminated(thread));
    }

    @Test
    public void threadBelongsToParentProcess() {
        ProcessManager processManager = new ProcessManager();
        ThreadManager manager = new ThreadManager();
        SimulatedProcess process = processManager.createProcess("Parent");
        SimulatedThread thread = manager.createThread(process, "Worker");

        assertEquals(process.getPid(), thread.getParentPid());
        assertEquals(1, manager.getThreads(process).size());
    }
}
