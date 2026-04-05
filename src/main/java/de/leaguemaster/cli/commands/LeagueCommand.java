package de.leaguemaster.cli.commands;

import de.leaguemaster.application.dto.CreateLeagueRequest;
import de.leaguemaster.application.usecase.CreateLeagueService;
import de.leaguemaster.cli.Command;
import de.leaguemaster.cli.CommandContext;
import de.leaguemaster.cli.modes.CommandResult;
import de.leaguemaster.cli.modes.CreateLeagueWizard;
import de.leaguemaster.cli.parser.CommandArgs;

public class LeagueCommand implements Command {
    private final CreateLeagueService createLeagueService;

    public LeagueCommand(CreateLeagueService createLeagueService) {
        this.createLeagueService = createLeagueService;
    }

    @Override
    public CommandResult execute(CommandContext ctx, CommandArgs args) {
        String action = args.pos(0);
        if (action == null || action.equalsIgnoreCase("help")) {
            return CommandResult.ok(helpText());
        }

        if (action.equalsIgnoreCase("create")) {
            String name = args.getOption("name");
            if (name != null && !name.isBlank()) {
                String id = createLeagueService.execute(new CreateLeagueRequest(name)).id();
                ctx.setCurrentLeagueId(id);
                return CommandResult.ok("Liga erstellt. ID: " + id + "\nNaechste Schritte: mindestens 4 Teams hinzufuegen mit team add --name <TEAM>, dann team done, dann schedule round-robin");
            }

            ctx.startInteraction(new CreateLeagueWizard(ctx, createLeagueService));
            return CommandResult.ok("Erstellung gestartet. Du kannst den Namen eingeben.");
        }

        return CommandResult.invalid("Unbekannte Aktion. " + helpText());
    }

    private String helpText() {
        return "Verwendung: league create [--name <NAME>]";
    }
}
