package de.leaguemaster.cli.commands;

import de.leaguemaster.application.usecase.AddTeamService;
import de.leaguemaster.application.usecase.LeagueQueryService;
import de.leaguemaster.cli.Command;
import de.leaguemaster.cli.CommandContext;
import de.leaguemaster.cli.modes.CommandResult;
import de.leaguemaster.cli.parser.CommandArgs;
import de.leaguemaster.domain.model.League;
import de.leaguemaster.domain.model.Team;

public class TeamCommand implements Command {
    private final AddTeamService addTeamService;
    private final LeagueQueryService leagueQueryService;

    public TeamCommand(AddTeamService addTeamService, LeagueQueryService leagueQueryService) {
        this.addTeamService = addTeamService;
        this.leagueQueryService = leagueQueryService;
    }

    @Override
    public CommandResult execute(CommandContext ctx, CommandArgs args) {
        String action = args.pos(0);
        if (action == null || action.equalsIgnoreCase("help")) {
            return CommandResult.ok(helpText());
        }

        if (ctx.getCurrentLeagueId() == null) {
            return CommandResult.invalid("Keine aktive Liga. Nutze: league create");
        }

        if (action.equalsIgnoreCase("add")) {
            String name = args.getOption("name");
            if (name == null || name.isBlank()) {
                return CommandResult.invalid("Fehlender Team-Name. Beispiel: team add --name \"FC Example\"");
            }
            try {
                Team team = addTeamService.execute(ctx.getCurrentLeagueId(), name);
                return CommandResult.ok("Team hinzugefuegt: " + team.name() + "\nNaechster Schritt: weitere Teams hinzufuegen (mindestens 4), dann team done");
            } catch (IllegalStateException | IllegalArgumentException e) {
                return CommandResult.invalid(e.getMessage());
            }
        }

        if (action.equalsIgnoreCase("done")) {
            League league = leagueQueryService.byId(ctx.getCurrentLeagueId());
            if (league.teams().size() < 4) {
                return CommandResult.invalid("Mindestens 4 Teams erforderlich. Aktuell: " + league.teams().size());
            }
            ctx.confirmTeams();
            return CommandResult.ok("Teams bestaetigt. Naechster Schritt: schedule round-robin");
        }

        if (action.equalsIgnoreCase("list")) {
            League league = leagueQueryService.byId(ctx.getCurrentLeagueId());
            if (league.teams().isEmpty()) {
                return CommandResult.ok("Noch keine Teams. Nutze: team add --name <TEAM>");
            }
            StringBuilder sb = new StringBuilder("Teams:\n");
            for (Team team : league.teams().values()) {
                sb.append(" - ").append(team.name()).append("\n");
            }
            return CommandResult.ok(sb.toString().trim());
        }

        return CommandResult.invalid("Unbekannte Aktion. " + helpText());
    }

    private String helpText() {
        return "Verwendung: team add --name <TEAM> | team list | team done";
    }
}
