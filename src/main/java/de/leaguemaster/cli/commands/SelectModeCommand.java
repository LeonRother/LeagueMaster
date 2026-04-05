package de.leaguemaster.cli.commands;

import de.leaguemaster.cli.Command;
import de.leaguemaster.cli.CommandContext;
import de.leaguemaster.cli.GameModeFactory;
import de.leaguemaster.cli.GameModeType;
import de.leaguemaster.cli.modes.CommandResult;
import de.leaguemaster.cli.modes.GameMode;
import de.leaguemaster.cli.parser.CommandArgs;

public class SelectModeCommand implements Command {
    private final GameModeType type;
    private final GameModeFactory factory;

    public SelectModeCommand(GameModeType type, GameModeFactory factory) {
        this.type = type;
        this.factory = factory;
    }

    @Override
    public CommandResult execute(CommandContext ctx, CommandArgs args) {
        if (!factory.isSupported(type)) {
            return CommandResult.invalid("Modus '" + type.displayName() + "' ist noch nicht verfuegbar.");
        }

        GameMode mode = factory.create(type);
        return CommandResult.switchTo(mode, "Modus gewechselt zu: " + type.displayName());
    }
}
