package de.leaguemaster.application.usecase;

import de.leaguemaster.application.dto.CreateLeagueRequest;
import de.leaguemaster.application.dto.TableRow;
import de.leaguemaster.cli.output.TableRenderer;
import de.leaguemaster.domain.model.Match;
import de.leaguemaster.domain.model.Team;
import de.leaguemaster.infrastructure.storage.InMemoryLeagueRepository;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LeagueWinnerTest {

    @Test
    void determinesWinnerAfterAllResultsAreRecorded() {
        InMemoryLeagueRepository repository = new InMemoryLeagueRepository();
        CreateLeagueService createLeagueService = new CreateLeagueService(repository);
        AddTeamService addTeamService = new AddTeamService(repository);
        ScheduleMatchesService scheduleMatchesService = new ScheduleMatchesService(repository);
        RecordMatchResultService recordMatchResultService = new RecordMatchResultService(repository);
        ShowTableService showTableService = new ShowTableService(repository);

        String leagueId = createLeagueService.execute(new CreateLeagueRequest("Test Liga")).id();
        List<Team> teams = addTeams(addTeamService, leagueId, List.of("Lions", "Bears", "Wolves", "Eagles"));

        String championId = teams.get(0).id();
        List<Match> matches = scheduleMatchesService.roundRobin(leagueId);
        recordAllResults(recordMatchResultService, leagueId, matches, championId);

        List<TableRow> table = showTableService.execute(leagueId);
        List<String> winner = winnerNames(table);

        System.out.println(TableRenderer.render(table));
        System.out.println("Sieger: " + winner);

        assertEquals(List.of("Lions"), winner);
        assertEquals("Lions", table.get(0).teamName());
        assertEquals(9, table.get(0).points());
    }

    private List<Team> addTeams(AddTeamService addTeamService, String leagueId, List<String> names) {
        List<Team> created = new ArrayList<>();
        for (String name : names) {
            created.add(addTeamService.execute(leagueId, name));
        }
        return created;
    }

    private void recordAllResults(RecordMatchResultService recordMatchResultService,
                                  String leagueId,
                                  List<Match> matches,
                                  String championId) {
        for (Match match : matches) {
            int homeGoals = 1;
            int awayGoals = 1;
            if (match.homeTeamId().equals(championId)) {
                homeGoals = 2;
                awayGoals = 0;
            } else if (match.awayTeamId().equals(championId)) {
                homeGoals = 0;
                awayGoals = 2;
            }
            recordMatchResultService.execute(leagueId, match.id(), homeGoals, awayGoals);
        }
    }

    private List<String> winnerNames(List<TableRow> rows) {
        int maxPoints = rows.stream().mapToInt(TableRow::points).max().orElse(0);
        int maxDiff = rows.stream()
                .filter(row -> row.points() == maxPoints)
                .mapToInt(row -> row.goalsFor() - row.goalsAgainst())
                .max()
                .orElse(0);
        return rows.stream()
                .filter(row -> row.points() == maxPoints)
                .filter(row -> (row.goalsFor() - row.goalsAgainst()) == maxDiff)
                .map(TableRow::teamName)
                .toList();
    }
}
