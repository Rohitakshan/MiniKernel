package com.minikernel.app.simulation.deadlock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Educational, deterministic simulation of the Banker's safety algorithm. */
public class BankersAlgorithm {
    private final int[][] allocation;
    private final int[][] maximum;
    private final int[] available;
    private final int[][] need;

    public BankersAlgorithm(int[][] allocation, int[][] maximum, int[] available) {
        if (allocation == null || maximum == null || available == null || allocation.length == 0
                || allocation.length != maximum.length) {
            throw new IllegalArgumentException("Invalid Banker's algorithm input");
        }
        int resources = available.length;
        for (int i = 0; i < allocation.length; i++) {
            if (allocation[i].length != resources || maximum[i].length != resources) {
                throw new IllegalArgumentException("Matrix dimensions do not match");
            }
        }
        this.allocation = copy(allocation);
        this.maximum = copy(maximum);
        this.available = available.clone();
        this.need = new int[allocation.length][resources];
        for (int i = 0; i < allocation.length; i++) {
            for (int j = 0; j < resources; j++) {
                this.need[i][j] = maximum[i][j] - allocation[i][j];
                if (this.need[i][j] < 0) throw new IllegalArgumentException("Maximum < allocation");
            }
        }
        validateNonNegative(this.available, this.need);
    }

    /** Creates a safety simulation directly from Allocation, Need, and Available. */
    public BankersAlgorithm(int[][] allocation, int[][] need, int[] available, boolean directNeed) {
        if (allocation == null || need == null || available == null || allocation.length == 0
                || allocation.length != need.length || available.length == 0) {
            throw new IllegalArgumentException("Invalid Banker's algorithm input");
        }
        int resources = available.length;
        for (int i = 0; i < allocation.length; i++) {
            if (allocation[i].length != resources || need[i].length != resources) {
                throw new IllegalArgumentException("Matrix dimensions do not match");
            }
        }
        this.allocation = copy(allocation);
        this.need = copy(need);
        this.available = available.clone();
        this.maximum = new int[allocation.length][resources];
        for (int i = 0; i < allocation.length; i++) {
            for (int j = 0; j < resources; j++) {
                this.maximum[i][j] = this.allocation[i][j] + this.need[i][j];
            }
        }
        validateNonNegative(this.available, this.allocation, this.need);
    }

    public int[][] getNeed() { return copy(need); }

    public List<BankersStep> generateSteps() {
        List<BankersStep> steps = new ArrayList<>();
        int[] work = available.clone();
        boolean[] finish = new boolean[allocation.length];
        int completed = 0;

        while (completed < allocation.length) {
            boolean found = false;
            for (int i = 0; i < allocation.length; i++) {
                if (finish[i]) continue;
                steps.add(new BankersStep(BankersStep.Type.CHECK, i,
                        "Checking whether P" + (i + 1) + " can finish: Need ≤ Available.", work, finish));
                if (lessOrEqual(need[i], work)) {
                    found = true;
                    finish[i] = true;
                    completed++;
                    steps.add(new BankersStep(BankersStep.Type.SAFE, i,
                            "P" + (i + 1) + " can finish because its remaining Need fits in Available.", work, finish));
                    for (int r = 0; r < work.length; r++) work[r] += allocation[i][r];
                    steps.add(new BankersStep(BankersStep.Type.RELEASE, i,
                            "P" + (i + 1) + " finishes and releases its allocated resources.", work, finish));
                }
            }
            if (!found) {
                steps.add(new BankersStep(BankersStep.Type.UNSAFE, -1,
                        "No unfinished process can satisfy Need ≤ Available. The state is UNSAFE.", work, finish));
                return steps;
            }
        }
        steps.add(new BankersStep(BankersStep.Type.COMPLETE, -1,
                "All processes can finish. The system is in a SAFE state.", work, finish));
        return steps;
    }

    private static boolean lessOrEqual(int[] a, int[] b) {
        for (int i = 0; i < a.length; i++) if (a[i] > b[i]) return false;
        return true;
    }

    private static void validateNonNegative(int[] available, int[][]... matrices) {
        for (int value : available) if (value < 0) throw new IllegalArgumentException("Values cannot be negative");
        for (int[][] matrix : matrices) {
            for (int[] row : matrix) for (int value : row)
                if (value < 0) throw new IllegalArgumentException("Values cannot be negative");
        }
    }

    private static int[][] copy(int[][] source) {
        int[][] out = new int[source.length][];
        for (int i = 0; i < source.length; i++) out[i] = Arrays.copyOf(source[i], source[i].length);
        return out;
    }
}
