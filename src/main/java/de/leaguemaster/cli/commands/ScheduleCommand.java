package de.leaguemaster.cli.commands;

import de.leaguemaster.application.usecase.LeagueQueryService;
import de.leaguemaster.application.usecase.ScheduleMatchesService;
import de.leaguemaster.cli.Command;
import de.leaguemaster.cli.CommandContext;
import de.leaguemaster.cli.modes.CommandResult;
import de.leaguemaster.cli.parser.CommandArgs;
import de.leaguemaster.domain.model.League;
import de.leaguemaster.domain.model.Match;
import de.leaguemaster.domain.model.Team;

public class ScheduleCommand implements Command {
    private final ScheduleMatchesService scheduleMatchesService;
    private final LeagueQueryService leagueQueryService;

    public ScheduleCommand(ScheduleMatchesService scheduleMatchesService, LeagueQueryService leagueQueryService) {
        this.scheduleMatchesService = scheduleMatchesService;
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
        if (!ctx.areTeamsConfirmed() && !action.equalsIgnoreCase("list")) {
            return CommandResult.invalid("Teams noch nicht bestaetigt. Nutze: team done");
        }

        if (action.equalsIgnoreCase("round-robin")) {
            try {
                scheduleMatchesService.roundRobin(ctx.getCurrentLeagueId());
                return scheduleAndShowFirstRound(ctx.getCurrentLeagueId());
            } catch (IllegalStateException e) {
                return CommandResult.invalid(e.getMessage());
            }
        }

        if (action.equalsIgnoreCase("list")) {
            return listRounds(ctx.getCurrentLeagueId());
        }

        return CommandResult.invalid("Unbekannte Aktion. " + helpText());
    }

    private CommandResult listRounds(String leagueId) {
        League league = leagueQueryService.byId(leagueId);
        if (league.matches().isEmpty()) {
            return CommandResult.ok("Noch kein Spielplan. Nutze: schedule round-robin");
        }
        StringBuilder sb = new StringBuilder("Spieltage:\n");
        for (int i = 0; i < league.totalRounds(); i++) {
            sb.append("Spieltag ").append(i + 1).append(":\n");
            appendRoundMatches(sb, league, i);
        }
        return CommandResult.ok(sb.toString().trim());
    }

    private String helpText() {
        return "Verwendung: schedule round-robin | schedule list";
    }

    private CommandResult scheduleAndShowFirstRound(String leagueId) {
        League league = leagueQueryService.byId(leagueId);
        int roundIndex = league.currentRoundIndex();
        if (roundIndex < 0) {
            return CommandResult.ok("Spielplan erstellt. Keine offenen Matches gefunden.");
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Spielplan erstellt. Runde ").append(roundIndex + 1).append("/").append(league.totalRounds()).append(":\n");
        appendRoundMatches(sb, league, roundIndex);
        sb.append("\nSpieltage anzeigen: match list (aktueller) oder match list --round <N>");
        sb.append("\nPunkteuebersicht: table show");
        sb.append("\nNaechster Schritt: Ergebnisse eintragen mit match record --id <ID> --score X:Y");
        return CommandResult.ok(sb.toString());
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
              .append(away.name())
              .append("\n");
        }
    }
}
