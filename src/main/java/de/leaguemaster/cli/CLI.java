package de.leaguemaster.cli;


import de.leaguemaster.application.usecase.GameModeSelectionService;

import java.util.Scanner;

public class CLI {
    private final CommandParser parser;
    private final Scanner scanner;

    public CLI(Scanner scanner, CommandParser parser) {
        this.scanner = scanner;
        this.parser = parser;
    }

    public void start() {
        System.out.println("Willkommen bei LeagueMaster!");
        System.out.println("Tippe 'help' um Befehle anzuzeigen.");
        GameModeSelectionService gMSS = new GameModeSelectionService(this);
        gMSS.execute();
        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine().trim();
            parser.handle(input);
        }
    }
    public void readInput(){
        String input = scanner.nextLine().trim();
        parser.handle(input);
    }
}