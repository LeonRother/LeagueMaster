package de.leaguemaster.cli.modes;

import de.leaguemaster.cli.CommandContext;
import de.leaguemaster.cli.parser.CommandSpec;

import java.util.List;

public interface GameMode {
    String name();          // z.B. "League"
    String description();   // kurze Erklärung
    void start();           // startet Interaktion innerhalb des Modus
    void onEnter(CommandContext context);
    List<CommandSpec> commands(CommandContext context);
}
