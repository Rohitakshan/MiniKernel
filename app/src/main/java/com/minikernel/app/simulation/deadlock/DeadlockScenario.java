package com.minikernel.app.simulation.deadlock;

public final class DeadlockScenario {
    private DeadlockScenario() {}

    public static BankersAlgorithm safe() {
        int[][] allocation = {
                {0, 1, 0},
                {2, 0, 0},
                {3, 0, 2},
                {2, 1, 1},
                {0, 0, 2}
        };
        int[][] maximum = {
                {7, 5, 3},
                {3, 2, 2},
                {9, 0, 2},
                {2, 2, 2},
                {4, 3, 3}
        };
        int[] available = {3, 3, 2};
        return new BankersAlgorithm(allocation, maximum, available);
    }

    public static BankersAlgorithm unsafe() {
        int[][] allocation = {
                {1, 0, 1},
                {0, 1, 0},
                {1, 1, 0}
        };
        int[][] maximum = {
                {2, 1, 1},
                {1, 2, 1},
                {2, 2, 1}
        };
        int[] available = {0, 0, 1};
        return new BankersAlgorithm(allocation, maximum, available);
    }
}
