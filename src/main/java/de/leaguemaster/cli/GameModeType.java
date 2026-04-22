package de.leaguemaster.cli;

// domain/gamemode/GameModeType.java
public enum GameModeType {
    LEAGUE("League", "Liga mit Spieltagen und Tabelle."),
    KNOCKOUT("Knockout", "KO-Turnier mit Brackets.");

    private final String displayName;
    private final String description;

    GameModeType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
    public String displayName() { return displayName; }
    public String description() { return description; }
}
