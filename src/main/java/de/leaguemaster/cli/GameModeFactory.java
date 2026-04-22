package de.leaguemaster.cli;

import de.leaguemaster.application.usecase.AddTeamService;
import de.leaguemaster.application.usecase.CreateLeagueService;
import de.leaguemaster.application.usecase.LeagueQueryService;
import de.leaguemaster.application.usecase.RecordMatchResultService;
import de.leaguemaster.application.usecase.ScheduleMatchesService;
import de.leaguemaster.application.usecase.ShowTableService;
import de.leaguemaster.cli.modes.GameMode;
import de.leaguemaster.cli.modes.KnockoutMode;
import de.leaguemaster.cli.modes.LeagueMode;

public class GameModeFactory {
    private final CreateLeagueService createLeagueService;
    private final AddTeamService addTeamService;
    private final ScheduleMatchesService scheduleMatchesService;
    private final RecordMatchResultService recordMatchResultService;
    private final ShowTableService showTableService;
    private final LeagueQueryService leagueQueryService;

    public GameModeFactory(CreateLeagueService createLeagueService,
                           AddTeamService addTeamService,
                           ScheduleMatchesService scheduleMatchesService,
                           RecordMatchResultService recordMatchResultService,
                           ShowTableService showTableService,
                           LeagueQueryService leagueQueryService) {
        this.createLeagueService = createLeagueService;
        this.addTeamService = addTeamService;
        this.scheduleMatchesService = scheduleMatchesService;
        this.recordMatchResultService = recordMatchResultService;
        this.showTableService = showTableService;
        this.leagueQueryService = leagueQueryService;
    }

    public boolean isSupported(GameModeType type) {
        return type == GameModeType.LEAGUE || type == GameModeType.KNOCKOUT;
    }

    public GameMode create(GameModeType type /*, ggf. Services */) {
        switch (type) {
            case LEAGUE:
                return new LeagueMode(
                        createLeagueService,
                        addTeamService,
                        scheduleMatchesService,
                        recordMatchResultService,
                        showTableService,
                        leagueQueryService
                );
            case KNOCKOUT:
                return new KnockoutMode(
                        createLeagueService,
                        addTeamService,
                        scheduleMatchesService,
                        recordMatchResultService,
                        leagueQueryService
                );
            default:
                throw new IllegalArgumentException("Unbekannter Modus: " + type);
        }
    }
}
