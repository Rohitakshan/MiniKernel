package com.minikernel.app.simulation.syscall;

public enum SystemCall {
    READ("read()", "Read data from a file descriptor into a user buffer."),
    WRITE("write()", "Write data from a user buffer to a file descriptor."),
    OPEN("open()", "Request the kernel to open a file and return a file descriptor."),
    CLOSE("close()", "Release an open file descriptor."),
    FORK("fork()", "Create a new child process from the calling process."),
    EXEC("exec()", "Replace the current process image with a new program."),
    WAIT("wait()", "Wait for a child process to terminate and collect its status.");

    private final String displayName;
    private final String description;

    SystemCall(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() { return displayName; }
    public String getDescription() { return description; }
}
