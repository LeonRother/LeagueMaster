package de.leaguemaster.cli.output;

import de.leaguemaster.application.dto.TableRow;

import java.util.List;

public final class TableRenderer {
    private TableRenderer() {
    }

    public static String render(List<TableRow> rows) {
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
