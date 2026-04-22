package de.leaguemaster.cli.commands;

import de.leaguemaster.application.usecase.LeagueQueryService;
import de.leaguemaster.application.usecase.ScheduleMatchesService;
import de.leaguemaster.cli.Command;
import de.leaguemaster.cli.CommandContext;
import de.leaguemaster.cli.modes.CommandResult;
import de.leaguemaster.cli.output.KnockoutBracketRenderer;
import de.leaguemaster.cli.parser.CommandArgs;
import de.leaguemaster.domain.model.League;

public class KnockoutScheduleCommand implements Command {
    private final ScheduleMatchesService scheduleMatchesService;
    private final LeagueQueryService leagueQueryService;

    public KnockoutScheduleCommand(ScheduleMatchesService scheduleMatchesService, LeagueQueryService leagueQueryService) {
        this.scheduleMatchesService = scheduleMatchesService;
        this.leagueQueryService = leagueQueryService;
    }

    @Override
    public CommandResult execute(CommandContext ctx, CommandArgs args) {
        String action = args.pos(0);
        if (action == null || action.equalsIgnoreCase("help")) {
            return CommandResult.ok("Verwendung: schedule knockout | schedule list");
        }

        if (ctx.getCurrentLeagueId() == null) {
            return CommandResult.invalid("Kein aktives Turnier. Nutze: league create");
        }
        if (!ctx.areTeamsConfirmed() && !action.equalsIgnoreCase("list")) {
            return CommandResult.invalid("Teams noch nicht bestaetigt. Nutze: team done");
        }

        if (action.equalsIgnoreCase("knockout")) {
            try {
                scheduleMatchesService.knockout(ctx.getCurrentLeagueId());
                League league = leagueQueryService.byId(ctx.getCurrentLeagueId());
                return CommandResult.ok("Bracket erstellt.\n" + KnockoutBracketRenderer.render(league)
                        + "\n\nNaechster Schritt: match record --id <ID> --score X:Y");
            } catch (IllegalStateException e) {
                return CommandResult.invalid(e.getMessage());
            }
        }

        if (action.equalsIgnoreCase("list")) {
            League league = leagueQueryService.byId(ctx.getCurrentLeagueId());
            return CommandResult.ok(KnockoutBracketRenderer.render(league));
        }

        return CommandResult.invalid("Unbekannte Aktion. Verwendung: schedule knockout | schedule list");
    }
}
