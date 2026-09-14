package com.minikernel.app.simulation.scheduling;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Step controller over a calculated schedule. Each Next Step reveals one CPU execution segment.
 */
public class SchedulingSimulation {
    private final SchedulingResult result;
    private int currentIndex = 0;

    public SchedulingSimulation(SchedulingResult result) {
        this.result = result;
    }

    public boolean hasNext() { return currentIndex < result.getSegments().size(); }

    public ScheduleSegment nextStep() {
        if (!hasNext()) return null;
        return result.getSegments().get(currentIndex++);
    }

    public int getCurrentIndex() { return currentIndex; }
    public int getCurrentTime() {
        if (currentIndex == 0) return 0;
        return result.getSegments().get(currentIndex - 1).getEndTime();
    }

    public List<ScheduleSegment> getVisibleSegments() {
        return Collections.unmodifiableList(new ArrayList<>(result.getSegments().subList(0, currentIndex)));
    }

    public SchedulingResult getResult() { return result; }
    public boolean isComplete() { return !hasNext(); }
}
