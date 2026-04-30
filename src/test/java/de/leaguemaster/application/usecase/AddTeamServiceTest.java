package de.leaguemaster.application.usecase;

import de.leaguemaster.application.dto.CreateLeagueRequest;
import de.leaguemaster.infrastructure.storage.InMemoryLeagueRepository;
import de.leaguemaster.domain.model.Match;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class AddTeamServiceTest {

    private String leagueId;
    private AddTeamService addTeamService;
    private ScheduleMatchesService scheduleMatchesService;

    @BeforeEach
    void setUp() {
        InMemoryLeagueRepository repo = new InMemoryLeagueRepository();
        leagueId = new CreateLeagueService(repo).execute(new CreateLeagueRequest("Test Liga")).id();
        addTeamService = new AddTeamService(repo);
        scheduleMatchesService = new ScheduleMatchesService(repo);
    }

    @Test
    void rejectsDuplicateTeamName() {
        // Arrange
        addTeamService.execute(leagueId, "Lions");

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> addTeamService.execute(leagueId, "Lions"));
    }

    @Test
    void rejectsAddingTeamAfterMatchesAreScheduled() {
        // Arrange
        addTeamService.execute(leagueId, "A");
        addTeamService.execute(leagueId, "B");
        addTeamService.execute(leagueId, "C");
        addTeamService.execute(leagueId, "D");
        scheduleMatchesService.roundRobin(leagueId);

        // Act & Assert
        assertThrows(IllegalStateException.class,
                () -> addTeamService.execute(leagueId, "E"));
    }
}
