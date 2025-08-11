package com.bingaso.bingo.model;

/**
 * Represents the difficulty levels available for Bingo games.
 * Each level affects the weighted selection of items based on their difficulty scores,
 * making the game easier or harder to complete.
 */
public enum DifficultyLevel {
    /**
     * Easiest difficulty level.
     * Predominantly includes simple items that are quick to obtain,
     * with a high weight for score-1 and score-2 items.
     */
    EASY,

    /**
     * Balanced difficulty level.
     * Includes a mix of simple and moderately challenging items,
     * with highest weight for score-2 and score-3 items.
     */
    MEDIUM,

    /**
     * Challenging difficulty level.
     * Favors harder-to-obtain items with some easier ones mixed in,
     * with highest weight for score-3 and score-4 items.
     */
    HARD,

    /**
     * Most difficult level.
     * Primarily includes challenging and rare items,
     * with highest weight for score-3, score-4, and score-5 items.
     */
    EXTREME,
}
