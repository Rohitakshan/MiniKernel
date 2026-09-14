package com.minikernel.app.simulation.syscall;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SystemCallSimulatorTest {
    @Test
    public void readTraceStartsInUserModeAndReturnsToUserMode() {
        List<SyscallStep> steps = new SystemCallSimulator().createTrace(SystemCall.READ, 1001);
        assertEquals("USER MODE", steps.get(0).getStage());
        assertEquals("USER MODE", steps.get(steps.size() - 1).getStage());
        assertTrue(steps.size() >= 6);
    }

    @Test
    public void allCallsProduceNonEmptyTraces() {
        SystemCallSimulator simulator = new SystemCallSimulator();
        for (SystemCall call : SystemCall.values()) {
            assertTrue(call.getDisplayName(), !simulator.createTrace(call, 1001).isEmpty());
        }
    }
}
