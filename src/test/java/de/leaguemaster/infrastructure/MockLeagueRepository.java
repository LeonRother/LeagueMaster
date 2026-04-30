package de.leaguemaster.infrastructure;

import de.leaguemaster.domain.model.League;
import de.leaguemaster.domain.repository.LeagueRepository;

import java.util.List;
import java.util.Optional;

public class MockLeagueRepository implements LeagueRepository {

    private League savedLeague;
    private int saveCallCount = 0;
    private String lastQueriedId;

    @Override
    public void save(League league) {
        this.savedLeague = league;
        this.saveCallCount++;
    }

    @Override
    public Optional<League> findById(String leagueId) {
        this.lastQueriedId = leagueId;
        if (savedLeague != null && savedLeague.id().equals(leagueId)) {
            return Optional.of(savedLeague);
        }
        return Optional.empty();
    }

    @Override
    public List<League> findAll() {
        return savedLeague != null ? List.of(savedLeague) : List.of();
    }

    @Override
    public void deleteById(String leagueId) {}

    public League savedLeague() {
        return savedLeague;
    }

    public int saveCallCount() {
        return saveCallCount;
    }

    public String lastQueriedId() {
        return lastQueriedId;
    }
}
