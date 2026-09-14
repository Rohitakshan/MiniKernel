package com.minikernel.app.simulation.ipc;

import org.junit.Test;

import static org.junit.Assert.*;

public class SimulatedPipeTest {
    @Test public void writeAndReadAreFifo() {
        SimulatedPipe pipe = new SimulatedPipe();
        assertTrue(pipe.write(1001, 1002, "first"));
        assertTrue(pipe.write(1001, 1002, "second"));
        assertEquals("first", pipe.read().getText());
        assertEquals("second", pipe.read().getText());
        assertTrue(pipe.isEmpty());
    }

    @Test public void blankMessagesAreRejected() {
        SimulatedPipe pipe = new SimulatedPipe();
        assertFalse(pipe.write(1001, 1002, "   "));
        assertTrue(pipe.isEmpty());
    }

    @Test public void resetClearsBufferAndHistory() {
        SimulatedPipe pipe = new SimulatedPipe();
        pipe.write(1001, 1002, "hello");
        pipe.reset();
        assertTrue(pipe.isEmpty());
        assertTrue(pipe.getHistory().isEmpty());
    }
}
