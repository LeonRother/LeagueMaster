package de.leaguemaster.cli;


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

        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine().trim();
            parser.handle(input);
        }
    }
}