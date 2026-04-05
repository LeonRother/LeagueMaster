package de.leaguemaster.cli.commands;

import de.leaguemaster.application.dto.TableRow;
import de.leaguemaster.application.usecase.ShowTableService;
import de.leaguemaster.cli.Command;
import de.leaguemaster.cli.CommandContext;
import de.leaguemaster.cli.modes.CommandResult;
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
            return CommandResult.ok(render(rows));
        }

        return CommandResult.invalid("Unbekannte Aktion. Verwendung: table show");
    }

    private String render(List<TableRow> rows) {
        if (rows.isEmpty()) {
            return "Keine Daten. Erstelle Teams, Spielplan und Ergebnisse.";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Tabelle:\n");
        for (TableRow row : rows) {
            sb.append(" - ")
              .append(row.teamName())
              .append(" | ")
              .append("Sp ").append(row.played())
              .append(" W ").append(row.wins())
              .append(" D ").append(row.draws())
              .append(" L ").append(row.losses())
              .append(" | ")
              .append(row.goalsFor()).append(":").append(row.goalsAgainst())
              .append(" | ")
              .append(row.points()).append(" P")
              .append("\n");
        }
        return sb.toString().trim();
    }
}
