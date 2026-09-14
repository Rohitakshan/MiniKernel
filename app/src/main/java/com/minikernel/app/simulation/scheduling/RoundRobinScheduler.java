package com.minikernel.app.simulation.scheduling;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class RoundRobinScheduler implements Scheduler {
    @Override
    public SchedulingResult schedule(List<SchedulingProcess> input, int quantum) {
        if (quantum <= 0) throw new IllegalArgumentException("Quantum must be positive");

        List<SchedulingProcess> list = new ArrayList<>(input);
        list.sort(Comparator.comparingInt(SchedulingProcess::getArrivalTime)
                .thenComparingInt(SchedulingProcess::getPid));

        Queue<SchedulingProcess> ready = new ArrayDeque<>();
        Map<Integer, Integer> remaining = new HashMap<>();
        Map<Integer, Integer> completion = new HashMap<>();
        List<ScheduleSegment> segments = new ArrayList<>();
        int index = 0;
        int time = 0;

        while (index < list.size() || !ready.isEmpty()) {
            if (ready.isEmpty() && index < list.size() && time < list.get(index).getArrivalTime()) {
                time = list.get(index).getArrivalTime();
            }
            while (index < list.size() && list.get(index).getArrivalTime() <= time) {
                SchedulingProcess p = list.get(index++);
                ready.add(p);
                remaining.put(p.getPid(), p.getBurstTime());
            }

            SchedulingProcess p = ready.poll();
            if (p == null) continue;

            int run = Math.min(quantum, remaining.get(p.getPid()));
            int start = time;
            time += run;
            remaining.put(p.getPid(), remaining.get(p.getPid()) - run);

            while (index < list.size() && list.get(index).getArrivalTime() <= time) {
                SchedulingProcess arriving = list.get(index++);
                ready.add(arriving);
                remaining.put(arriving.getPid(), arriving.getBurstTime());
            }

            if (remaining.get(p.getPid()) > 0) {
                ready.add(p);
            } else {
                completion.put(p.getPid(), time);
            }
            segments.add(new ScheduleSegment(p.getPid(), p.getName(), start, time));
        }

        List<SchedulingResult.Metric> metrics = new ArrayList<>();
        for (SchedulingProcess p : list) {
            metrics.add(new SchedulingResult.Metric(p, completion.get(p.getPid())));
        }
        metrics.sort(Comparator.comparingInt(m -> m.getProcess().getPid()));
        return new SchedulingResult(segments, metrics);
    }
}
