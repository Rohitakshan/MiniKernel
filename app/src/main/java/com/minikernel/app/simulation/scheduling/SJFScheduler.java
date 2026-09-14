package com.minikernel.app.simulation.scheduling;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SJFScheduler implements Scheduler {
    @Override
    public SchedulingResult schedule(List<SchedulingProcess> input, int quantum) {
        List<SchedulingProcess> remaining = new ArrayList<>(input);
        List<ScheduleSegment> segments = new ArrayList<>();
        List<SchedulingResult.Metric> metrics = new ArrayList<>();
        int time = 0;

        while (!remaining.isEmpty()) {
            SchedulingProcess selected = null;
            for (SchedulingProcess p : remaining) {
                if (p.getArrivalTime() <= time &&
                        (selected == null ||
                         p.getBurstTime() < selected.getBurstTime() ||
                         (p.getBurstTime() == selected.getBurstTime() &&
                          (p.getArrivalTime() < selected.getArrivalTime() ||
                           (p.getArrivalTime() == selected.getArrivalTime() && p.getPid() < selected.getPid()))))){
                    selected = p;
                }
            }
            if (selected == null) {
                remaining.sort(Comparator.comparingInt(SchedulingProcess::getArrivalTime)
                        .thenComparingInt(SchedulingProcess::getPid));
                time = Math.max(time, remaining.get(0).getArrivalTime());
                continue;
            }
            remaining.remove(selected);
            int start = time;
            time += selected.getBurstTime();
            segments.add(new ScheduleSegment(selected.getPid(), selected.getName(), start, time));
            metrics.add(new SchedulingResult.Metric(selected, time));
        }
        return new SchedulingResult(segments, metrics);
    }
}
