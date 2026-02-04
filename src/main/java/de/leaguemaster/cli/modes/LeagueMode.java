package de.leaguemaster.cli.modes;

// domain/gamemode/LeagueMode.java

import de.leaguemaster.cli.CommandContext;
import de.leaguemaster.cli.CommandParser;

import java.util.Scanner;

public class LeagueMode implements GameMode {
    private final Scanner scanner;
    public LeagueMode(Scanner scanner) {
        this.scanner = scanner;
    }

    @Override
    public String name() {
        return "League";
    }

    @Override
    public String description() {
        return "Klassische Liga mit Spieltagen und Tabelle.";
    }

    @Override
    public void start() {
        System.out.println("[LeagueMode] Willkommen! Verfügbare Befehle:");
        System.out.println(" - league create --name <NAME>");
        System.out.println(" - team add --name <NAME>");
        System.out.println(" - schedule round-robin");
        System.out.println(" - match record --id <ID> --score <X:Y>");
        System.out.println(" - table show");
        System.out.println("Gib 'exit' ein, um zum Hauptmenü zurückzukehren.");

        while (true) {
            System.out.print("league> ");
            String line = scanner.nextLine().trim();
            if (line.equalsIgnoreCase("exit")) {
                System.out.println("Zurück zum Hauptmenü.");
                break;
            }
            // Hier später: deinen CommandParser aufrufen, z.B. parser.handle(line);
            // Für den Anfang ein Platzhalter:
            if (line.isEmpty()) continue;
            System.out.println("[LeagueMode] Eingabe empfangen: " + line);
        }
    }

    @Override
    public void onEnter(CommandParser parser, CommandContext context) {

    }
}