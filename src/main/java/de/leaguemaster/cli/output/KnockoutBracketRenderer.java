package de.leaguemaster.cli.output;

import de.leaguemaster.domain.model.League;
import de.leaguemaster.domain.model.Match;
import de.leaguemaster.domain.model.Team;

import java.util.ArrayList;
import java.util.List;

public final class KnockoutBracketRenderer {
    private static final int COLUMN_GAP = 6;
    private static final int BOX_HEIGHT = 5;
    private static final int HEADER_ROW = 2;
    private static final int FIRST_MATCH_ROW = 4;

    private KnockoutBracketRenderer() {
    }

    public static String render(League league) {
        if (league.matches().isEmpty()) {
            return "Noch kein Bracket. Fuege Teams hinzu und bestaetige sie mit team done";
        }

        BracketLayout layout = BracketLayout.from(league);
        Canvas canvas = new Canvas(layout.totalHeight(), layout.totalWidth());

        canvas.write(0, 0, "Knockout Bracket: " + league.name());
        writeRounds(canvas, layout);
        canvas.write(layout.championRow(), 0, championLine(league));

        return canvas.render();
    }

    private static void writeRounds(Canvas canvas, BracketLayout layout) {
        for (int roundIndex = 0; roundIndex < layout.columns().size(); roundIndex++) {
            RoundColumn column = layout.columns().get(roundIndex);
            int startColumn = layout.columnStart(roundIndex);
            canvas.write(HEADER_ROW, startColumn, column.title());
            for (int matchIndex = 0; matchIndex < column.matches().size(); matchIndex++) {
                MatchView matchView = column.matches().get(matchIndex);
                int startRow = layout.matchStartRow(roundIndex, matchIndex);
                writeMatch(canvas, startRow, startColumn, column.width(), matchView);
            }
        }
    }

    private static void writeMatch(Canvas canvas, int startRow, int startColumn, int width, MatchView matchView) {
        String border = "+" + "-".repeat(width - 2) + "+";
        canvas.write(startRow, startColumn, border);
        canvas.write(startRow + 1, startColumn, pad("| " + matchView.matchLabel(), width - 1) + "|");
        canvas.write(startRow + 2, startColumn, pad("| " + matchView.homeLine(), width - 1) + "|");
        canvas.write(startRow + 3, startColumn, pad("| " + matchView.awayLine(), width - 1) + "|");
        canvas.write(startRow + 4, startColumn, border);
    }

    private static String championLine(League league) {
        Team champion = league.champion();
        return champion == null
                ? "Champion: offen"
                : "Champion: " + champion.name();
    }

    private static String pad(String value, int width) {
        if (value.length() >= width) {
            return value;
        }
        return value + " ".repeat(width - value.length());
    }

    private record MatchView(String matchLabel, String homeLine, String awayLine) {
        static MatchView fromMatch(League league, Match match) {
            Team home = resolveTeam(league, match.homeTeamId());
            Team away = resolveTeam(league, match.awayTeamId());

            String homePrefix = winnerMarker(match, true);
            String awayPrefix = winnerMarker(match, false);
            String homeScore = scoreValue(match, true);
            String awayScore = scoreValue(match, false);

            return new MatchView(
                    match.id(),
                    homePrefix + " " + home.name() + " (" + homeScore + ")",
                    awayPrefix + " " + away.name() + " (" + awayScore + ")"
            );
        }

        static MatchView placeholder(int roundIndex, int matchIndex) {
            return new MatchView(
                    placeholderMatchId(roundIndex, matchIndex),
                    "  TBD (-)",
                    "  TBD (-)"
            );
        }

        int width() {
            return Math.max(Math.max(matchLabel.length(), homeLine.length()), awayLine.length()) + 4;
        }

        private static String placeholderMatchId(int roundIndex, int matchIndex) {
            return "R" + (roundIndex + 1) + "M" + (matchIndex + 1);
        }

        private static String winnerMarker(Match match, boolean home) {
            if (!match.isPlayed()) {
                return " ";
            }
            boolean homeWon = match.score().home() > match.score().away();
            return home == homeWon ? ">" : "x";
        }

        private static String scoreValue(Match match, boolean home) {
            if (!match.isPlayed()) {
                return "-";
            }
            return String.valueOf(home ? match.score().home() : match.score().away());
        }

        private static Team resolveTeam(League league, String teamId) {
            if (teamId == null) {
                return new Team("TBD", "TBD");
            }
            return league.teams().get(teamId);
        }
    }

    private record RoundColumn(String title, List<MatchView> matches, int width) {
        static RoundColumn create(String title, List<MatchView> matches) {
            int width = title.length();
            for (MatchView match : matches) {
                width = Math.max(width, match.width());
            }
            return new RoundColumn(title, matches, width);
        }
    }

    private record BracketLayout(List<RoundColumn> columns, int totalHeight, int totalWidth, int championRow) {
        static BracketLayout from(League league) {
            int totalRounds = expectedRounds(league.teams().size());
            List<RoundColumn> columns = new ArrayList<>();

            for (int roundIndex = 0; roundIndex < totalRounds; roundIndex++) {
                columns.add(buildColumn(league, roundIndex, totalRounds));
            }

            int totalWidth = Math.max(
                    widthOf(columns),
                    Math.max(("Knockout Bracket: " + league.name()).length(), championLine(league).length())
            );

            int championRow = FIRST_MATCH_ROW + matchSpacing(0) * Math.max(firstRoundMatchCount(league), 1) + 1;
            int totalHeight = championRow + 1;

            return new BracketLayout(columns, totalHeight, totalWidth, championRow);
        }

        int columnStart(int roundIndex) {
            int start = 0;
            for (int index = 0; index < roundIndex; index++) {
                start += columns.get(index).width() + COLUMN_GAP;
            }
            return start;
        }

        int matchStartRow(int roundIndex, int matchIndex) {
            int roundOffset = ((1 << roundIndex) - 1) * ((BOX_HEIGHT + 1) / 2);
            return FIRST_MATCH_ROW + roundOffset + matchIndex * matchSpacing(roundIndex);
        }

        private static RoundColumn buildColumn(League league, int roundIndex, int totalRounds) {
            List<Match> actualMatches = league.round(roundIndex);
            int expectedMatches = league.teams().size() / (1 << (roundIndex + 1));
            List<MatchView> matchViews = new ArrayList<>();

            for (Match actualMatch : actualMatches) {
                matchViews.add(MatchView.fromMatch(league, actualMatch));
            }
            for (int matchIndex = actualMatches.size(); matchIndex < expectedMatches; matchIndex++) {
                matchViews.add(MatchView.placeholder(roundIndex, matchIndex));
            }

            return RoundColumn.create(roundTitle(roundIndex, totalRounds), matchViews);
        }

        private static int widthOf(List<RoundColumn> columns) {
            int width = 0;
            for (int index = 0; index < columns.size(); index++) {
                if (index > 0) {
                    width += COLUMN_GAP;
                }
                width += columns.get(index).width();
            }
            return width;
        }

        private static int expectedRounds(int teamCount) {
            int rounds = 0;
            int teams = teamCount;
            while (teams > 1) {
                teams /= 2;
                rounds++;
            }
            return Math.max(rounds, 1);
        }

        private static int firstRoundMatchCount(League league) {
            return league.teams().size() / 2;
        }
    }

    private static int matchSpacing(int roundIndex) {
        return (BOX_HEIGHT + 1) * (1 << roundIndex);
    }

    private static String roundTitle(int roundIndex, int totalRounds) {
        if (roundIndex == totalRounds - 1) {
            return "Finale";
        }
        if (roundIndex == totalRounds - 2) {
            return "Halbfinale";
        }
        if (roundIndex == totalRounds - 3) {
            return "Viertelfinale";
        }
        return "Runde " + (roundIndex + 1);
    }

    private static final class Canvas {
        private final char[][] cells;

        private Canvas(int height, int width) {
            this.cells = createCells(height, width);
        }

        void write(int row, int column, String value) {
            for (int index = 0; index < value.length(); index++) {
                if (row >= 0 && row < cells.length && column + index >= 0 && column + index < cells[row].length) {
                    cells[row][column + index] = value.charAt(index);
                }
            }
        }

        String render() {
            StringBuilder builder = new StringBuilder();
            for (char[] row : cells) {
                builder.append(trimRight(new String(row))).append("\n");
            }
            return builder.toString().trim();
        }

        private static char[][] createCells(int height, int width) {
            char[][] cells = new char[Math.max(height, 1)][Math.max(width, 1)];
            for (int row = 0; row < cells.length; row++) {
                for (int column = 0; column < cells[row].length; column++) {
                    cells[row][column] = ' ';
                }
            }
            return cells;
        }

        private static String trimRight(String value) {
            int end = value.length();
            while (end > 0 && value.charAt(end - 1) == ' ') {
                end--;
            }
            return value.substring(0, end);
        }
    }
}
