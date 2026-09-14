package com.minikernel.app.simulation.synchronization;

import org.junit.Test;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

public class SynchronizationAlgorithmTest {
    @Test
    public void petersonShowsStateChangesAndCriticalSection() {
        List<SimulationStep> steps = PetersonAlgorithm.generateDemo();
        assertTrue(steps.size() >= 10);
        boolean sawContention = false;
        boolean sawCritical = false;
        for (SimulationStep step : steps) {
            boolean[] flags = step.getFlags();
            if (flags[0] && flags[1]) sawContention = true;
            if (step.isInCriticalSection()) sawCritical = true;
        }
        assertTrue(sawContention);
        assertTrue(sawCritical);
    }

    @Test
    public void bakerySupportsFiveProcessesAndTicketOrder() {
        List<SimulationStep> steps = BakeryAlgorithm.generateDemo(5);
        assertTrue(steps.size() > 20);
        assertEquals(1, steps.get(2).getTickets()[0]);
        assertEquals(5, steps.get(10).getTickets()[4]);
        boolean sawCritical = false;
        for (SimulationStep step : steps) if (step.isInCriticalSection()) sawCritical = true;
        assertTrue(sawCritical);
    }
}
