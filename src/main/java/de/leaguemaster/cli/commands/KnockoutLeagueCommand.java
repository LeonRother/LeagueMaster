package de.leaguemaster.cli.commands;

import de.leaguemaster.application.dto.CreateLeagueRequest;
import de.leaguemaster.application.usecase.CreateLeagueService;
import de.leaguemaster.cli.Command;
import de.leaguemaster.cli.CommandContext;
import de.leaguemaster.cli.modes.CommandResult;
import de.leaguemaster.cli.parser.CommandArgs;
import de.leaguemaster.domain.model.CompetitionFormat;

public class KnockoutLeagueCommand implements Command {
    private final CreateLeagueService createLeagueService;

    public KnockoutLeagueCommand(CreateLeagueService createLeagueService) {
        this.createLeagueService = createLeagueService;
    }

    @Override
    public CommandResult execute(CommandContext ctx, CommandArgs args) {
        String name = args.getOption("name");
        if (name == null || name.isBlank()) {
            return CommandResult.invalid("Fehlender Turnier-Name. Beispiel: create --name \"KO Cup\"");
        }

        String id = createLeagueService.execute(new CreateLeagueRequest(name, CompetitionFormat.KNOCKOUT)).id();
        ctx.setCurrentLeagueId(id);
        ctx.resetTeamsConfirmation();
        return CommandResult.ok("Knockout-Turnier erstellt. ID: " + id
                + "\nNaechste Schritte: Teams hinzufuegen mit team add --name <TEAM>, dann team done");
    }
}
