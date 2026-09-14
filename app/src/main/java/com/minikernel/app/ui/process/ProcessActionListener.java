package com.minikernel.app.ui.process;

import com.minikernel.app.model.SimulatedProcess;

public interface ProcessActionListener {
    void onReady(SimulatedProcess process);
    void onRun(SimulatedProcess process);
    void onWait(SimulatedProcess process);
    void onTerminate(SimulatedProcess process);
    void onDelete(SimulatedProcess process);
}
