package de.leaguemaster.cli.parser;

import de.leaguemaster.cli.Command;

import java.util.*;

/**
 * Registry für alle aktuell verfügbaren Commands.
 *
 * Verantwortlichkeiten:
 * - Commands registrieren (Name, Beschreibung, Aliase)
 * - Commands anhand des Namens auflösen
 * - Help-Text generieren
 */
public class CommandRegistry {

    /** kanonischer Name -> Command */
    private final Map<String, Command> commands = new LinkedHashMap<>();

    /** kanonischer Name -> Beschreibung */
    private final Map<String, String> descriptions = new LinkedHashMap<>();

    /** Alias -> kanonischer Name */
    private final Map<String, String> aliases = new HashMap<>();

    /**
     * Registriert einen neuen Command.
     *
     * @param name        kanonischer Name (z. B. "create-league")
     * @param command     Command-Implementierung
     * @param description kurze Beschreibung
     * @param aliasNames  optionale Aliase (z. B. "cl", "create-league")
     */
    public void register(String name,
                         Command command,
                         String description,
                         String... aliasNames) {

        String canonical = normalize(name);

        commands.put(canonical, command);
        descriptions.put(canonical, description == null ? "" : description);

        if (aliasNames != null) {
            for (String alias : aliasNames) {
                if (alias != null && !alias.isBlank()) {
                    aliases.put(normalize(alias), canonical);
                }
            }
        }
    }

    /**
     * Löst einen Command-Namen (oder Alias) auf.
     *
     * @param input Name oder Alias
     * @return Optional<Command>
     */
    public Optional<Command> resolve(String input) {
        if (input == null || input.isBlank()) {
            return Optional.empty();
        }

        String key = normalize(input);

        // 1) Direkter Treffer
        if (commands.containsKey(key)) {
            return Optional.of(commands.get(key));
        }

        // 2) Alias-Treffer
        if (aliases.containsKey(key)) {
            String canonical = aliases.get(key);
            return Optional.ofNullable(commands.get(canonical));
        }

        return Optional.empty();
    }

    /**
     * Entfernt alle registrierten Commands.
     * (z. B. beim Wechsel des GameModes)
     */
    public void clear() {
        commands.clear();
        descriptions.clear();
        aliases.clear();
    }

    /**
     * Erzeugt eine Hilfe-Ausgabe für den CLI-Befehl "help".
     */
    public String help() {
        StringBuilder sb = new StringBuilder();
        sb.append("Verfügbare Befehle:\n");

        for (String name : commands.keySet()) {
            sb.append("  ").append(name);

            // Aliase anzeigen
            List<String> aliasList = new ArrayList<>();
            for (Map.Entry<String, String> e : aliases.entrySet()) {
                if (e.getValue().equals(name)) {
                    aliasList.add(e.getKey());
                }
            }

            if (!aliasList.isEmpty()) {
                sb.append(" (Aliase: ")
                        .append(String.join(", ", aliasList))
                        .append(")");
            }

            String desc = descriptions.get(name);
            if (desc != null && !desc.isBlank()) {
                sb.append("\n      ").append(desc);
            }

            sb.append("\n");
        }

        return sb.toString();
    }

    private String normalize(String s) {
        return s.trim().toLowerCase(Locale.ROOT);
    }
}