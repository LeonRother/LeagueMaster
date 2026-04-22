package de.leaguemaster.cli.output;

import de.leaguemaster.application.dto.CreateLeagueRequest;
import de.leaguemaster.application.usecase.AddTeamService;
import de.leaguemaster.application.usecase.CreateLeagueService;
import de.leaguemaster.application.usecase.LeagueQueryService;
import de.leaguemaster.application.usecase.RecordMatchResultService;
import de.leaguemaster.application.usecase.ScheduleMatchesService;
import de.leaguemaster.domain.model.CompetitionFormat;
import de.leaguemaster.domain.model.League;
import de.leaguemaster.domain.model.Match;
import de.leaguemaster.infrastructure.storage.InMemoryLeagueRepository;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class KnockoutBracketRendererTest {

    @Test
    void rendersBracketAsRoundColumns() {
        InMemoryLeagueRepository repository = new InMemoryLeagueRepository();
        CreateLeagueService createLeagueService = new CreateLeagueService(repository);
        AddTeamService addTeamService = new AddTeamService(repository);
        ScheduleMatchesService scheduleMatchesService = new ScheduleMatchesService(repository);
        RecordMatchResultService recordMatchResultService = new RecordMatchResultService(repository);
        LeagueQueryService leagueQueryService = new LeagueQueryService(repository);

        String leagueId = createLeagueService.execute(new CreateLeagueRequest("CLI Cup", CompetitionFormat.KNOCKOUT)).id();
        for (String teamName : List.of("Lions", "Bears", "Wolves", "Eagles")) {
            addTeamService.execute(leagueId, teamName);
        }

        List<Match> semiFinals = scheduleMatchesService.knockout(leagueId);
        recordMatchResultService.execute(leagueId, semiFinals.get(0).id(), 2, 0);
        recordMatchResultService.execute(leagueId, semiFinals.get(1).id(), 1, 3);

        League league = leagueQueryService.byId(leagueId);
        String output = KnockoutBracketRenderer.render(league);

        assertTrue(output.contains("Knockout Bracket: CLI Cup"));
        assertTrue(output.contains("Halbfinale"));
        assertTrue(output.contains("Finale"));
        assertTrue(output.contains("> Lions (2)"));
        assertTrue(output.contains("> Wolves (3)"));
        assertTrue(output.contains("M3"));
        assertTrue(output.contains("  Lions (-)"));
        assertTrue(output.contains("  Wolves (-)"));
        assertTrue(output.contains("Champion: offen"));
    }
}
