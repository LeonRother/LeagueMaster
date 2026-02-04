package de.leaguemaster;

import de.leaguemaster.cli.CLI;
import de.leaguemaster.cli.CommandParser;

import java.util.Scanner;
public class Main {
    public static void main(String args[]) {
        Scanner sc = new Scanner(System.in);
        CommandParser cp = new CommandParser();
        CLI cli = new CLI(sc,cp);
        cli.start();
    }
}
