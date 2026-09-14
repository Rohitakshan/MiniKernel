package com.minikernel.app.simulation.scheduling;

import java.util.List;

public interface Scheduler {
    SchedulingResult schedule(List<SchedulingProcess> processes, int quantum);
}
