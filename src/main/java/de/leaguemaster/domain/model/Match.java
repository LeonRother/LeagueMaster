package de.leaguemaster.domain.model;

public class Match {
    private final String id;
    private String homeTeamId;
    private String awayTeamId;
    private Score score;

    public Match(String id, String homeTeamId, String awayTeamId) {
        this.id = id;
        this.homeTeamId = homeTeamId;
        this.awayTeamId = awayTeamId;
    }

    public String id() {
        return id;
    }

    public String homeTeamId() {
        return homeTeamId;
    }

    public String awayTeamId() {
        return awayTeamId;
    }

    public boolean hasAssignedTeams() {
        return homeTeamId != null && awayTeamId != null;
    }

    public void assignHomeTeam(String teamId) {
        assignTeam(true, teamId);
    }

    public void assignAwayTeam(String teamId) {
        assignTeam(false, teamId);
    }

    public Score score() {
        return score;
    }

    public boolean isPlayed() {
        return score != null;
    }

    public void recordScore(Score score) {
        if (!hasAssignedTeams()) {
            throw new IllegalStateException("Match ist noch nicht vollstaendig besetzt.");
        }
        if (isPlayed()) {
            throw new IllegalStateException("Ergebnis wurde bereits eingetragen.");
        }
        this.score = score;
    }

    private void assignTeam(boolean home, String teamId) {
        if (teamId == null || teamId.isBlank()) {
            throw new IllegalArgumentException("Team-ID darf nicht leer sein.");
        }

        String currentTeamId = home ? homeTeamId : awayTeamId;
        if (currentTeamId != null && !currentTeamId.equals(teamId)) {
            throw new IllegalStateException("Team-Slot ist bereits belegt.");
        }

        if (home) {
            homeTeamId = teamId;
            return;
        }
        awayTeamId = teamId;
    }
}
