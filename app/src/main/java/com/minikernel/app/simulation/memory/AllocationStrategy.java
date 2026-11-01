package com.minikernel.app.simulation.memory;

public enum AllocationStrategy {
    FIRST_FIT("First Fit"),
    BEST_FIT("Best Fit"),
    WORST_FIT("Worst Fit");

    private final String label;
    AllocationStrategy(String label) { this.label = label; }
    public String getLabel() { return label; }
}
