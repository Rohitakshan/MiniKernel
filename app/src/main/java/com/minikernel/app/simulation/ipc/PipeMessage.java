package com.minikernel.app.simulation.ipc;

/** A message travelling through the simulated pipe. */
public class PipeMessage {
    private final int writerPid;
    private final int readerPid;
    private final String text;

    public PipeMessage(int writerPid, int readerPid, String text) {
        this.writerPid = writerPid;
        this.readerPid = readerPid;
        this.text = text;
    }

    public int getWriterPid() { return writerPid; }
    public int getReaderPid() { return readerPid; }
    public String getText() { return text; }
}
