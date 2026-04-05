package de.leaguemaster.application.usecase;

import de.leaguemaster.domain.model.League;
import de.leaguemaster.domain.model.Match;
import de.leaguemaster.domain.repository.LeagueRepository;

import java.util.List;
import java.util.Optional;

public class ScheduleMatchesService {
    private final LeagueRepository leagueRepository;

    public ScheduleMatchesService(LeagueRepository leagueRepository) {
        this.leagueRepository = leagueRepository;
    }

    public List<Match> roundRobin(String leagueId) {
        Optional<League> leagueOpt = leagueRepository.findById(leagueId);
        if (leagueOpt.isEmpty()) {
            throw new IllegalArgumentException("Liga nicht gefunden.");
        }
        League league = leagueOpt.get();
        if (league.hasMatches()) {
            throw new IllegalStateException("Spielplan existiert bereits.");
        }
        if (league.teams().size() < 4) {
            throw new IllegalStateException("Mindestens 4 Teams erforderlich.");
        }
        List<Match> created = league.scheduleRoundRobin();
        leagueRepository.save(league);
        return created;
    }
}
