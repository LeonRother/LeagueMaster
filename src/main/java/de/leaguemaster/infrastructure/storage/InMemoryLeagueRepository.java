package de.leaguemaster.infrastructure.storage;

import de.leaguemaster.domain.model.League;
import de.leaguemaster.domain.repository.LeagueRepository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemoryLeagueRepository implements LeagueRepository {
    private final Map<String, League> store = new LinkedHashMap<>();

    @Override
    public void save(League league) {
        store.put(league.id(), league);
    }

    @Override
    public Optional<League> findById(String leagueId) {
        return Optional.ofNullable(store.get(leagueId));
    }

    @Override
    public List<League> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public void deleteById(String leagueId) {
        store.remove(leagueId);
    }
}
