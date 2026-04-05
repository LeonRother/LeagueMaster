package de.leaguemaster.domain.model;

public class Team {
    private final String id;
    private final String name;

    public Team(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String id() {
        return id;
    }

    public String name() {
        return name;
    }
}
