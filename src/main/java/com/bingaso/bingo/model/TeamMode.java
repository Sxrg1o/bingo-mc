package com.bingaso.bingo.model;

/**
 * Represents the team assignment modes available for Bingo games.
 * Determines how players are organized into teams before a match starts.
 */
public enum TeamMode {
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
