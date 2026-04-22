package de.leaguemaster.cli.commands;

import de.leaguemaster.cli.Command;
import de.leaguemaster.cli.CommandContext;
import de.leaguemaster.cli.modes.CommandResult;
import de.leaguemaster.cli.modes.GameMode;
import de.leaguemaster.cli.parser.CommandArgs;

import java.util.function.Supplier;

public class ExitCommand implements Command {
    private final Supplier<GameMode> startModeSupplier;

    public ExitCommand(Supplier<GameMode> startModeSupplier) {
        this.startModeSupplier = startModeSupplier;
    }

    @Override
    public CommandResult execute(CommandContext ctx, CommandArgs args) {
        ctx.reset();
        return CommandResult.switchTo(startModeSupplier.get(), "Zurueck zur Modusauswahl.");
    }
}
