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
    private final CompetitionFormat format;
    private final Map<String, Team> teams = new LinkedHashMap<>();
    private final Map<String, Match> matches = new LinkedHashMap<>();
    private final List<List<String>> rounds = new ArrayList<>();
    private int nextMatchNumber = 1;

    public League(String name) {
        this(name, CompetitionFormat.LEAGUE);
    }

    public League(String name, CompetitionFormat format) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.format = format;
    }

    public String id() {
        return id;
    }

    public String name() {
        return name;
    }

    public CompetitionFormat format() {
        return format;
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
        ensureFormat(CompetitionFormat.LEAGUE);
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

    public List<Match> scheduleKnockout() {
        ensureFormat(CompetitionFormat.KNOCKOUT);
        validateKnockoutTeamCount();
        List<Team> participants = new ArrayList<>(teams.values());
        return createKnockoutRound(participants);
    }

    public Match findMatch(String matchId) {
        return matches.get(matchId);
    }

    public Match recordMatchResult(String matchId, Score score) {
        Match match = matches.get(matchId);
        if (match == null) {
            throw new IllegalArgumentException("Match nicht gefunden.");
        }
        if (format == CompetitionFormat.KNOCKOUT && score.home() == score.away()) {
            throw new IllegalArgumentException("Im Knockout sind keine Unentschieden erlaubt.");
        }

        match.recordScore(score);

        if (format == CompetitionFormat.KNOCKOUT) {
            createNextKnockoutRoundIfReady();
        }
        return match;
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

    public Team champion() {
        if (format != CompetitionFormat.KNOCKOUT || currentRoundIndex() != -1 || rounds.isEmpty()) {
            return null;
        }
        List<Match> finalRound = round(rounds.size() - 1);
        if (finalRound.size() != 1 || !finalRound.get(0).isPlayed()) {
            return null;
        }
        Match finalMatch = finalRound.get(0);
        String winnerId = finalMatch.score().home() > finalMatch.score().away()
                ? finalMatch.homeTeamId()
                : finalMatch.awayTeamId();
        return teams.get(winnerId);
    }

    private void validateKnockoutTeamCount() {
        int teamCount = teams.size();
        if (teamCount < 2) {
            throw new IllegalStateException("Mindestens 2 Teams erforderlich.");
        }
        if (!isPowerOfTwo(teamCount)) {
            throw new IllegalStateException("Im Knockout muss die Teamanzahl eine Zweierpotenz sein (2, 4, 8, 16, ...).");
        }
    }

    private boolean isPowerOfTwo(int value) {
        return value > 0 && (value & (value - 1)) == 0;
    }

    private void ensureFormat(CompetitionFormat expectedFormat) {
        if (format != expectedFormat) {
            throw new IllegalStateException("Dieser Spielmodus unterstuetzt die Aktion nicht.");
        }
    }

    private List<Match> createKnockoutRound(List<Team> participants) {
        List<Match> created = new ArrayList<>();
        List<String> roundMatchIds = new ArrayList<>();

        int left = 0;
        int right = participants.size() - 1;
        while (left < right) {
            Team home = participants.get(left++);
            Team away = participants.get(right--);
            String matchId = "M" + nextMatchNumber++;
            Match match = new Match(matchId, home.id(), away.id());
            matches.put(matchId, match);
            created.add(match);
            roundMatchIds.add(matchId);
        }

        if (!roundMatchIds.isEmpty()) {
            rounds.add(roundMatchIds);
        }
        return created;
    }

    private void createNextKnockoutRoundIfReady() {
        if (rounds.isEmpty()) {
            return;
        }

        List<Match> currentRound = round(rounds.size() - 1);
        if (currentRound.isEmpty() || currentRound.stream().anyMatch(match -> !match.isPlayed())) {
            return;
        }
        if (currentRound.size() == 1) {
            return;
        }

        List<Team> winners = new ArrayList<>();
        for (Match match : currentRound) {
            String winnerId = match.score().home() > match.score().away()
                    ? match.homeTeamId()
                    : match.awayTeamId();
            winners.add(teams.get(winnerId));
        }

        createKnockoutRound(winners);
    }
}
