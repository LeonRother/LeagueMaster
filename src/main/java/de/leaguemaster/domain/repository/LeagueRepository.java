package de.leaguemaster.domain.repository;
import de.leaguemaster.domain.model.League;

import java.util.List;
import java.util.Optional;

/**
 * Domain-Port für das Speichern und Laden von Ligen.
 *
 * WICHTIG (Clean Architecture):
 * - Interface liegt im Domain-Layer
 * - KEIN Bezug zu JSON, Files, Datenbanken, CLI, etc.
 * - Implementierungen liegen im Infrastructure-Layer
 */
public interface LeagueRepository {

    /**
     * Speichert eine Liga (neu oder aktualisiert).
     */
    void save(League league);

    /**
     * Findet eine Liga anhand ihrer ID.
     */
    Optional<League> findById(String leagueId);

    /**
     * Liefert alle gespeicherten Ligen.
     * (z. B. für "list leagues")
     */
    List<League> findAll();

    /**
     * Löscht eine Liga (optional, aber oft sinnvoll).
     */
    void deleteById(String leagueId);
}