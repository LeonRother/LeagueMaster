package de.leaguemaster.application.usecase;

import de.leaguemaster.application.dto.TableRow;
import de.leaguemaster.domain.model.League;
import de.leaguemaster.domain.model.Match;
import de.leaguemaster.domain.model.Score;
import de.leaguemaster.domain.model.Team;
import de.leaguemaster.domain.repository.LeagueRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ShowTableService {
    private final LeagueRepository leagueRepository;

    public ShowTableService(LeagueRepository leagueRepository) {
        this.leagueRepository = leagueRepository;
    }

    public List<TableRow> execute(String leagueId) {
        Optional<League> leagueOpt = leagueRepository.findById(leagueId);
        if (leagueOpt.isEmpty()) {
            throw new IllegalArgumentException("Liga nicht gefunden.");
        }
        League league = leagueOpt.get();

        Map<String, Stats> stats = new HashMap<>();
        for (Team team : league.teams().values()) {
            stats.put(team.id(), new Stats(team.name()));
        }

        for (Match match : league.matches().values()) {
            if (!match.isPlayed()) continue;
            Score score = match.score();
            Stats home = stats.get(match.homeTeamId());
            Stats away = stats.get(match.awayTeamId());
            home.played++; away.played++;
            home.goalsFor += score.home();
            home.goalsAgainst += score.away();
            away.goalsFor += score.away();
            away.goalsAgainst += score.home();

            if (score.home() > score.away()) {
                home.wins++; away.losses++;
                home.points += 3;
            } else if (score.home() < score.away()) {
                away.wins++; home.losses++;
                away.points += 3;
            } else {
                home.draws++; away.draws++;
                home.points += 1; away.points += 1;
            }
        }

        List<TableRow> rows = new ArrayList<>();
        for (Stats s : stats.values()) {
            rows.add(new TableRow(
                    s.teamName,
                    s.played,
                    s.wins,
                    s.draws,
                    s.losses,
                    s.goalsFor,
                    s.goalsAgainst,
                    s.points
            ));
        }

        rows.sort(Comparator
                .comparingInt(TableRow::points).reversed()
                .thenComparing(r -> (r.goalsFor() - r.goalsAgainst()), Comparator.reverseOrder())
                .thenComparing(TableRow::teamName));

        return rows;
    }

    private static class Stats {
        private final String teamName;
        private int played;
        private int wins;
        private int draws;
        private int losses;
        private int goalsFor;
        private int goalsAgainst;
        private int points;

        private Stats(String teamName) {
            this.teamName = teamName;
        }
    }
}
