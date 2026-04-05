package de.leaguemaster.application.usecase;

import de.leaguemaster.domain.model.League;
import de.leaguemaster.domain.repository.LeagueRepository;

import java.util.Optional;

public class LeagueQueryService {
    private final LeagueRepository leagueRepository;

    public LeagueQueryService(LeagueRepository leagueRepository) {
        this.leagueRepository = leagueRepository;
    }

    public League byId(String leagueId) {
        Optional<League> leagueOpt = leagueRepository.findById(leagueId);
        if (leagueOpt.isEmpty()) {
            throw new IllegalArgumentException("Liga nicht gefunden.");
        }
        return leagueOpt.get();
    }
}
