package de.leaguemaster.domain.model;

public class Match {
    private final String id;
    private final String homeTeamId;
    private final String awayTeamId;
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

    public Score score() {
        return score;
    }

    public boolean isPlayed() {
        return score != null;
    }

    public void recordScore(Score score) {
        if (isPlayed()) {
            throw new IllegalStateException("Ergebnis wurde bereits eingetragen.");
        }
        this.score = score;
    }
}
