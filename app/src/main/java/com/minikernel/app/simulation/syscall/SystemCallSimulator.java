package com.minikernel.app.simulation.syscall;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Builds an executable educational trace for a simulated system call. */
public class SystemCallSimulator {

    public List<SyscallStep> createTrace(SystemCall call, int pid) {
        if (call == null) throw new IllegalArgumentException("Select a system call.");
        if (pid <= 0) throw new IllegalArgumentException("PID must be positive.");

        List<SyscallStep> steps = new ArrayList<>();
        steps.add(new SyscallStep("USER MODE", "Application invokes " + call.getDisplayName(),
                "PID " + pid + " requests a service from the operating system."));
        steps.add(new SyscallStep("TRAP", "CPU switches to kernel mode",
                "The system-call instruction transfers control to the kernel entry point."));
        steps.add(new SyscallStep("KERNEL", "Kernel validates the request",
                "The kernel identifies the call and checks the simulated process context."));

        switch (call) {
            case READ:
                steps.add(new SyscallStep("KERNEL", "Validate file descriptor", "Check that the descriptor refers to an open input resource."));
                steps.add(new SyscallStep("KERNEL", "Copy data to user buffer", "Simulate moving bytes from the kernel-managed buffer to the application's buffer."));
                break;
            case WRITE:
                steps.add(new SyscallStep("KERNEL", "Validate file descriptor", "Check that the descriptor accepts output."));
                steps.add(new SyscallStep("KERNEL", "Copy data from user buffer", "Simulate transferring bytes into the kernel-managed output buffer."));
                break;
            case OPEN:
                steps.add(new SyscallStep("KERNEL", "Resolve file", "Simulate locating the requested file/resource."));
                steps.add(new SyscallStep("KERNEL", "Allocate file descriptor", "Simulate creating an entry in the process file-descriptor table."));
                break;
            case CLOSE:
                steps.add(new SyscallStep("KERNEL", "Locate descriptor", "Find the descriptor in the simulated process table."));
                steps.add(new SyscallStep("KERNEL", "Release descriptor", "Simulate closing the resource and freeing the descriptor entry."));
                break;
            case FORK:
                steps.add(new SyscallStep("KERNEL", "Duplicate process context", "Simulate creating a child process with a copied execution context."));
                steps.add(new SyscallStep("KERNEL", "Assign child PID", "Simulate assigning a new PID to the child."));
                break;
            case EXEC:
                steps.add(new SyscallStep("KERNEL", "Load new program image", "Simulate replacing the current program image."));
                steps.add(new SyscallStep("KERNEL", "Initialize execution context", "Simulate setting the new entry point and program state."));
                break;
            case WAIT:
                steps.add(new SyscallStep("KERNEL", "Check child processes", "Simulate checking whether a child has terminated."));
                steps.add(new SyscallStep("KERNEL", "Collect child status", "Simulate returning the terminated child's status to the parent."));
                break;
        }

        steps.add(new SyscallStep("RETURN", "System call completes",
                "The kernel places the simulated return value in the process context."));
        steps.add(new SyscallStep("USER MODE", "Resume application",
                "Control returns to PID " + pid + " and execution continues in user mode."));
        return Collections.unmodifiableList(steps);
    }
}
