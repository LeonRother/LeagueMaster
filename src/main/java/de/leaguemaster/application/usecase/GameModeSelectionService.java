package de.leaguemaster.application.usecase;

import de.leaguemaster.cli.CLI;
import de.leaguemaster.cli.GameModeType;

public class GameModeSelectionService {
    /*
    Verfügbare GameModes abfragen
    User-Auswahl entgegennehmen
    Passenden GameMode erzeugen
    Kontrolle an den GameMode übergeben
     */
    CLI cli;
    public GameModeSelectionService(CLI cli) {
        this.cli = cli;
    }

    public void execute() {

        System.out.println("Bitte wähle einen Spielmodus:\n");

        int index = 1;
        for (GameModeType type : GameModeType.values()) {
            System.out.println(
                    index++ + ") " +
                            type.displayName() +
                            " – " +
                            type.description()
            );
        }

        System.out.println("\nEingabe: Nummer oder Name des Spielmodus");
        cli.readInput();
    }
}
