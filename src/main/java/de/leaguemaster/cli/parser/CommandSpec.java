package de.leaguemaster.cli.parser;

import de.leaguemaster.cli.Command;

public record CommandSpec(
        String name,
        Command command,
        String description,
        String... aliases
) {
}
