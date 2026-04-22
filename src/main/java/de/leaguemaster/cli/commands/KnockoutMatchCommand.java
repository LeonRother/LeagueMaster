package de.leaguemaster.cli.commands;

import de.leaguemaster.application.usecase.LeagueQueryService;
import de.leaguemaster.application.usecase.RecordMatchResultService;
import de.leaguemaster.cli.Command;
import de.leaguemaster.cli.CommandContext;
import de.leaguemaster.cli.modes.CommandResult;
import de.leaguemaster.cli.output.KnockoutBracketRenderer;
import de.leaguemaster.cli.parser.CommandArgs;
import de.leaguemaster.domain.model.League;
import de.leaguemaster.domain.model.Team;

public class KnockoutMatchCommand implements Command {
    private final RecordMatchResultService recordMatchResultService;
    private final LeagueQueryService leagueQueryService;

    public KnockoutMatchCommand(RecordMatchResultService recordMatchResultService,
                                LeagueQueryService leagueQueryService) {
        this.recordMatchResultService = recordMatchResultService;
        this.leagueQueryService = leagueQueryService;
    }

    @Override
    public CommandResult execute(CommandContext ctx, CommandArgs args) {
        String action = args.pos(0);
        if (action == null || action.equalsIgnoreCase("help")) {
            return CommandResult.ok("Verwendung: match list | match record --id <ID> --score X:Y");
        }

        if (ctx.getCurrentLeagueId() == null) {
            return CommandResult.invalid("Kein aktives Turnier. Nutze: create --name <NAME>");
        }

        if (action.equalsIgnoreCase("list")) {
            League league = leagueQueryService.byId(ctx.getCurrentLeagueId());
            return CommandResult.ok(KnockoutBracketRenderer.render(league));
        }

        if (!action.equalsIgnoreCase("record")) {
            return CommandResult.invalid("Unbekannte Aktion. Verwendung: match list | match record --id <ID> --score X:Y");
        }

        String id = args.getOption("id");
        String score = args.getOption("score");
        if (id == null || id.isBlank()) {
            return CommandResult.invalid("Fehlende Match-ID. Beispiel: match record --id M1 --score 2:1");
        }
        if (score == null || score.isBlank()) {
            return CommandResult.invalid("Fehlender Score. Beispiel: match record --id M1 --score 2:1");
        }

        int[] parsedScore = parseScore(score);
        if (parsedScore == null) {
            return CommandResult.invalid("Ungueltiger Score. Im Knockout ist nur X:Y ohne Unentschieden erlaubt.");
        }

        try {
            recordMatchResultService.execute(ctx.getCurrentLeagueId(), id, parsedScore[0], parsedScore[1]);
            League league = leagueQueryService.byId(ctx.getCurrentLeagueId());
            Team champion = league.champion();
            if (champion != null) {
                return CommandResult.ok("Ergebnis gespeichert.\n" + KnockoutBracketRenderer.render(league)
                        + "\n\nSieger des Turniers: " + champion.name());
            }
            return CommandResult.ok("Ergebnis gespeichert.\n" + KnockoutBracketRenderer.render(league)
                    + "\n\nNaechster Schritt: weitere Ergebnisse mit match record --id <ID> --score X:Y");
        } catch (IllegalArgumentException | IllegalStateException e) {
            return CommandResult.invalid(e.getMessage());
        }
    }

    private int[] parseScore(String score) {
        String[] parts = score.split(":");
        if (parts.length != 2) {
            return null;
        }
        try {
            int home = Integer.parseInt(parts[0]);
            int away = Integer.parseInt(parts[1]);
            if (home < 0 || away < 0 || home == away) {
                return null;
            }
            return new int[] {home, away};
        } catch (NumberFormatException exception) {
            return null;
        }
    }
}
