package de.leaguemaster.cli.parser;


import java.util.*;

public class CommandArgs {

    private final String commandName;
    private final List<String> positionals = new ArrayList<>();
    private final Map<String, String> options = new HashMap<>();

    private CommandArgs(String commandName) {
        this.commandName = commandName;
    }

    public String command() {
        return commandName;
    }

    public List<String> pos() {
        return positionals;
    }

    public String pos(int i) {
        return (i < positionals.size()) ? positionals.get(i) : null;
    }

    public boolean hasOption(String key) {
        return options.containsKey(key);
    }

    public String getOption(String key) {
        return options.get(key);
    }

    public static CommandArgs parse(String input) {
        List<String> tokens = tokenize(input);
        if (tokens.isEmpty()) return new CommandArgs("");

        CommandArgs args = new CommandArgs(tokens.get(0));

        for (int i = 1; i < tokens.size(); i++) {
            String t = tokens.get(i);

            if (t.startsWith("--")) {
                String key = t.substring(2);

                String value = "true";
                if (i + 1 < tokens.size() && !tokens.get(i + 1).startsWith("--")) {
                    value = tokens.get(++i);
                }

                args.options.put(key, value);
            } else {
                args.positionals.add(t);
            }
        }

        return args;
    }

    /** Tokenizer supporting quotes */
    private static List<String> tokenize(String input) {
        List<String> result = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder current = new StringBuilder();

        for (char c : input.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes;
                continue;
            }
            if (c == ' ' && !inQuotes) {
                if (current.length() > 0) {
                    result.add(current.toString());
                    current.setLength(0);
                }
            } else {
                current.append(c);
            }
        }
        if (current.length() > 0) result.add(current.toString());
        return result;
    }
}