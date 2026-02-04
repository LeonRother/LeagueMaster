package de.leaguemaster.cli;

import de.leaguemaster.cli.modes.GameMode;
import de.leaguemaster.cli.parser.CommandArgs;
import de.leaguemaster.cli.parser.CommandRegistry;

public class CommandParser {
    private GameMode activeMode;

    private final CommandRegistry registry = new CommandRegistry();

    private final CommandContext ctx = new CommandContext();
    public void handle(String input) {
        input = input.trim();
        if (input.isEmpty()) return;

        if (ctx.hasPendingInteraction()) {
            ctx.resumeInteraction(input); // siehe Pattern #3 unten
            return;
        }

        String[] parts = input.split("\\s+");

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
                                registry.clear();
                                next.onEnter(this, ctx);
                            });
                        },
                        () -> System.out.println("Unbekannter Befehl. Tippe 'help'.")
                );
    }
}
