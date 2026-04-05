package de.leaguemaster;

import de.leaguemaster.cli.CLI;
import de.leaguemaster.cli.CommandParser;
import de.leaguemaster.cli.GameModeFactory;
import de.leaguemaster.application.usecase.AddTeamService;
import de.leaguemaster.application.usecase.CreateLeagueService;
import de.leaguemaster.application.usecase.LeagueQueryService;
import de.leaguemaster.application.usecase.RecordMatchResultService;
import de.leaguemaster.application.usecase.ScheduleMatchesService;
import de.leaguemaster.application.usecase.ShowTableService;
import de.leaguemaster.infrastructure.storage.InMemoryLeagueRepository;

import java.util.Scanner;
public class Main {
    public static void main(String args[]) {
        Scanner sc = new Scanner(System.in);
        CommandParser cp = new CommandParser();
        InMemoryLeagueRepository repo = new InMemoryLeagueRepository();
        CreateLeagueService createLeagueService = new CreateLeagueService(repo);
        AddTeamService addTeamService = new AddTeamService(repo);
        ScheduleMatchesService scheduleMatchesService = new ScheduleMatchesService(repo);
        RecordMatchResultService recordMatchResultService = new RecordMatchResultService(repo);
        ShowTableService showTableService = new ShowTableService(repo);
        LeagueQueryService leagueQueryService = new LeagueQueryService(repo);
        GameModeFactory factory = new GameModeFactory(
                createLeagueService,
                addTeamService,
                scheduleMatchesService,
                recordMatchResultService,
                showTableService,
                leagueQueryService
        );
        CLI cli = new CLI(sc,cp, factory);
        cli.start();
    }
}
