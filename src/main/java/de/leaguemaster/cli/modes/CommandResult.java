package de.leaguemaster.cli.modes;

import java.util.Optional;

public class CommandResult {
    public enum Status { OK, INVALID_INPUT, NOT_FOUND, ERROR }

    private final Status status;
    private final String message;
    private final Optional<GameMode> nextMode;

    private CommandResult(Status status, String message, Optional<GameMode> nextMode) {
        this.status = status; this.message = message; this.nextMode = nextMode;
    }

    public static CommandResult ok(String msg)                 { return new CommandResult(Status.OK, msg, Optional.empty()); }
    public static CommandResult invalid(String msg)            { return new CommandResult(Status.INVALID_INPUT, msg, Optional.empty()); }
    public static CommandResult notFound(String msg)           { return new CommandResult(Status.NOT_FOUND, msg, Optional.empty()); }
    public static CommandResult error(String msg)              { return new CommandResult(Status.ERROR, msg, Optional.empty()); }
    public static CommandResult switchTo(GameMode next, String msg) { return new CommandResult(Status.OK, msg, Optional.of(next)); }

    public Status status() { return status; }
    public String message() { return message; }
    public Optional<GameMode> nextMode() { return nextMode; }
}