package de.leaguemaster.application.usecase;

import de.leaguemaster.application.dto.CreateLeagueRequest;
import de.leaguemaster.domain.model.Match;
import de.leaguemaster.infrastructure.storage.InMemoryLeagueRepository;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class RecordMatchResultServiceTest {

    @Test
    void rejectsScoringAlreadyPlayedMatch() {
        // Arrange
        InMemoryLeagueRepository repo = new InMemoryLeagueRepository();
        String leagueId = new CreateLeagueService(repo).execute(new CreateLeagueRequest("Liga")).id();
        AddTeamService addTeam = new AddTeamService(repo);
        addTeam.execute(leagueId, "A");
        addTeam.execute(leagueId, "B");
        addTeam.execute(leagueId, "C");
        addTeam.execute(leagueId, "D");

        List<Match> matches = new ScheduleMatchesService(repo).roundRobin(leagueId);
        RecordMatchResultService recordService = new RecordMatchResultService(repo);
        String matchId = matches.get(0).id();
        recordService.execute(leagueId, matchId, 2, 1);

        // Act & Assert
        assertThrows(IllegalStateException.class,
                () -> recordService.execute(leagueId, matchId, 1, 0));
    }
}
