package com.bingaso.bingo.game;

import com.bingaso.bingo.card.CardGenerator.DifficultyLevel;
import com.bingaso.bingo.utils.ItemRepository;

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
    /** The items that will be used in the game */
    private ItemRepository itemRepository = new ItemRepository();

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
        int gameDuration,
        ItemRepository itemRepository
    ) {
        this.gameMode = gameMode;
        this.teamMode = teamMode;
        this.gameDuration = gameDuration;
        this.difficultyLevel = difficultyLevel;
        this.itemRepository = itemRepository;
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

    public ItemRepository getItemRepository() {
        return itemRepository;
    }

    public void setItemRepository(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    /**
     * Represents the team assignment modes available for Bingo games.
     * Determines how players are organized into teams before a match starts.
     */
    public static enum TeamMode {
        /**
         * Automatically assigns players to teams.
         * The system will distribute players evenly across teams.
         */
        RANDOM,

        /**
         * Players choose their teams manually.
         * Team assignments are controlled by player commands or GUI interactions.
         */
        MANUAL,
    }

    /**
     * Represents the possible states of a Bingo game.
     * The game transitions through these states during its lifecycle.
     */
    public static enum GameState {
        /**
         * Initial state where players can join teams and settings can be configured.
         * No active gameplay occurs in this state.
         */
        LOBBY,

        /**
         * Active gameplay state where players are finding items for their Bingo cards.
         * The game remains in this state until win conditions are met or time expires.
         */
        IN_PROGRESS,

        /**
         * Transitional state after a game has ended but before returning to the lobby.
         * Used for winner announcements and cleanup operations.
         */
        FINISHING,
    }
    
    /**
     * Represents the different modes to complete a Card.
     * Each mode has unique win conditions and gameplay mechanics.
     */
    public static enum GameMode {
        /**
         * Traditional Bingo rules.
         * Players win by completing any row, column, or diagonal on the card.
         */
        STANDARD,

        /**
         * Complete the entire card.
         * Players must find all 25 items on their card to win.
         */
        BLACKOUT,

        /**
         * Time-limited matches.
         * When time expires, the team with the most items found wins.
         */
        TIMED,

        /**
         * First-come-first-served mode.
         * Once an item is found by any team, it's locked and cannot be claimed by others.
         * Teams need to find a certain number of items based on the number of teams.
         */
        LOCKED,
    }

}
