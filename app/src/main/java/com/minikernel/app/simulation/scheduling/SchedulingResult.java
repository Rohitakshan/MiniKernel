package com.minikernel.app.simulation.scheduling;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Complete result produced by a scheduling algorithm. */
public class SchedulingResult {
    public static class Metric {
        private final SchedulingProcess process;
        private final int completionTime;
        private final int turnaroundTime;
        private final int waitingTime;

        public Metric(SchedulingProcess process, int completionTime) {
            this.process = process;
            this.completionTime = completionTime;
            this.turnaroundTime = completionTime - process.getArrivalTime();
            this.waitingTime = turnaroundTime - process.getBurstTime();
        }

        public SchedulingProcess getProcess() { return process; }
        public int getCompletionTime() { return completionTime; }
        public int getTurnaroundTime() { return turnaroundTime; }
        public int getWaitingTime() { return waitingTime; }
    }

    private final List<ScheduleSegment> segments;
    private final List<Metric> metrics;

    public SchedulingResult(List<ScheduleSegment> segments, List<Metric> metrics) {
        this.segments = new ArrayList<>(segments);
        this.metrics = new ArrayList<>(metrics);
    }

    public List<ScheduleSegment> getSegments() { return Collections.unmodifiableList(segments); }
    public List<Metric> getMetrics() { return Collections.unmodifiableList(metrics); }

    public double getAverageWaitingTime() {
        if (metrics.isEmpty()) return 0;
        double sum = 0;
        for (Metric m : metrics) sum += m.getWaitingTime();
        return sum / metrics.size();
    }

    public double getAverageTurnaroundTime() {
        if (metrics.isEmpty()) return 0;
        double sum = 0;
        for (Metric m : metrics) sum += m.getTurnaroundTime();
        return sum / metrics.size();
    }
}
