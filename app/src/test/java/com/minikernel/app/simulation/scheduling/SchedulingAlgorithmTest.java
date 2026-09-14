package com.minikernel.app.simulation.scheduling;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class SchedulingAlgorithmTest {

    private List<SchedulingProcess> sample() {
        return Arrays.asList(
                new SchedulingProcess(1001, "P1", 0, 5),
                new SchedulingProcess(1002, "P2", 1, 3),
                new SchedulingProcess(1003, "P3", 2, 1)
        );
    }

    @Test
    public void fcfsProducesArrivalOrderAndMetrics() {
        SchedulingResult r = new FCFSScheduler().schedule(sample(), 2);
        assertEquals(3, r.getSegments().size());
        assertEquals(1001, r.getSegments().get(0).getPid());
        assertEquals(1002, r.getSegments().get(1).getPid());
        assertEquals(1003, r.getSegments().get(2).getPid());
        assertEquals(5, r.getMetrics().get(0).getCompletionTime());
    }

    @Test
    public void sjfChoosesShortestAvailableJob() {
        SchedulingResult r = new SJFScheduler().schedule(sample(), 2);
        assertEquals(1001, r.getSegments().get(0).getPid());
        assertEquals(1003, r.getSegments().get(1).getPid());
        assertEquals(1002, r.getSegments().get(2).getPid());
    }

    @Test
    public void roundRobinRotatesByQuantum() {
        List<SchedulingProcess> p = Arrays.asList(
                new SchedulingProcess(1001, "P1", 0, 5),
                new SchedulingProcess(1002, "P2", 0, 3)
        );
        SchedulingResult r = new RoundRobinScheduler().schedule(p, 2);
        assertEquals(1001, r.getSegments().get(0).getPid());
        assertEquals(1002, r.getSegments().get(1).getPid());
        assertEquals(1001, r.getSegments().get(2).getPid());
        assertEquals(1002, r.getSegments().get(3).getPid());
    }

    @Test
    public void waitingAndTurnaroundAreCalculated() {
        List<SchedulingProcess> p = Arrays.asList(
                new SchedulingProcess(1001, "P1", 0, 4),
                new SchedulingProcess(1002, "P2", 0, 2)
        );
        SchedulingResult r = new FCFSScheduler().schedule(p, 2);
        SchedulingResult.Metric p2 = r.getMetrics().get(1);
        assertEquals(6, p2.getCompletionTime());
        assertEquals(6, p2.getTurnaroundTime());
        assertEquals(4, p2.getWaitingTime());
    }
}
