package de.leaguemaster.cli.commands;

import de.leaguemaster.application.usecase.AddTeamService;
import de.leaguemaster.application.usecase.LeagueQueryService;
import de.leaguemaster.application.usecase.ScheduleMatchesService;
import de.leaguemaster.cli.Command;
import de.leaguemaster.cli.CommandContext;
import de.leaguemaster.cli.modes.CommandResult;
import de.leaguemaster.cli.output.KnockoutBracketRenderer;
import de.leaguemaster.cli.parser.CommandArgs;
import de.leaguemaster.domain.model.League;
import de.leaguemaster.domain.model.Team;

public class KnockoutTeamCommand implements Command {
    private final AddTeamService addTeamService;
    private final LeagueQueryService leagueQueryService;
    private final ScheduleMatchesService scheduleMatchesService;

    public KnockoutTeamCommand(AddTeamService addTeamService,
                               LeagueQueryService leagueQueryService,
                               ScheduleMatchesService scheduleMatchesService) {
        this.addTeamService = addTeamService;
        this.leagueQueryService = leagueQueryService;
        this.scheduleMatchesService = scheduleMatchesService;
    }

    @Override
    public CommandResult execute(CommandContext ctx, CommandArgs args) {
        String action = args.pos(0);
        if (action == null || action.equalsIgnoreCase("help")) {
            return CommandResult.ok("Verwendung: team add --name <TEAM> | team list | team done");
        }

        if (ctx.getCurrentLeagueId() == null) {
            return CommandResult.invalid("Kein aktives Turnier. Nutze: create --name <NAME>");
        }

        if (action.equalsIgnoreCase("add")) {
            return addTeam(ctx, args);
        }
        if (action.equalsIgnoreCase("list")) {
            return listTeams(ctx);
        }
        if (action.equalsIgnoreCase("done")) {
            return confirmTeamsAndCreateBracket(ctx);
        }

        return CommandResult.invalid("Unbekannte Aktion. Verwendung: team add --name <TEAM> | team list | team done");
    }

    private CommandResult addTeam(CommandContext ctx, CommandArgs args) {
        String name = args.getOption("name");
        if (name == null || name.isBlank()) {
            return CommandResult.invalid("Fehlender Team-Name. Beispiel: team add --name \"FC Example\"");
        }
        try {
            Team team = addTeamService.execute(ctx.getCurrentLeagueId(), name);
            return CommandResult.ok("Team hinzugefuegt: " + team.name()
                    + "\nNaechster Schritt: weitere Teams hinzufuegen, dann team done");
        } catch (IllegalStateException | IllegalArgumentException exception) {
            return CommandResult.invalid(exception.getMessage());
        }
    }

    private CommandResult listTeams(CommandContext ctx) {
        League league = leagueQueryService.byId(ctx.getCurrentLeagueId());
        if (league.teams().isEmpty()) {
            return CommandResult.ok("Noch keine Teams. Nutze: team add --name <TEAM>");
        }
        StringBuilder builder = new StringBuilder("Teams:\n");
        for (Team team : league.teams().values()) {
            builder.append(" - ").append(team.name()).append("\n");
        }
        return CommandResult.ok(builder.toString().trim());
    }

    private CommandResult confirmTeamsAndCreateBracket(CommandContext ctx) {
        League league = leagueQueryService.byId(ctx.getCurrentLeagueId());
        if (league.teams().size() < 2) {
            return CommandResult.invalid("Mindestens 2 Teams erforderlich. Aktuell: " + league.teams().size());
        }

        try {
            scheduleMatchesService.knockout(ctx.getCurrentLeagueId());
            ctx.confirmTeams();
            League scheduledLeague = leagueQueryService.byId(ctx.getCurrentLeagueId());
            return CommandResult.ok("Teams bestaetigt. Bracket wurde erstellt.\n"
                    + KnockoutBracketRenderer.render(scheduledLeague)
                    + "\n\nNaechster Schritt: match record --id <ID> --score X:Y");
        } catch (IllegalStateException | IllegalArgumentException exception) {
            return CommandResult.invalid(exception.getMessage());
        }
    }
}
