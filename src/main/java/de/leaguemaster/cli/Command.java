package de.leaguemaster.cli;


import de.leaguemaster.cli.modes.CommandResult;
import de.leaguemaster.cli.parser.CommandArgs;

public interface Command {
    CommandResult execute(CommandContext ctx, CommandArgs args);
}
