package de.leaguemaster.cli;


import de.leaguemaster.cli.parser.PendingInteraction;

public class CommandContext {
    private String currentLeagueId;

    // Multi-step interaction
    private PendingInteraction pending;

    public String getCurrentLeagueId() { return currentLeagueId; }
    public void setCurrentLeagueId(String id) { this.currentLeagueId = id; }

    public boolean hasPendingInteraction() { return pending != null; }
    public void startInteraction(PendingInteraction i) { this.pending = i; }
    public void resumeInteraction(String input) {
        if (pending != null) {
            boolean done = pending.onInput(input);
            if (done) pending = null;
        }
    }
}