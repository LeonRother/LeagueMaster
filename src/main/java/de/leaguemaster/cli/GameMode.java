package de.leaguemaster.cli;


import java.util.Scanner;

public class GameMode {
    private static final CommandParser parser = new CommandParser();
    static Scanner sc = new Scanner(System.in);
    public static void start() {
        System.out.println("Please choose your game mode");
        while (true) {
            System.out.print("> ");
            String input = sc.nextLine();
            parser.handle(input);
        }
    }
}
