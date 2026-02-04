package de.leaguemaster.application.usecase;

import de.leaguemaster.application.dto.CreateLeagueRequest;
import de.leaguemaster.application.dto.CreateLeagueResponse;
import de.leaguemaster.domain.model.League;
import de.leaguemaster.domain.repository.LeagueRepository;

public class CreateLeagueService {

    private final LeagueRepository leagueRepository;

    public CreateLeagueService(LeagueRepository leagueRepository) {
        this.leagueRepository = leagueRepository;
    }

    public CreateLeagueResponse execute(CreateLeagueRequest request) {
        League league = new League(request.name());
        leagueRepository.save(league);
        return new CreateLeagueResponse(league.id());
    }
}
