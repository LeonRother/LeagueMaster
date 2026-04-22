package de.leaguemaster.application.usecase;

import de.leaguemaster.cli.GameModeFactory;
import de.leaguemaster.cli.GameModeType;
import de.leaguemaster.cli.commands.SelectModeCommand;
import de.leaguemaster.cli.parser.CommandSpec;

import java.util.List;

public class GameModeSelectionService {
    private final GameModeFactory factory;

    public GameModeSelectionService(GameModeFactory factory) {
        this.factory = factory;
    }

    public void printSelection() {
        System.out.println("Bitte waehle einen Spielmodus:\n");

        int index = 1;
        for (GameModeType type : GameModeType.values()) {
            String availability = factory.isSupported(type)
                    ? ""
                    : " (noch nicht verfuegbar)";
            System.out.println(
                    index++ + ") " +
                            type.displayName() +
                            " - " +
                            type.description() +
                            availability
            );
        }

        System.out.println("\nEingabe: Nummer oder Name des Spielmodus");
    }

    public List<CommandSpec> selectionCommands() {
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
                )
        );
    }
}
