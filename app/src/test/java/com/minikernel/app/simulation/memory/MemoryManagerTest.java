package com.minikernel.app.simulation.memory;

import org.junit.Test;
import static org.junit.Assert.*;

public class MemoryManagerTest {
    @Test public void firstFitAllocatesFromFirstSuitableBlock() {
        MemoryManager m = new MemoryManager(100);
        assertTrue(m.allocate("P1", 1, 30));
        assertTrue(m.allocate("P2", 2, 20));
        assertEquals(50, m.getFreeMemory());
        assertEquals(0, m.getBlocks().get(0).getStart());
        assertEquals(30, m.getBlocks().get(0).getSize());
    }

    @Test public void bestFitChoosesSmallestSuitableHole() {
        MemoryManager m = new MemoryManager(100);
        m.allocate("P1", 1, 20); m.allocate("P2", 2, 30); m.allocate("P3", 3, 20);
        m.freeByPid(1); m.freeByPid(3);
        m.setStrategy(AllocationStrategy.BEST_FIT);
        assertTrue(m.allocate("P4", 4, 15));
        assertEquals(20, m.getBlocks().get(0).getSize());
        assertEquals(15, m.getBlocks().get(1).getSize());
    }

    @Test public void compactionRemovesExternalFragmentation() {
        MemoryManager m = new MemoryManager(100);
        m.allocate("P1", 1, 20); m.allocate("P2", 2, 20); m.allocate("P3", 3, 20);
        m.freeByPid(2);
        assertFalse(m.allocate("P4", 4, 45));
        m.compact();
        assertTrue(m.allocate("P4", 4, 45));
    }

    @Test public void resetRestoresSingleFreeBlock() {
        MemoryManager m = new MemoryManager(100);
        m.allocate("P1", 1, 50);
        m.reset();
        assertEquals(1, m.getBlocks().size());
        assertEquals(100, m.getFreeMemory());
    }
}
