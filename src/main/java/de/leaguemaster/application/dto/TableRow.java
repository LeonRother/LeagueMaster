package de.leaguemaster.application.dto;

public record TableRow(
        String teamName,
        int played,
        int wins,
        int draws,
        int losses,
        int goalsFor,
        int goalsAgainst,
        int points
) {
}
