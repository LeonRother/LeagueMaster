package de.leaguemaster.cli;

import de.leaguemaster.cli.parser.PendingInteraction;

public class CommandContext {
    private String currentLeagueId;
    private boolean teamsConfirmed;
    private PendingInteraction pending;

    public String getCurrentLeagueId() {
        return currentLeagueId;
    }

    public void setCurrentLeagueId(String id) {
        this.currentLeagueId = id;
    }

    public boolean areTeamsConfirmed() {
        return teamsConfirmed;
    }

    public void confirmTeams() {
        this.teamsConfirmed = true;
    }

    public void resetTeamsConfirmation() {
        this.teamsConfirmed = false;
    }

    public boolean hasPendingInteraction() {
        return pending != null;
    }

    public void startInteraction(PendingInteraction interaction) {
        this.pending = interaction;
    }

    public void resumeInteraction(String input) {
        if (pending != null) {
            boolean done = pending.onInput(input);
            if (done) {
                pending = null;
            }
        }
    }

    public void reset() {
        currentLeagueId = null;
        teamsConfirmed = false;
        pending = null;
    }
}
