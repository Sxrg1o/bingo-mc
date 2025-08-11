package com.bingaso.bingo.game;

import com.bingaso.bingo.model.DifficultyLevel;
import com.bingaso.bingo.model.GameMode;
import com.bingaso.bingo.model.TeamMode;

public class MatchSettings {

    private GameMode gameMode = GameMode.STANDARD;
    private TeamMode teamMode = TeamMode.RANDOM;
    private DifficultyLevel difficultyLevel = DifficultyLevel.MEDIUM;
    private int gameDuration = 25;

    public MatchSettings() {}

    public MatchSettings(
        GameMode gameMode,
        TeamMode teamMode,
        DifficultyLevel difficultyLevel,
        int gameDuration
    ) {
        this.gameMode = gameMode;
        this.teamMode = teamMode;
        this.gameDuration = gameDuration;
        this.difficultyLevel = difficultyLevel;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public TeamMode getTeamMode() {
        return teamMode;
    }

    public int getGameDuration() {
        return gameDuration;
    }

    public DifficultyLevel getDifficultyLevel() {
        return difficultyLevel;
    }
}
