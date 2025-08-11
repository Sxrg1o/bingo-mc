package com.bingaso.bingo.model;

import org.bukkit.Material;

public class BingoCard {

    private final BingoItem[][] items;

    public BingoCard() {
        BingoCard newCard = generateNewCard();
        this.items = newCard.getItems();
    }

    public BingoCard(BingoItem[][] items) {
        this.items = items;
    }

    public BingoItem[][] getItems() {
        return items;
    }

    public BingoCard generateNewCard() {
        // TODO: Generate card
        return null;
    }

    public BingoItem getItem(Material material) {
        for (BingoItem[] row : items) {
            for (BingoItem item : row) {
                if (item.getMaterial() == material) {
                    return item;
                }
            }
        }
        return null;
    }
}
