package de.leaguemaster.domain.model;

public class Score {
    private final int home;
    private final int away;

    public Score(int home, int away) {
        this.home = home;
        this.away = away;
    }

    public int home() {
        return home;
    }

    public int away() {
        return away;
    }
}
