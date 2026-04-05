package de.leaguemaster.cli;

import de.leaguemaster.cli.modes.GameMode;
import de.leaguemaster.cli.parser.CommandArgs;
import de.leaguemaster.cli.parser.CommandRegistry;
import de.leaguemaster.cli.parser.CommandSpec;

import java.util.Collection;
import java.util.function.Consumer;

public class CommandParser {
    private final CommandRegistry registry = new CommandRegistry();

    private final CommandContext ctx = new CommandContext();
    private Consumer<GameMode> modeSwitchHandler;

    public void setModeSwitchHandler(Consumer<GameMode> modeSwitchHandler) {
        this.modeSwitchHandler = modeSwitchHandler;
    }

    public void register(CommandSpec spec) {
        registry.register(spec.name(), spec.command(), spec.description(), spec.aliases());
    }

    public void registerAll(Collection<CommandSpec> specs) {
        if (specs == null || specs.isEmpty()) return;
        for (CommandSpec spec : specs) {
            register(spec);
        }
    }

    public void register(String name, Command command, String description, String... aliasNames) {
        registry.register(name, command, description, aliasNames);
    }

    public String help() {
        return registry.help();
    }

    public void clearRegistry() {
        registry.clear();
    }

    public CommandContext context() {
        return ctx;
    }

    public void handle(String input) {
        input = input.trim();
        if (input.isEmpty()) return;

        if (ctx.hasPendingInteraction()) {
            ctx.resumeInteraction(input); // siehe Pattern #3 unten
            return;
        }

        CommandArgs args = CommandArgs.parse(input);
        String cmdName = args.command();

        registry.resolve(cmdName)
                .map(cmd -> cmd.execute(ctx,args))
                .ifPresentOrElse(
                        result -> {
                            // Ausgabe
                    if (result.message() != null && !result.message().isBlank()) {
                        System.out.println(result.message());
                    }

                    // Mode-Wechsel?
                    result.nextMode().ifPresent(next -> {
                        if (modeSwitchHandler != null) {
                            modeSwitchHandler.accept(next);
                        }
                    });
                },
                () -> System.out.println("Unbekannter Befehl. Tippe 'help'.")
        );
    }
}
