package com.minikernel.app.simulation.disk;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DiskSchedulingResult {
    private final List<Integer> order;
    private final List<Integer> movements;
    private final int totalHeadMovement;

    public DiskSchedulingResult(List<Integer> order, int initialHead) {
        this.order = Collections.unmodifiableList(new ArrayList<>(order));
        List<Integer> distances = new ArrayList<>();
        int total = 0;
        int previous = initialHead;
        for (int track : order) {
            int distance = Math.abs(track - previous);
            distances.add(distance);
            total += distance;
            previous = track;
        }
        this.movements = Collections.unmodifiableList(distances);
        this.totalHeadMovement = total;
    }

    public List<Integer> getOrder() { return order; }
    public List<Integer> getMovements() { return movements; }
    public int getTotalHeadMovement() { return totalHeadMovement; }
}
