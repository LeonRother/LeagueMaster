package de.leaguemaster.domain.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class League {
    private final String id;
    private final String name;
    private final Map<String, Team> teams = new LinkedHashMap<>();
    private final Map<String, Match> matches = new LinkedHashMap<>();
    private final List<List<String>> rounds = new ArrayList<>();
    private int nextMatchNumber = 1;

    public League(String name) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
    }

    public String id() {
        return id;
    }

    public String name() {
        return name;
    }

    public Map<String, Team> teams() {
        return Collections.unmodifiableMap(teams);
    }

    public Map<String, Match> matches() {
        return Collections.unmodifiableMap(matches);
    }

    public Team addTeam(String teamName) {
        if (hasTeamName(teamName)) {
            throw new IllegalArgumentException("Team-Name bereits vorhanden.");
        }
        String id = "T" + (teams.size() + 1);
        Team team = new Team(id, teamName);
        teams.put(id, team);
        return team;
    }

    public boolean hasTeamName(String teamName) {
        String normalized = normalize(teamName);
        for (Team team : teams.values()) {
            if (normalize(team.name()).equals(normalized)) {
                return true;
            }
        }
        return false;
    }

    private String normalize(String name) {
        return name == null ? "" : name.trim().toLowerCase();
    }

    public boolean hasMatches() {
        return !matches.isEmpty();
    }

    public List<Match> scheduleRoundRobin() {
        List<Team> list = new ArrayList<>(teams.values());
        if (list.size() < 2) {
            return Collections.emptyList();
        }

        List<Team> rotation = new ArrayList<>(list);
        if (rotation.size() % 2 != 0) {
            rotation.add(null);
        }

        int n = rotation.size();
        int roundsCount = n - 1;
        List<Match> created = new ArrayList<>();

        for (int r = 0; r < roundsCount; r++) {
            List<String> roundMatchIds = new ArrayList<>();
            for (int i = 0; i < n / 2; i++) {
                Team home = rotation.get(i);
                Team away = rotation.get(n - 1 - i);
                if (home == null || away == null) {
                    continue;
                }
                String matchId = "M" + nextMatchNumber++;
                Match match = new Match(matchId, home.id(), away.id());
                matches.put(matchId, match);
                created.add(match);
                roundMatchIds.add(matchId);
            }
            rounds.add(roundMatchIds);

            Team last = rotation.remove(rotation.size() - 1);
            rotation.add(1, last);
        }

        return created;
    }

    public Match findMatch(String matchId) {
        return matches.get(matchId);
    }

    public int totalRounds() {
        return rounds.size();
    }

    public List<Match> round(int roundIndex) {
        if (roundIndex < 0 || roundIndex >= rounds.size()) {
            return Collections.emptyList();
        }
        List<Match> result = new ArrayList<>();
        for (String id : rounds.get(roundIndex)) {
            Match match = matches.get(id);
            if (match != null) {
                result.add(match);
            }
        }
        return result;
    }

    public int currentRoundIndex() {
        for (int i = 0; i < rounds.size(); i++) {
            List<String> ids = rounds.get(i);
            for (String id : ids) {
                Match match = matches.get(id);
                if (match != null && !match.isPlayed()) {
                    return i;
                }
            }
        }
        return -1;
    }
}
