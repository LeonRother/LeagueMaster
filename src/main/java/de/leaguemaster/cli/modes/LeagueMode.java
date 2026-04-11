package de.leaguemaster.cli.modes;

// domain/gamemode/LeagueMode.java

import de.leaguemaster.application.usecase.AddTeamService;
import de.leaguemaster.application.usecase.CreateLeagueService;
import de.leaguemaster.application.usecase.LeagueQueryService;
import de.leaguemaster.application.usecase.RecordMatchResultService;
import de.leaguemaster.application.usecase.ScheduleMatchesService;
import de.leaguemaster.application.usecase.ShowTableService;
import de.leaguemaster.cli.CommandContext;
import de.leaguemaster.cli.commands.LeagueCommand;
import de.leaguemaster.cli.commands.MatchCommand;
import de.leaguemaster.cli.commands.ScheduleCommand;
import de.leaguemaster.cli.commands.TableCommand;
import de.leaguemaster.cli.commands.TeamCommand;
import de.leaguemaster.cli.parser.CommandSpec;

import java.util.List;

public class LeagueMode implements GameMode {
    private final CreateLeagueService createLeagueService;
    private final AddTeamService addTeamService;
    private final ScheduleMatchesService scheduleMatchesService;
    private final RecordMatchResultService recordMatchResultService;
    private final ShowTableService showTableService;
    private final LeagueQueryService leagueQueryService;

    public LeagueMode(CreateLeagueService createLeagueService,
                      AddTeamService addTeamService,
                      ScheduleMatchesService scheduleMatchesService,
                      RecordMatchResultService recordMatchResultService,
                      ShowTableService showTableService,
                      LeagueQueryService leagueQueryService) {
        this.createLeagueService = createLeagueService;
        this.addTeamService = addTeamService;
        this.scheduleMatchesService = scheduleMatchesService;
        this.recordMatchResultService = recordMatchResultService;
        this.showTableService = showTableService;
        this.leagueQueryService = leagueQueryService;
    }

    @Override
    public String name() {
        return "League";
    }

    @Override
    public String description() {
        return "Klassische Liga mit Spieltagen und Tabelle.";
    }

    @Override
    public void start() {
        System.out.println("[LeagueMode] Modus bereit. Tippe 'help' für Befehle.");
    }

    @Override
    public void onEnter(CommandContext context) {
        System.out.println("League-Modus aktiv.");
        System.out.println("Start: league create --name \"Mein Liga\" -> team add --name \"Team A\" (mind. 4 Teams) -> team done -> schedule round-robin");
    }

    @Override
    public List<CommandSpec> commands(CommandContext context) {
        return List.of(
                new CommandSpec(
                        "league",
                        new LeagueCommand(createLeagueService),
                        "Liga-Befehle (z. B. create).",
                        "l"
                ),
                new CommandSpec(
                        "team",
                        new TeamCommand(addTeamService, leagueQueryService),
                        "Team-Befehle (add, list).",
                        "t"
                ),
                new CommandSpec(
                        "schedule",
                        new ScheduleCommand(scheduleMatchesService, leagueQueryService),
                        "Spielplan-Befehle (round-robin, list).",
                        "s"
                ),
                new CommandSpec(
                        "match",
                        new MatchCommand(recordMatchResultService, leagueQueryService, showTableService),
                        "Match-Befehle (list, record).",
                        "m"
                ),
                new CommandSpec(
                        "table",
                        new TableCommand(showTableService),
                        "Tabelle anzeigen.",
                        "tab"
                )
        );
    }
}
