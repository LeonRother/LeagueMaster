package de.leaguemaster.application.usecase;

import de.leaguemaster.domain.model.League;
import de.leaguemaster.domain.model.Match;
import de.leaguemaster.domain.model.Score;
import de.leaguemaster.domain.repository.LeagueRepository;

import java.util.Optional;

public class RecordMatchResultService {
    private final LeagueRepository leagueRepository;

    public RecordMatchResultService(LeagueRepository leagueRepository) {
        this.leagueRepository = leagueRepository;
    }

    public Match execute(String leagueId, String matchId, int home, int away) {
        Optional<League> leagueOpt = leagueRepository.findById(leagueId);
        if (leagueOpt.isEmpty()) {
            throw new IllegalArgumentException("Liga nicht gefunden.");
        }
        League league = leagueOpt.get();
        Match match = league.recordMatchResult(matchId, new Score(home, away));
        leagueRepository.save(league);
        return match;
    }
}
