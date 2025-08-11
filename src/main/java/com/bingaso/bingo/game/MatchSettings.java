package com.bingaso.bingo.game;

import com.bingaso.bingo.model.DifficultyLevel;
import com.bingaso.bingo.model.GameMode;
import com.bingaso.bingo.model.TeamMode;

/**
 * Contains configuration settings for a Bingo match.
 * This class stores all customizable parameters that define how a match will be played,
 * including game mode, team formation, difficulty level, and time limits.
 */
public class MatchSettings {

    /** The game mode that determines win conditions */
    private GameMode gameMode = GameMode.STANDARD;
    /** The team assignment mode that determines how players are grouped */
    private TeamMode teamMode = TeamMode.MANUAL;
    /** The difficulty level that affects item selection */
    private DifficultyLevel difficultyLevel = DifficultyLevel.MEDIUM;
    /** The duration of timed matches in minutes */
    private int gameDuration = 25;

    /**
     * Creates a new MatchSettings instance with default values.
     * Defaults to STANDARD game mode, MANUAL team mode, MEDIUM difficulty, and 25-minute duration.
     */
    public MatchSettings() {}

    /**
     * Creates a new MatchSettings instance with the specified values.
     *
     * @param gameMode The game mode that determines win conditions
     * @param teamMode The team assignment mode for player grouping
     * @param difficultyLevel The difficulty level for item selection
     * @param gameDuration The duration of timed matches in minutes
     */
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

    /**
     * Gets the current game mode setting.
     *
     * @return The game mode that determines win conditions
     */
    public GameMode getGameMode() {
        return gameMode;
    }

    /**
     * Gets the current team mode setting.
     *
     * @return The team assignment mode for player grouping
     */
    public TeamMode getTeamMode() {
        return teamMode;
    }

    /**
     * Gets the current game duration setting.
     *
     * @return The duration of timed matches in minutes
     */
    public int getGameDuration() {
        return gameDuration;
    }

    /**
     * Gets the current difficulty level setting.
     *
     * @return The difficulty level for item selection
     */
    public DifficultyLevel getDifficultyLevel() {
        return difficultyLevel;
    }

    /**
     * Sets the game mode setting.
     *
     * @param gameMode The game mode that determines win conditions
     */
    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    /**
     * Sets the team mode setting.
     *
     * @param teamMode The team assignment mode for player grouping
     */
    public void setTeamMode(TeamMode teamMode) {
        this.teamMode = teamMode;
    }

    /**
     * Sets the difficulty level setting.
     *
     * @param difficultyLevel The difficulty level for item selection
     */
    public void setDifficultyLevel(DifficultyLevel difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    /**
     * Sets the game duration setting.
     *
     * @param gameDuration The duration of timed matches in minutes
     */
    public void setGameDuration(int gameDuration) {
        this.gameDuration = gameDuration;
    }
}
