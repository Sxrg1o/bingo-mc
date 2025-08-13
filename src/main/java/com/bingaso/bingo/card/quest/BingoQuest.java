package com.bingaso.bingo.card.quest;

import java.io.Serializable;
import java.time.Instant;

import com.bingaso.bingo.team.BingoTeam;

/**
 * Interface representing a quest on a Bingo card that players need to complete during a game.
 * Each BingoQuest implementation can track which teams have completed it.
 * 
 * This interface supports various quest types including items, potions, and advancements.
 * 
 * Implementations should be serializable to allow saving and loading of bingo cards.
 * 
 * @since 1.0
 */
public abstract class BingoQuest implements Serializable {

    /**
     * Checks if this quest has been completed by the specified team.
     *
     * @param bingoTeam The team to check
     * @return true if the team has completed this quest, false otherwise
     * @since 1.0
     */
    public boolean isCompletedBy(BingoTeam bingoTeam) {
        return bingoTeam.hasCompletedItem(this);
    }

    /**
     * Returns the completion instant for the specified team.
     *
     * @param bingoTeam The team to get the completion instant for
     * @return The completion instant for the team, or null if not found
     * @since 1.0
     */
    public Instant getCompletionInstant(BingoTeam bingoTeam) {
        return bingoTeam.getCompletionInstant(this);
    }
}
