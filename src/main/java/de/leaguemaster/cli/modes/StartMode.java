package de.leaguemaster.cli.modes;

import de.leaguemaster.application.usecase.GameModeSelectionService;
import de.leaguemaster.cli.CommandContext;
import de.leaguemaster.cli.GameModeFactory;
import de.leaguemaster.cli.parser.CommandSpec;

import java.util.List;

public class StartMode implements GameMode {
    private final GameModeSelectionService selectionService;

    public StartMode(GameModeFactory factory) {
        this.selectionService = new GameModeSelectionService(factory);
    }

    @Override
    public String name() {
        return "Start";
    }

    @Override
    public String description() {
        return "Modusauswahl.";
    }

    @Override
    public void start() {
    }

    @Override
    public void onEnter(CommandContext context) {
        selectionService.printSelection();
    }

    @Override
    public List<CommandSpec> commands(CommandContext context) {
        return selectionService.selectionCommands();
    }
}
