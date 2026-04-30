package de.leaguemaster.application.usecase;

import de.leaguemaster.application.dto.CreateLeagueRequest;
import de.leaguemaster.domain.model.Match;
import de.leaguemaster.infrastructure.storage.InMemoryLeagueRepository;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ScheduleMatchesServiceTest {

    @Test
    void createsCorrectMatchCountForFourTeamRoundRobin() {
        // Arrange
        InMemoryLeagueRepository repo = new InMemoryLeagueRepository();
        String leagueId = new CreateLeagueService(repo).execute(new CreateLeagueRequest("Liga")).id();
        AddTeamService addTeam = new AddTeamService(repo);
        addTeam.execute(leagueId, "A");
        addTeam.execute(leagueId, "B");
        addTeam.execute(leagueId, "C");
        addTeam.execute(leagueId, "D");

        // Act
        List<Match> matches = new ScheduleMatchesService(repo).roundRobin(leagueId);

        // Assert: 4 Teams -> 3 Runden x 2 Spiele = 6 Matches gesamt
        assertEquals(6, matches.size());
    }
}
