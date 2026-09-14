package com.minikernel.app.simulation.synchronization;

/** Immutable snapshot of the simulated machine after one synchronization event. */
public class SimulationStep {
    private final String actor;
    private final String action;
    private final String detail;
    private final boolean inCriticalSection;
    private final boolean[] flags;
    private final int turn;
    private final int[] tickets;
    private final String[] states;

    public SimulationStep(String actor, String action, String detail, boolean inCriticalSection) {
        this(actor, action, detail, inCriticalSection, new boolean[5], -1, new int[5], new String[5]);
    }

    public SimulationStep(String actor, String action, String detail, boolean inCriticalSection,
                          boolean[] flags, int turn, int[] tickets, String[] states) {
        this.actor = actor;
        this.action = action;
        this.detail = detail;
        this.inCriticalSection = inCriticalSection;
        this.flags = flags.clone();
        this.turn = turn;
        this.tickets = tickets.clone();
        this.states = states.clone();
    }

    public String getActor() { return actor; }
    public String getAction() { return action; }
    public String getDetail() { return detail; }
    public boolean isInCriticalSection() { return inCriticalSection; }
    public boolean[] getFlags() { return flags.clone(); }
    public int getTurn() { return turn; }
    public int[] getTickets() { return tickets.clone(); }
    public String[] getStates() { return states.clone(); }
}
