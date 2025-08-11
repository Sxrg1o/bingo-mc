package com.bingaso.bingo.utils;

import com.bingaso.bingo.BingoPlugin;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ItemRepository {

    private final List<ItemData> allItems;

    public static class ItemData {

        public String name;
        public int score;
    }

    public ItemRepository() {
        this.allItems = loadItems();
    }

    private List<ItemData> loadItems() {
        InputStream stream = BingoPlugin.getInstance().getResource(
            "scores.json"
        );

        if (stream == null) {
            BingoPlugin.getInstance()
                .getLogger()
                .severe("scores.json not found.");
            return Collections.emptyList();
        }

        Gson gson = new Gson();

        Type listType = new TypeToken<ArrayList<ItemData>>() {}.getType();

        InputStreamReader reader = new InputStreamReader(
            stream,
            StandardCharsets.UTF_8
        );

        List<ItemData> loadedItems = gson.fromJson(reader, listType);

        BingoPlugin.getInstance()
            .getLogger()
            .info(
                "Loaded " +
                loadedItems.size() +
                " items from scores.json successfully."
            );

        return loadedItems;
    }

    public List<ItemData> getAllItems() {
        return allItems;
    }
}
