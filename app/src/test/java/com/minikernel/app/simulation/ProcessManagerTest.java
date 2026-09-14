package com.minikernel.app.simulation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.minikernel.app.model.ProcessState;
import com.minikernel.app.model.SimulatedProcess;

import org.junit.Test;

public class ProcessManagerTest {

    @Test
    public void createProcessAssignsUniquePidAndStartsNew() {
        ProcessManager manager = new ProcessManager();

        SimulatedProcess first = manager.createProcess("Editor");
        SimulatedProcess second = manager.createProcess("Terminal");

        assertEquals(1001, first.getPid());
        assertEquals(1002, second.getPid());
        assertEquals(ProcessState.NEW, first.getState());
        assertEquals("Editor", first.getName());
    }

    @Test
    public void validLifecycleTransitionsAreAccepted() {
        ProcessManager manager = new ProcessManager();
        SimulatedProcess process = manager.createProcess("Compiler");

        assertTrue(manager.transitionToReady(process));
        assertEquals(ProcessState.READY, process.getState());

        assertTrue(manager.transitionToRunning(process));
        assertEquals(ProcessState.RUNNING, process.getState());

        assertTrue(manager.transitionToWaiting(process));
        assertEquals(ProcessState.WAITING, process.getState());

        assertTrue(manager.transitionToReady(process));
        assertEquals(ProcessState.READY, process.getState());

        assertTrue(manager.transitionToRunning(process));
        assertTrue(manager.terminate(process));
        assertEquals(ProcessState.TERMINATED, process.getState());
    }

    @Test
    public void invalidTransitionsAreRejected() {
        ProcessManager manager = new ProcessManager();
        SimulatedProcess process = manager.createProcess("Invalid Test");

        assertFalse(manager.transitionToRunning(process));
        assertFalse(manager.transitionToWaiting(process));
        assertFalse(manager.terminate(process));
        assertEquals(ProcessState.NEW, process.getState());
    }

    @Test
    public void terminatedProcessCanBeDeleted() {
        ProcessManager manager = new ProcessManager();
        SimulatedProcess process = manager.createProcess("Cleanup");

        manager.transitionToReady(process);
        manager.transitionToRunning(process);
        manager.terminate(process);

        assertTrue(manager.deleteTerminated(process));
        assertEquals(0, manager.getProcesses().size());
        assertFalse(manager.deleteTerminated(process));
    }
}
