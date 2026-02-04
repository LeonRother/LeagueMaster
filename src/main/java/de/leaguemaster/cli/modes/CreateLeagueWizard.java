package de.leaguemaster.cli.modes;

import de.leaguemaster.application.dto.CreateLeagueRequest;
import de.leaguemaster.application.usecase.CreateLeagueService;
import de.leaguemaster.cli.CommandContext;
import de.leaguemaster.cli.CommandParser;
import de.leaguemaster.cli.parser.PendingInteraction;

public class CreateLeagueWizard implements PendingInteraction {
    private final CommandParser parser;
    private final CommandContext ctx;
    private final CreateLeagueService useCase;
    private String name;
    private Integer pointsWin;

    public CreateLeagueWizard(CommandParser parser, CommandContext ctx, CreateLeagueService ls) {
        this.parser = parser; this.ctx = ctx; this.useCase = ls;
        System.out.print("Liga-Name: ");
    }

    @Override
    public boolean onInput(String line) {
        if (name == null) {
            name = line.trim();
            System.out.print("Punkte für Sieg (z. B. 3): ");
            return false;
        }
        if (pointsWin == null) {
            try {
                pointsWin = Integer.parseInt(line.trim());
            } catch (NumberFormatException e) {
                System.out.print("Bitte Zahl eingeben: ");
                return false;
            }
            var id = useCase.execute(new CreateLeagueRequest(name)).id();
            ctx.setCurrentLeagueId(id);
            System.out.println("Liga erstellt mit ID: " + id);
            return true; // fertig
        }
        return true;
    }
}