package de.leaguemaster.cli;

import de.leaguemaster.util.Logger;

import java.util.Scanner;

public class CLI {


    public void start() {
        System.out.println("Welcome to LeagueMaster!");
        Logger.log("Starting LeagueMaster!");

        GameMode.start();

    }
}
