package de.leaguemaster.application.dto;

import de.leaguemaster.domain.model.CompetitionFormat;

public record CreateLeagueRequest(String name, CompetitionFormat format) {
    public CreateLeagueRequest(String name) {
        this(name, CompetitionFormat.LEAGUE);
    }
}
