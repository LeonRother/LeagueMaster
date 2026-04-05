package de.leaguemaster.cli.commands;

import de.leaguemaster.cli.Command;
import de.leaguemaster.cli.CommandContext;
import de.leaguemaster.cli.modes.CommandResult;
import de.leaguemaster.cli.parser.CommandArgs;

import java.util.function.Supplier;

public class HelpCommand implements Command {
    private final Supplier<String> helpSupplier;

    public HelpCommand(Supplier<String> helpSupplier) {
        this.helpSupplier = helpSupplier;
    }

    @Override
    public CommandResult execute(CommandContext ctx, CommandArgs args) {
        return CommandResult.ok(helpSupplier.get());
    }
}
