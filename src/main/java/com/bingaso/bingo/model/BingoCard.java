package com.bingaso.bingo.model;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;

/**
 * Represents a Bingo card used in the game.
 * A Bingo card contains a collection of BingoItems that players need to find.
 * In a standard game, a card consists of 25 items arranged in a 5x5 grid.
 */
public class BingoCard {

    private final List<BingoItem> items;

    /**
     * Creates an empty Bingo card with no items.
     */
    public BingoCard() {
        items = new ArrayList<>();
    }

    /**
     * Creates a Bingo card with the specified list of items.
     *
     * @param items The list of BingoItems to populate this card
     */
    public BingoCard(List<BingoItem> items) {
        this.items = items;
    }

    /**
     * Gets all items on this Bingo card.
     *
     * @return The list of BingoItems on this card
     */
    public List<BingoItem> getItems() {
        return items;
    }

    /**
     * Finds a specific BingoItem on this card by its material type.
     *
     * @param material The material type to search for
     * @return The matching BingoItem if found, or null if not on this card
     */
    public BingoItem getItem(Material material) {
        for (BingoItem item : items) {
            if (item.getMaterial() == material) {
                return item;
            }
        }
        return null;
    }
}
