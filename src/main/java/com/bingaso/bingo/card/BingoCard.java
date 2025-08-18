package com.bingaso.bingo.card;

import java.util.Arrays;
import java.util.List;
import org.bukkit.Material;

import com.bingaso.bingo.quest.BingoQuest;
import com.bingaso.bingo.quest.BingoQuestItem;
import com.bingaso.bingo.team.BingoTeam;

/**
 * Represents a Bingo card used in the game.
 * A Bingo card contains a collection of BingoQuests that players need to complete.
 * In a standard game, a card consists of 25 quests arranged in a 5x5 grid.
 * 
 * @since 1.0
 */
public class BingoCard {

    private final BingoQuest[] items;
    private int itemCount = 0;
    private final int size;

    /**
     * Creates an empty Bingo card with no items.
     *
     * @param size The size of the card (number of items per row or column)
     * @since 1.0
     */
    protected BingoCard(int size) {
        this.size = size;
        this.items = new BingoQuest[size * size];
    }

    /**
     * Creates a Bingo card with default size (5x5).
     *
     * @since 1.0
     */
    protected BingoCard() {
        this(5);
    }

    /**
     * Creates a Bingo card from a list of quests.
     *
     * @param questList The list of BingoQuests to populate the card with
     * @since 1.0
     */
    protected BingoCard(List<BingoQuest> questList) {
        this.size = (int) Math.sqrt(questList.size());
        this.items = new BingoQuest[size * size];
        this.itemCount = Math.min(questList.size(), size * size);
        
        for (int i = 0; i < itemCount; i++) {
            this.items[i] = questList.get(i);
        }
    }

    /**
     * Securely adds a quest into the Bingo card.
     *
     * @param quest The BingoQuest to add
     * @return True if the quest was added successfully, false if the card is full
     * @since 1.0
     */
    protected boolean addItem(BingoQuest quest) {
        if (itemCount >= items.length) {
            return false;
        }
        
        // Check for duplicates
        for (int i = 0; i < itemCount; i++) {
            if (items[i].equals(quest)) {
                return false;
            }
        }
        
        items[itemCount++] = quest;
        return true;
    }

    /**
     * Gets all quests on this Bingo card.
     *
     * @return The list of BingoQuests on this card
     * @since 1.0
     */
    public List<BingoQuest> getItems() {
        return Arrays.asList(items);
    }

    /**
     * Finds a quest item by material type.
     *
     * @param material The material to search for
     * @return The QuestItem with the specified material, or null if not found
     * @since 1.0
     */
    public BingoQuest getItem(Material material) {
        for (BingoQuest quest : items) {
            if (quest instanceof BingoQuestItem) {
                BingoQuestItem questItem = (BingoQuestItem) quest;
                if (questItem.getMaterial() == material) {
                    return questItem;
                }
            }
        }
        return null;
    }

    /**
     * Checks if any row on this Bingo card is completed by the specified team.
     *
     * @param bingoTeam The team to check completion for
     * @return True if any row is completed by the team, false otherwise
     * @since 1.0
     */
    public boolean isAnyRowCompletedByTeam(BingoTeam bingoTeam) {
        for (int row = 0; row < size; row++) {
            if (isRowCompletedByTeam(row, bingoTeam)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if a specific row on this Bingo card is completed by the specified team.
     *
     * @param row The row index to check (0-based)
     * @param bingoTeam The team to check completion for
     * @return True if the specified row is completed by the team, false otherwise
     * @since 1.0
     */
    public boolean isRowCompletedByTeam(int row, BingoTeam bingoTeam) {
        for (int col = 0; col < size; col++) {
            if (!bingoTeam.hasCompletedQuest(items[row * size + col])) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if any column on this Bingo card is completed by the specified team.
     *
     * @param bingoTeam The team to check completion for
     * @return True if any column is completed by the team, false otherwise
     * @since 1.0
     */
    public boolean isAnyColumnCompletedByTeam(BingoTeam bingoTeam) {
        for (int col = 0; col < size; col++) {
            if (isColumnCompletedByTeam(col, bingoTeam)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if a specific column on this Bingo card is completed by the specified team.
     *
     * @param col The column index to check (0-based)
     * @param bingoTeam The team to check completion for
     * @return True if the specified column is completed by the team, false otherwise
     * @since 1.0
     */
    public boolean isColumnCompletedByTeam(int col, BingoTeam bingoTeam) {
        for (int row = 0; row < size; row++) {
            if (!bingoTeam.hasCompletedQuest(items[row * size + col])) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Checks if any diagonal on this Bingo card is completed by the specified team.
     * This includes both the main diagonal (top-left to bottom-right) and 
     * the anti-diagonal (top-right to bottom-left).
     *
     * @param bingoTeam The team to check completion for
     * @return True if any diagonal is completed by the team, false otherwise
     * @since 1.0
     */
    public boolean isAnyDiagonalCompletedByTeam(BingoTeam bingoTeam) {
        return isMainDiagonalCompletedByTeam(bingoTeam) ||
            isAntiDiagonalCompletedByTeam(bingoTeam);
    }

    /**
     * Checks if the main diagonal (top-left to bottom-right) on this Bingo card 
     * is completed by the specified team.
     *
     * @param bingoTeam The team to check completion for
     * @return True if the main diagonal is completed by the team, false otherwise
     * @since 1.0
     */
    public boolean isMainDiagonalCompletedByTeam(BingoTeam bingoTeam) {
        for (int i = 0; i < size; i++) {
            if (!bingoTeam.hasCompletedQuest(items[i * size + i])) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if the anti-diagonal (top-right to bottom-left) on this Bingo card 
     * is completed by the specified team.
     *
     * @param bingoTeam The team to check completion for
     * @return True if the anti-diagonal is completed by the team, false otherwise
     * @since 1.0
     */
    public boolean isAntiDiagonalCompletedByTeam(BingoTeam bingoTeam) {
        for (int i = 0; i < size; i++) {
            if (!bingoTeam.hasCompletedQuest(items[i * size + (size - 1 - i)])) {
                return false;
            }
        }
        return true;
    }
}
