package de.leaguemaster.cli;

import de.leaguemaster.cli.modes.GameMode;
import de.leaguemaster.cli.modes.LeagueMode;

import java.util.Scanner;

public class GameModeFactory {
    public static GameMode create(GameModeType type, Scanner scanner /*, ggf. Services */) {
        switch (type) {
            case LEAGUE:
                return new LeagueMode(scanner /* , services... */);
            case KNOCKOUT:
//return new KnockoutMode(scanner /* , services... */);
            case GROUP_STAGE:
  //              return new GroupStageMode(scanner /* , services... */);
            default:
                throw new IllegalArgumentException("Unbekannter Modus: " + type);
        }
    }
}