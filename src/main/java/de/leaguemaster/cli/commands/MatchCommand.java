package de.leaguemaster.cli.commands;

import de.leaguemaster.application.usecase.LeagueQueryService;
import de.leaguemaster.application.usecase.RecordMatchResultService;
import de.leaguemaster.cli.Command;
import de.leaguemaster.cli.CommandContext;
import de.leaguemaster.cli.modes.CommandResult;
import de.leaguemaster.cli.parser.CommandArgs;
import de.leaguemaster.domain.model.League;
import de.leaguemaster.domain.model.Match;
import de.leaguemaster.domain.model.Team;

public class MatchCommand implements Command {
    private final RecordMatchResultService recordMatchResultService;
    private final LeagueQueryService leagueQueryService;

    public MatchCommand(RecordMatchResultService recordMatchResultService, LeagueQueryService leagueQueryService) {
        this.recordMatchResultService = recordMatchResultService;
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

        if (action.equalsIgnoreCase("list")) {
            return listMatches(ctx.getCurrentLeagueId(), args.getOption("round"));
        }

        if (action.equalsIgnoreCase("record")) {
            String id = args.getOption("id");
            String score = args.getOption("score");
            if (id == null || id.isBlank()) {
                return CommandResult.invalid("Fehlende Match-ID. Beispiel: match record --id M1 --score 2:1");
            }
            if (score == null || score.isBlank()) {
                return CommandResult.invalid("Fehlender Score. Beispiel: match record --id M1 --score 2:1");
            }
            int[] parsed = parseScore(score);
            if (parsed == null) {
                return CommandResult.invalid("Ungueltiger Score. Format: X:Y");
            }
            try {
                League before = leagueQueryService.byId(ctx.getCurrentLeagueId());
                int beforeRound = before.currentRoundIndex();
                recordMatchResultService.execute(ctx.getCurrentLeagueId(), id, parsed[0], parsed[1]);
                League after = leagueQueryService.byId(ctx.getCurrentLeagueId());
                int afterRound = after.currentRoundIndex();

                StringBuilder sb = new StringBuilder();
                sb.append("Ergebnis gespeichert.");

                if (afterRound == -1) {
                    sb.append(" Alle Ergebnisse erfasst. Naechster Schritt: table show");
                    return CommandResult.ok(sb.toString());
                }

                if (beforeRound != -1 && afterRound != beforeRound) {
                    sb.append(" Runde ").append(beforeRound + 1).append(" abgeschlossen.");
                    sb.append("\nNaechste Runde ").append(afterRound + 1).append("/").append(after.totalRounds()).append(":\n");
                    appendRoundMatches(sb, after, afterRound);
                    sb.append("\nSpieltage anzeigen: match list (aktueller) oder match list --round <N>");
                    sb.append("\nPunkteuebersicht: table show");
                    sb.append("\nNaechster Schritt: match record --id <ID> --score X:Y");
                    return CommandResult.ok(sb.toString());
                }

                sb.append(" Naechster Schritt: match record --id <ID> --score X:Y");
                sb.append("\nSpieltage anzeigen: match list (aktueller) oder match list --round <N>");
                sb.append("\nPunkteuebersicht: table show");
                return CommandResult.ok(sb.toString());
            } catch (IllegalArgumentException e) {
                return CommandResult.invalid(e.getMessage());
            }
        }

        return CommandResult.invalid("Unbekannte Aktion. " + helpText());
    }

    private CommandResult listMatches(String leagueId, String roundArg) {
        League league = leagueQueryService.byId(leagueId);
        if (league.matches().isEmpty()) {
            return CommandResult.ok("Noch kein Spielplan. Nutze: schedule round-robin");
        }
        Integer roundIndex = parseRoundIndex(roundArg, league.totalRounds());
        if (roundIndex == null) {
            int current = league.currentRoundIndex();
            if (current == -1) {
                return CommandResult.ok("Alle Ergebnisse erfasst. Nutze: table show");
            }
            roundIndex = current;
        }
        return CommandResult.ok(renderRound(league, roundIndex));
    }

    private int[] parseScore(String score) {
        String[] parts = score.split(":");
        if (parts.length != 2) return null;
        try {
            int home = Integer.parseInt(parts[0]);
            int away = Integer.parseInt(parts[1]);
            if (home < 0 || away < 0) return null;
            return new int[] { home, away };
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void appendRoundMatches(StringBuilder sb, League league, int roundIndex) {
        for (Match match : league.round(roundIndex)) {
            Team home = league.teams().get(match.homeTeamId());
            Team away = league.teams().get(match.awayTeamId());
            sb.append(" - ")
              .append(match.id())
              .append(": ")
              .append(home.name())
              .append(" vs ")
              .append(away.name());
            if (match.isPlayed()) {
                sb.append(" [")
                  .append(match.score().home())
                  .append(":")
                  .append(match.score().away())
                  .append("]");
            }
            sb.append("\n");
        }
    }

    private Integer parseRoundIndex(String roundArg, int totalRounds) {
        if (roundArg == null || roundArg.isBlank()) return null;
        try {
            int roundNumber = Integer.parseInt(roundArg.trim());
            if (roundNumber < 1 || roundNumber > totalRounds) {
                return null;
            }
            return roundNumber - 1;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String renderRound(League league, int roundIndex) {
        StringBuilder sb = new StringBuilder();
        sb.append("Spieltag ").append(roundIndex + 1).append("/").append(league.totalRounds()).append(":\n");
        appendRoundMatches(sb, league, roundIndex);
        sb.append("\nNaechster Schritt: match record --id <ID> --score X:Y");
        return sb.toString().trim();
    }

    private String helpText() {
        return "Verwendung: match list [--round <N>] | match record --id <ID> --score X:Y";
    }
}
