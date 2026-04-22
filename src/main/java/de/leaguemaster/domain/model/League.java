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

    public boolean hasMatches() {
        return !matches.isEmpty();
    }

    public List<Match> scheduleRoundRobin() {
        ensureFormat(CompetitionFormat.LEAGUE);
        List<Team> participants = new ArrayList<>(teams.values());
        if (participants.size() < 2) {
            return Collections.emptyList();
        }

        List<Team> rotation = new ArrayList<>(participants);
        if (rotation.size() % 2 != 0) {
            rotation.add(null);
        }

        int teamCount = rotation.size();
        int roundsCount = teamCount - 1;
        List<Match> createdMatches = new ArrayList<>();

        for (int roundIndex = 0; roundIndex < roundsCount; roundIndex++) {
            List<String> roundMatchIds = new ArrayList<>();
            for (int pairIndex = 0; pairIndex < teamCount / 2; pairIndex++) {
                Team home = rotation.get(pairIndex);
                Team away = rotation.get(teamCount - 1 - pairIndex);
                if (home == null || away == null) {
                    continue;
                }
                Match match = createMatch(home.id(), away.id());
                createdMatches.add(match);
                roundMatchIds.add(match.id());
            }
            rounds.add(roundMatchIds);

            Team last = rotation.remove(rotation.size() - 1);
            rotation.add(1, last);
        }

        return createdMatches;
    }

    public List<Match> scheduleKnockout() {
        ensureFormat(CompetitionFormat.KNOCKOUT);
        validateKnockoutTeamCount();
        return createKnockoutBracket(new ArrayList<>(teams.values()));
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
            advanceWinnerToNextKnockoutRound(matchId);
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
        for (int roundIndex = 0; roundIndex < rounds.size(); roundIndex++) {
            for (String matchId : rounds.get(roundIndex)) {
                Match match = matches.get(matchId);
                if (match != null && match.hasAssignedTeams() && !match.isPlayed()) {
                    return roundIndex;
                }
            }
        }
        return -1;
    }

    public Team champion() {
        if (format != CompetitionFormat.KNOCKOUT || rounds.isEmpty()) {
            return null;
        }
        List<Match> finalRound = round(rounds.size() - 1);
        if (finalRound.size() != 1 || !finalRound.get(0).isPlayed()) {
            return null;
        }
        Match finalMatch = finalRound.get(0);
        String winnerId = winnerIdOf(finalMatch);
        return teams.get(winnerId);
    }

    private String normalize(String name) {
        return name == null ? "" : name.trim().toLowerCase();
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

    private List<Match> createKnockoutBracket(List<Team> participants) {
        List<Match> firstRound = createInitialKnockoutRound(participants);
        int matchesInNextRound = firstRound.size() / 2;
        while (matchesInNextRound >= 1) {
            createEmptyKnockoutRound(matchesInNextRound);
            matchesInNextRound /= 2;
        }
        return firstRound;
    }

    private List<Match> createInitialKnockoutRound(List<Team> participants) {
        List<Match> createdMatches = new ArrayList<>();
        List<String> roundMatchIds = new ArrayList<>();

        int left = 0;
        int right = participants.size() - 1;
        while (left < right) {
            Team home = participants.get(left++);
            Team away = participants.get(right--);
            Match match = createMatch(home.id(), away.id());
            createdMatches.add(match);
            roundMatchIds.add(match.id());
        }

        rounds.add(roundMatchIds);
        return createdMatches;
    }

    private void createEmptyKnockoutRound(int numberOfMatches) {
        List<String> roundMatchIds = new ArrayList<>();
        for (int index = 0; index < numberOfMatches; index++) {
            Match match = createMatch(null, null);
            roundMatchIds.add(match.id());
        }
        rounds.add(roundMatchIds);
    }

    private Match createMatch(String homeTeamId, String awayTeamId) {
        String matchId = "M" + nextMatchNumber++;
        Match match = new Match(matchId, homeTeamId, awayTeamId);
        matches.put(matchId, match);
        return match;
    }

    private void advanceWinnerToNextKnockoutRound(String playedMatchId) {
        int roundIndex = roundIndexOf(playedMatchId);
        if (roundIndex < 0 || roundIndex >= rounds.size() - 1) {
            return;
        }

        int matchIndex = rounds.get(roundIndex).indexOf(playedMatchId);
        if (matchIndex < 0) {
            return;
        }

        Match playedMatch = matches.get(playedMatchId);
        String winnerId = winnerIdOf(playedMatch);

        String nextMatchId = rounds.get(roundIndex + 1).get(matchIndex / 2);
        Match nextMatch = matches.get(nextMatchId);
        if (matchIndex % 2 == 0) {
            nextMatch.assignHomeTeam(winnerId);
            return;
        }
        nextMatch.assignAwayTeam(winnerId);
    }

    private String winnerIdOf(Match match) {
        return match.score().home() > match.score().away()
                ? match.homeTeamId()
                : match.awayTeamId();
    }

    private int roundIndexOf(String matchId) {
        for (int roundIndex = 0; roundIndex < rounds.size(); roundIndex++) {
            if (rounds.get(roundIndex).contains(matchId)) {
                return roundIndex;
            }
        }
        return -1;
    }
}
