package com.minikernel.app.simulation.disk;

import org.junit.Test;
import java.util.Arrays;
import static org.junit.Assert.assertEquals;

public class DiskSchedulerTest {
    @Test public void fcfsCalculatesTotalMovement() {
        DiskSchedulingResult r = DiskScheduler.schedule(DiskScheduler.FCFS,
                Arrays.asList(98, 183, 37, 122, 14, 124, 65, 67), 53, 200, true);
        assertEquals(640, r.getTotalHeadMovement());
    }
    @Test public void sstfCalculatesKnownExample() {
        DiskSchedulingResult r = DiskScheduler.schedule(DiskScheduler.SSTF,
                Arrays.asList(98, 183, 37, 122, 14, 124, 65, 67), 53, 200, true);
        assertEquals(236, r.getTotalHeadMovement());
    }
    @Test public void rejectsOutOfRangeRequests() {
        try {
            DiskScheduler.schedule(DiskScheduler.FCFS, Arrays.asList(205), 50, 200, true);
        } catch (IllegalArgumentException expected) { return; }
        throw new AssertionError("Expected invalid track to be rejected");
    }
}
