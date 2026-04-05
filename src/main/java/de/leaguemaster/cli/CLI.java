package de.leaguemaster.cli;


import de.leaguemaster.application.usecase.GameModeSelectionService;
import de.leaguemaster.cli.commands.HelpCommand;
import de.leaguemaster.cli.modes.GameMode;
import de.leaguemaster.cli.parser.CommandSpec;

import java.util.Scanner;
import java.util.List;

public class CLI {
    private final CommandParser parser;
    private final Scanner scanner;
    private final GameModeFactory factory;

    public CLI(Scanner scanner, CommandParser parser, GameModeFactory factory) {
        this.scanner = scanner;
        this.parser = parser;
        this.factory = factory;
        this.parser.setModeSwitchHandler(this::enterMode);
    }

    public void start() {
        System.out.println("Willkommen bei LeagueMaster!");
        System.out.println("Tippe 'help' um Befehle anzuzeigen.");
        GameModeSelectionService gMSS = new GameModeSelectionService(this, factory);
        parser.registerAll(baseCommands());
        parser.registerAll(gMSS.execute());
        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine().trim();
            parser.handle(input);
        }
    }

    public CommandParser parser() {
        return parser;
    }

    public Scanner scanner() {
        return scanner;
    }

    public void enterMode(GameMode mode) {
        parser.clearRegistry();
        mode.onEnter(parser.context());
        parser.registerAll(baseCommands());
        parser.registerAll(mode.commands(parser.context()));
    }

    private List<CommandSpec> baseCommands() {
        return List.of(
                new CommandSpec(
                        "help",
                        new HelpCommand(parser::help),
                        "Zeigt diese Hilfe an.",
                        "?")
        );
    }
    public void readInput(){
        String input = scanner.nextLine().trim();
        parser.handle(input);
    }
}
