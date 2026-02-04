package de.leaguemaster.cli.parser;


public interface PendingInteraction {
    /**
     * @return true, wenn die Interaktion abgeschlossen ist (State wird gelöscht)
     */
    boolean onInput(String line);
}