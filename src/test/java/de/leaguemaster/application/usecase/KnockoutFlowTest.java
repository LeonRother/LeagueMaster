package de.leaguemaster.application.usecase;

import de.leaguemaster.application.dto.CreateLeagueRequest;
import de.leaguemaster.domain.model.CompetitionFormat;
import de.leaguemaster.domain.model.League;
import de.leaguemaster.domain.model.Match;
import de.leaguemaster.domain.model.Team;
import de.leaguemaster.infrastructure.storage.InMemoryLeagueRepository;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class KnockoutFlowTest {

    @Test
    void createsNextRoundAndChampionForKnockoutBracket() {
        InMemoryLeagueRepository repository = new InMemoryLeagueRepository();
        CreateLeagueService createLeagueService = new CreateLeagueService(repository);
        AddTeamService addTeamService = new AddTeamService(repository);
        ScheduleMatchesService scheduleMatchesService = new ScheduleMatchesService(repository);
        RecordMatchResultService recordMatchResultService = new RecordMatchResultService(repository);
        LeagueQueryService leagueQueryService = new LeagueQueryService(repository);

        String leagueId = createLeagueService.execute(new CreateLeagueRequest("KO Cup", CompetitionFormat.KNOCKOUT)).id();
        List<Team> teams = addTeams(addTeamService, leagueId, List.of("Alpha", "Beta", "Gamma", "Delta"));

        List<Match> semiFinals = scheduleMatchesService.knockout(leagueId);
        assertEquals(2, semiFinals.size());

        recordMatchResultService.execute(leagueId, semiFinals.get(0).id(), 2, 0);
        recordMatchResultService.execute(leagueId, semiFinals.get(1).id(), 0, 3);

        League leagueAfterSemiFinals = leagueQueryService.byId(leagueId);
        List<Match> finalRound = leagueAfterSemiFinals.round(1);
        assertEquals(1, finalRound.size());

        recordMatchResultService.execute(leagueId, finalRound.get(0).id(), 4, 1);

        League completedLeague = leagueQueryService.byId(leagueId);
        assertEquals(teams.get(0).name(), completedLeague.champion().name());
    }

    @Test
    void rejectsDrawInKnockoutMatch() {
        InMemoryLeagueRepository repository = new InMemoryLeagueRepository();
        CreateLeagueService createLeagueService = new CreateLeagueService(repository);
        AddTeamService addTeamService = new AddTeamService(repository);
        ScheduleMatchesService scheduleMatchesService = new ScheduleMatchesService(repository);
        RecordMatchResultService recordMatchResultService = new RecordMatchResultService(repository);

        String leagueId = createLeagueService.execute(new CreateLeagueRequest("KO Cup", CompetitionFormat.KNOCKOUT)).id();
        addTeams(addTeamService, leagueId, List.of("Alpha", "Beta"));
        Match match = scheduleMatchesService.knockout(leagueId).get(0);

        assertThrows(IllegalArgumentException.class,
                () -> recordMatchResultService.execute(leagueId, match.id(), 1, 1));
    }

    private List<Team> addTeams(AddTeamService addTeamService, String leagueId, List<String> names) {
        List<Team> created = new ArrayList<>();
        for (String name : names) {
            created.add(addTeamService.execute(leagueId, name));
        }
        return created;
    }
}
