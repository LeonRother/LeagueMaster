package de.leaguemaster.application.usecase;

import de.leaguemaster.application.dto.CreateLeagueRequest;
import de.leaguemaster.application.dto.TableRow;
import de.leaguemaster.domain.model.Match;
import de.leaguemaster.infrastructure.storage.InMemoryLeagueRepository;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ShowTableServiceTest {

    @Test
    void assignsOnePointEachForDraw() {
        // Arrange
        InMemoryLeagueRepository repo = new InMemoryLeagueRepository();
        String leagueId = new CreateLeagueService(repo).execute(new CreateLeagueRequest("Liga")).id();
        AddTeamService addTeam = new AddTeamService(repo);
        addTeam.execute(leagueId, "Alpha");
        addTeam.execute(leagueId, "Beta");
        addTeam.execute(leagueId, "Gamma");
        addTeam.execute(leagueId, "Delta");

        List<Match> matches = new ScheduleMatchesService(repo).roundRobin(leagueId);
        // First match in round-robin is Alpha vs Delta (Rotation-Algorithmus)
        new RecordMatchResultService(repo).execute(leagueId, matches.get(0).id(), 1, 1);

        // Act
        List<TableRow> table = new ShowTableService(repo).execute(leagueId);

        // Assert: both teams involved in the draw get exactly 1 point and 1 draw
        TableRow alphaRow = table.stream()
                .filter(r -> r.teamName().equals("Alpha")).findFirst().orElseThrow();
        TableRow deltaRow = table.stream()
                .filter(r -> r.teamName().equals("Delta")).findFirst().orElseThrow();

        assertEquals(1, alphaRow.points());
        assertEquals(1, alphaRow.draws());
        assertEquals(0, alphaRow.wins());
        assertEquals(1, deltaRow.points());
        assertEquals(1, deltaRow.draws());
        assertEquals(0, deltaRow.wins());
    }
}
