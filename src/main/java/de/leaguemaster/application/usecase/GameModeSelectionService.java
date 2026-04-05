package de.leaguemaster.application.usecase;

import de.leaguemaster.cli.CLI;
import de.leaguemaster.cli.GameModeFactory;
import de.leaguemaster.cli.GameModeType;
import de.leaguemaster.cli.commands.SelectModeCommand;
import de.leaguemaster.cli.parser.CommandSpec;

import java.util.List;

public class GameModeSelectionService {
    /*
    Verfügbare GameModes abfragen
    User-Auswahl entgegennehmen
    Passenden GameMode erzeugen
    Kontrolle an den GameMode übergeben
     */
    private final CLI cli;
    private final GameModeFactory factory;

    public GameModeSelectionService(CLI cli, GameModeFactory factory) {
        this.cli = cli;
        this.factory = factory;
    }

    public List<CommandSpec> execute() {
        List<CommandSpec> commands = selectionCommands();

        System.out.println("Bitte wähle einen Spielmodus:\n");

        int index = 1;
        for (GameModeType type : GameModeType.values()) {
            String availability = factory.isSupported(type)
                    ? ""
                    : " (noch nicht verfügbar)";
            System.out.println(
                    index++ + ") " +
                            type.displayName() +
                            " – " +
                            type.description() +
                            availability
            );
        }

        System.out.println("\nEingabe: Nummer oder Name des Spielmodus");
        cli.readInput();
        return commands;
    }

    private List<CommandSpec> selectionCommands() {
        return List.of(
                new CommandSpec(
                        "league",
                        new SelectModeCommand(GameModeType.LEAGUE, factory),
                        "Wechselt zum League-Modus.",
                        "1",
                        "l"
                ),
                new CommandSpec(
                        "knockout",
                        new SelectModeCommand(GameModeType.KNOCKOUT, factory),
                        "Wechselt zum Knockout-Modus.",
                        "2",
                        "ko"
                ),
                new CommandSpec(
                        "group-stage",
                        new SelectModeCommand(GameModeType.GROUP_STAGE, factory),
                        "Wechselt zum Group-Stage-Modus.",
                        "3",
                        "gs",
                        "group",
                        "groupstage"
                )
        );
    }
}
