package com.minikernel.app.simulation.deadlock;

import org.junit.Test;
import java.util.List;
import static org.junit.Assert.*;

public class BankersAlgorithmTest {
    @Test public void safeScenarioCompletes() {
        List<BankersStep> steps = DeadlockScenario.safe().generateSteps();
        assertEquals(BankersStep.Type.COMPLETE, steps.get(steps.size() - 1).getType());
    }

    @Test public void unsafeScenarioIsDetected() {
        List<BankersStep> steps = DeadlockScenario.unsafe().generateSteps();
        assertEquals(BankersStep.Type.UNSAFE, steps.get(steps.size() - 1).getType());
    }

    @Test public void needMatrixIsMaximumMinusAllocation() {
        int[][] allocation = {{1, 0, 0}};
        int[][] maximum = {{3, 2, 1}};
        int[] available = {1, 1, 1};
        int[][] need = new BankersAlgorithm(allocation, maximum, available).getNeed();
        assertArrayEquals(new int[]{2, 2, 1}, need[0]);
    }
}
