package com.minikernel.app.simulation.scheduling;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class FCFSScheduler implements Scheduler {
    @Override
    public SchedulingResult schedule(List<SchedulingProcess> input, int quantum) {
        List<SchedulingProcess> list = new ArrayList<>(input);
        list.sort(Comparator.comparingInt(SchedulingProcess::getArrivalTime)
                .thenComparingInt(SchedulingProcess::getPid));

        List<ScheduleSegment> segments = new ArrayList<>();
        List<SchedulingResult.Metric> metrics = new ArrayList<>();
        int time = 0;
        for (SchedulingProcess p : list) {
            if (time < p.getArrivalTime()) time = p.getArrivalTime();
            int start = time;
            time += p.getBurstTime();
            segments.add(new ScheduleSegment(p.getPid(), p.getName(), start, time));
            metrics.add(new SchedulingResult.Metric(p, time));
        }
        return new SchedulingResult(segments, metrics);
    }
}
