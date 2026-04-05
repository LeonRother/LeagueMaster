package de.leaguemaster.application.usecase;

import de.leaguemaster.domain.model.League;
import de.leaguemaster.domain.model.Team;
import de.leaguemaster.domain.repository.LeagueRepository;

import java.util.Optional;

public class AddTeamService {
    private final LeagueRepository leagueRepository;

    public AddTeamService(LeagueRepository leagueRepository) {
        this.leagueRepository = leagueRepository;
    }

    public Team execute(String leagueId, String teamName) {
        Optional<League> leagueOpt = leagueRepository.findById(leagueId);
        if (leagueOpt.isEmpty()) {
            throw new IllegalArgumentException("Liga nicht gefunden.");
        }
        League league = leagueOpt.get();
        if (league.hasMatches()) {
            throw new IllegalStateException("Spielplan existiert bereits. Teams koennen nicht mehr geaendert werden.");
        }
        Team team = league.addTeam(teamName);
        leagueRepository.save(league);
        return team;
    }
}
