package de.leaguemaster.cli.modes;

import de.leaguemaster.application.usecase.AddTeamService;
import de.leaguemaster.application.usecase.CreateLeagueService;
import de.leaguemaster.application.usecase.LeagueQueryService;
import de.leaguemaster.application.usecase.RecordMatchResultService;
import de.leaguemaster.application.usecase.ScheduleMatchesService;
import de.leaguemaster.cli.CommandContext;
import de.leaguemaster.cli.commands.KnockoutMatchCommand;
import de.leaguemaster.cli.commands.KnockoutLeagueCommand;
import de.leaguemaster.cli.commands.KnockoutTeamCommand;
import de.leaguemaster.cli.parser.CommandSpec;

import java.util.List;

public class KnockoutMode implements GameMode {
    private final CreateLeagueService createLeagueService;
    private final AddTeamService addTeamService;
    private final ScheduleMatchesService scheduleMatchesService;
    private final RecordMatchResultService recordMatchResultService;
    private final LeagueQueryService leagueQueryService;

    public KnockoutMode(CreateLeagueService createLeagueService,
                        AddTeamService addTeamService,
                        ScheduleMatchesService scheduleMatchesService,
                        RecordMatchResultService recordMatchResultService,
                        LeagueQueryService leagueQueryService) {
        this.createLeagueService = createLeagueService;
        this.addTeamService = addTeamService;
        this.scheduleMatchesService = scheduleMatchesService;
        this.recordMatchResultService = recordMatchResultService;
        this.leagueQueryService = leagueQueryService;
    }

    @Override
    public String name() {
        return "Knockout";
    }

    @Override
    public String description() {
        return "KO-Turnier mit Bracket bis zum Finale.";
    }

    @Override
    public void start() {
        System.out.println("[KnockoutMode] Modus bereit. Tippe 'help' fuer Befehle.");
    }

    @Override
    public void onEnter(CommandContext context) {
        System.out.println("Knockout-Modus aktiv.");
        System.out.println("Start: create --name \"Mein Cup\" -> team add --name \"Team A\" -> team done");
    }

    @Override
    public List<CommandSpec> commands(CommandContext context) {
        return List.of(
                new CommandSpec(
                        "create",
                        new KnockoutLeagueCommand(createLeagueService),
                        "Knockout-Turnier erstellen."
                ),
                new CommandSpec(
                        "team",
                        new KnockoutTeamCommand(addTeamService, leagueQueryService, scheduleMatchesService),
                        "Team-Befehle (add, list).",
                        "t"
                ),
                new CommandSpec(
                        "match",
                        new KnockoutMatchCommand(recordMatchResultService, leagueQueryService),
                        "Match-Befehle (list, record).",
                        "m"
                )
        );
    }
}
