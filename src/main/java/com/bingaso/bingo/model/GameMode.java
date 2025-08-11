package com.bingaso.bingo.model;

/**
 * Represents the different game modes available in Bingo.
 * Each mode has unique win conditions and gameplay mechanics.
 */
public enum GameMode {
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
