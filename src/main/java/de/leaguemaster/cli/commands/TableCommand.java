package de.leaguemaster.cli.commands;

import de.leaguemaster.application.dto.TableRow;
import de.leaguemaster.application.usecase.ShowTableService;
import de.leaguemaster.cli.Command;
import de.leaguemaster.cli.CommandContext;
import de.leaguemaster.cli.modes.CommandResult;
import de.leaguemaster.cli.output.TableRenderer;
import de.leaguemaster.cli.parser.CommandArgs;

import java.util.List;

public class TableCommand implements Command {
    private final ShowTableService showTableService;

    public TableCommand(ShowTableService showTableService) {
        this.showTableService = showTableService;
    }

    @Override
    public CommandResult execute(CommandContext ctx, CommandArgs args) {
        String action = args.pos(0);
        if (action == null || action.equalsIgnoreCase("help") || action.equalsIgnoreCase("show")) {
            if (ctx.getCurrentLeagueId() == null) {
                return CommandResult.invalid("Keine aktive Liga. Nutze: league create");
            }
            List<TableRow> rows = showTableService.execute(ctx.getCurrentLeagueId());
            return CommandResult.ok(TableRenderer.render(rows));
        }

        return CommandResult.invalid("Unbekannte Aktion. Verwendung: table show");
    }
}
