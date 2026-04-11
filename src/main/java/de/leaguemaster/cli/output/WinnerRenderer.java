package de.leaguemaster.cli.output;

import de.leaguemaster.application.dto.TableRow;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public final class WinnerRenderer {
    private WinnerRenderer() {
    }

    public static String render(List<TableRow> rows) {
        if (rows.isEmpty()) {
            return "Siegerehrung: Keine Daten.";
        }
        TableRow leader = rows.get(0);
        int leaderPoints = leader.points();
        int leaderDiff = goalDiff(leader);

        List<TableRow> winners = new ArrayList<>();
        for (TableRow row : rows) {
            if (row.points() != leaderPoints) {
                break;
            }
            if (goalDiff(row) != leaderDiff) {
                break;
            }
            winners.add(row);
        }

        if (winners.size() == 1) {
            return "Siegerehrung: Sieger ist " + leader.teamName() + " mit " + leaderPoints + " Punkten.";
        }

        StringJoiner names = new StringJoiner(", ");
        for (TableRow row : winners) {
            names.add(row.teamName());
        }
        return "Siegerehrung: Punktgleich auf Platz 1 sind " + names + " mit "
                + leaderPoints + " Punkten (TD " + leaderDiff + ").";
    }

    private static int goalDiff(TableRow row) {
        return row.goalsFor() - row.goalsAgainst();
    }
}
