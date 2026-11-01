package com.minikernel.app.simulation.disk;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Pure Java implementations of the six standard disk scheduling algorithms. */
public final class DiskScheduler {
    public static final String FCFS = "FCFS";
    public static final String SSTF = "SSTF";
    public static final String SCAN = "SCAN";
    public static final String C_SCAN = "C-SCAN";
    public static final String LOOK = "LOOK";
    public static final String C_LOOK = "C-LOOK";

    private DiskScheduler() { }

    public static DiskSchedulingResult schedule(String algorithm, List<Integer> requests,
                                                 int head, int diskSize, boolean moveRight) {
        if (requests == null || diskSize < 1 || head < 0 || head >= diskSize)
            throw new IllegalArgumentException("Invalid disk size or initial head position.");
        for (int request : requests) {
            if (request < 0 || request >= diskSize)
                throw new IllegalArgumentException("Every request must be between 0 and " + (diskSize - 1) + ".");
        }
        List<Integer> result;
        switch (algorithm) {
            case FCFS: result = fcfs(requests); break;
            case SSTF: result = sstf(requests, head); break;
            case SCAN: result = scan(requests, head, diskSize, moveRight, false, false); break;
            case C_SCAN: result = scan(requests, head, diskSize, moveRight, true, false); break;
            case LOOK: result = scan(requests, head, diskSize, moveRight, false, true); break;
            case C_LOOK: result = scan(requests, head, diskSize, moveRight, true, true); break;
            default: throw new IllegalArgumentException("Unknown disk scheduling algorithm.");
        }
        return new DiskSchedulingResult(result, head);
    }

    private static List<Integer> fcfs(List<Integer> requests) {
        return new ArrayList<>(requests);
    }

    private static List<Integer> sstf(List<Integer> requests, int head) {
        List<Integer> pending = new ArrayList<>(requests), order = new ArrayList<>();
        int current = head;
        while (!pending.isEmpty()) {
            int bestIndex = 0;
            for (int i = 1; i < pending.size(); i++)
                if (Math.abs(pending.get(i) - current) < Math.abs(pending.get(bestIndex) - current)) bestIndex = i;
            current = pending.remove(bestIndex);
            order.add(current);
        }
        return order;
    }

    private static List<Integer> scan(List<Integer> requests, int head, int diskSize,
                                      boolean right, boolean circular, boolean look) {
        List<Integer> left = new ArrayList<>(), rightList = new ArrayList<>(), order = new ArrayList<>();
        for (int r : requests) {
            if (r < head) left.add(r);
            else if (r > head) rightList.add(r);
            else order.add(r);
        }
        Collections.sort(left);
        Collections.sort(rightList);

        if (right) {
            order.addAll(rightList);
            if (!left.isEmpty()) {
                if (!look) order.add(diskSize - 1);
                if (circular && !look) order.add(0);
                if (!(circular && look)) Collections.reverse(left);
                order.addAll(left);
            }
        } else {
            Collections.reverse(left);
            order.addAll(left);
            if (!rightList.isEmpty()) {
                if (!look) order.add(0);
                if (circular && !look) order.add(diskSize - 1);
                Collections.reverse(rightList);
                order.addAll(rightList);
            }
        }
        return order;
    }
}
