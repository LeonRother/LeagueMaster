package de.leaguemaster.cli.modes;

import de.leaguemaster.cli.CommandContext;
import de.leaguemaster.cli.CommandParser;

public interface GameMode {
    String name();          // z.B. "League"
    String description();   // kurze Erklärung
    void start();           // startet Interaktion innerhalb des Modus
    void onEnter(CommandParser parser, CommandContext context);
}

