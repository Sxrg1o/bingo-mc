package com.bingaso.bingo.game;

/**
 * Represents the possible states of a Bingo game.
 * The game transitions through these states during its lifecycle.
 */
public enum GameState {
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
