package com.bingaso.bingo.model;

import java.util.List;

import org.bukkit.Material;

public class BingoCard {

    private final List<BingoItem> items;

    public BingoCard() {
        BingoCard newCard = generateNewCard();
        this.items = newCard.getItems();
    }

    public BingoCard(List<BingoItem> items) {
        this.items = items;
    }

    public List<BingoItem> getItems() {
        return items;
    }

    public BingoCard generateNewCard() {
        // TODO: Generate card
        return null;
    }

    public BingoItem getItem(Material material) {
        for (BingoItem item : items) {
            if (item.getMaterial() == material) {
                return item;
            }
        }
        return null;
    }
}