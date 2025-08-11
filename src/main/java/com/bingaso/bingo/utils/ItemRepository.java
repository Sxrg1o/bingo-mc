package com.bingaso.bingo.utils;

import java.util.ArrayList;
import java.util.List;

public class ItemRepository {

    private final List<ItemData> allItems = new ArrayList<>();

    public static class ItemData {

        public String material;
        public int score;
    }

    public ItemRepository() {
        loadItems();
    }

    private void loadItems() {
        // TODO: Reading JSON
    }

    public List<ItemData> getAllItems() {
        return allItems;
    }
}
