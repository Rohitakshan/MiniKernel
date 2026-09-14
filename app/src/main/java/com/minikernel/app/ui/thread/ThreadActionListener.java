package com.minikernel.app.ui.thread;

import com.minikernel.app.model.SimulatedThread;

public interface ThreadActionListener {
    void onReady(SimulatedThread thread);
    void onRun(SimulatedThread thread);
    void onWait(SimulatedThread thread);
    void onTerminate(SimulatedThread thread);
    void onDelete(SimulatedThread thread);
}
