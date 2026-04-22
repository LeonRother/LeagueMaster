package de.leaguemaster.cli.output;

import de.leaguemaster.domain.model.League;
import de.leaguemaster.domain.model.Match;
import de.leaguemaster.domain.model.Team;

public final class KnockoutBracketRenderer {
    private KnockoutBracketRenderer() {
    }

    public static String render(League league) {
        if (league.matches().isEmpty()) {
            return "Noch kein Bracket. Nutze: schedule knockout";
        }

        StringBuilder builder = new StringBuilder("Bracket:\n");
        for (int roundIndex = 0; roundIndex < league.totalRounds(); roundIndex++) {
            builder.append("Runde ").append(roundIndex + 1).append(":\n");
            for (Match match : league.round(roundIndex)) {
                Team home = league.teams().get(match.homeTeamId());
                Team away = league.teams().get(match.awayTeamId());
                builder.append(" - ")
                        .append(match.id())
                        .append(": ")
                        .append(home.name())
                        .append(" vs ")
                        .append(away.name());
                if (match.isPlayed()) {
                    builder.append(" [")
                            .append(match.score().home())
                            .append(":")
                            .append(match.score().away())
                            .append("]");
                }
                builder.append("\n");
            }
        }
        return builder.toString().trim();
    }
}
