package de.leaguemaster.application.usecase;

import de.leaguemaster.application.dto.CreateLeagueRequest;
import de.leaguemaster.infrastructure.MockLeagueRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CreateLeagueServiceTest {

    @Test
    void persistsLeagueWithCorrectName() {
        // Arrange
        MockLeagueRepository mockRepo = new MockLeagueRepository();
        CreateLeagueService service = new CreateLeagueService(mockRepo);

        // Act
        service.execute(new CreateLeagueRequest("Champions Cup"));

        // Assert: save() wurde genau einmal aufgerufen, gespeicherte Liga hat korrekten Namen
        assertEquals(1, mockRepo.saveCallCount());
        assertEquals("Champions Cup", mockRepo.savedLeague().name());
    }
}
