package com.bingaso.bingo.model;

import org.bukkit.Material;

public class BingoCard {

    private final BingoItem[][] items;

    public BingoCard(BingoItem[][] items) {
        this.items = items;
    }

    public BingoItem[][] getItems() {
        return items;
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
