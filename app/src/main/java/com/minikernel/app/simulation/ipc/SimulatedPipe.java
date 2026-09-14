package com.minikernel.app.simulation.ipc;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

/** Educational FIFO pipe simulation. It does not access the real Android/Linux IPC layer. */
public class SimulatedPipe {
    private final Deque<PipeMessage> buffer = new ArrayDeque<>();
    private final List<String> history = new ArrayList<>();

    public boolean write(int writerPid, int readerPid, String text) {
        if (text == null || text.trim().isEmpty()) return false;
        PipeMessage message = new PipeMessage(writerPid, readerPid, text.trim());
        buffer.addLast(message);
        history.add("PID " + writerPid + " → PIPE: \"" + message.getText() + "\"");
        return true;
    }

    public PipeMessage read() {
        PipeMessage message = buffer.pollFirst();
        if (message != null) {
            history.add("PIPE → PID " + message.getReaderPid() + ": \"" + message.getText() + "\"");
        }
        return message;
    }

    public PipeMessage peek() { return buffer.peekFirst(); }
    public int size() { return buffer.size(); }
    public boolean isEmpty() { return buffer.isEmpty(); }
    public List<PipeMessage> getMessages() { return Collections.unmodifiableList(new ArrayList<>(buffer)); }
    public List<String> getHistory() { return Collections.unmodifiableList(history); }

    public void reset() {
        buffer.clear();
        history.clear();
    }
}
