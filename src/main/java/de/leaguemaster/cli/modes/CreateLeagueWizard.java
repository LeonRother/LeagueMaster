package de.leaguemaster.cli.modes;

import de.leaguemaster.application.dto.CreateLeagueRequest;
import de.leaguemaster.application.usecase.CreateLeagueService;
import de.leaguemaster.cli.CommandContext;
import de.leaguemaster.cli.parser.PendingInteraction;

public class CreateLeagueWizard implements PendingInteraction {
    private final CommandContext ctx;
    private final CreateLeagueService useCase;
    private String name;

    public CreateLeagueWizard(CommandContext ctx, CreateLeagueService ls) {
        this.ctx = ctx; this.useCase = ls;
        System.out.print("Liga-Name (z. B. \"Sommerliga\"): ");
    }

    @Override
    public boolean onInput(String line) {
        if (name == null) {
            name = line.trim();
            if (name.isBlank()) {
                System.out.print("Name darf nicht leer sein. Bitte erneut: ");
                return false;
            }
            String id = useCase.execute(new CreateLeagueRequest(name)).id();
            ctx.setCurrentLeagueId(id);
            System.out.println("Liga erstellt. ID: " + id);
            System.out.println("Naechster Schritt: mindestens 4 Teams hinzufuegen mit team add --name <TEAM>. Dann team done");
            return true;
        }
        return true;
    }
}
