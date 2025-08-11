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

/**
 * Repository for accessing and managing Bingo item data.
 * This class is responsible for loading item definitions from a JSON resource file
 * and providing access to these items for card generation and other game functions.
 */
public class ItemRepository {

    private final List<ItemData> allItems;

    /**
     * Represents an item that can appear on a Bingo card.
     * Each item has a material name and a difficulty score.
     */
    public static class ItemData {

        /** The Minecraft material name of this item */
        public String name;
        /** The difficulty score of this item (1-5, with higher values being more difficult) */
        public int score;
    }

    /**
     * Creates a new ItemRepository instance.
     * Loads all available items from the scores.json resource file during initialization.
     */
    public ItemRepository() {
        this.allItems = loadItems();
    }

    /**
     * Loads item data from the scores.json resource file.
     * Parses the JSON file into a list of ItemData objects using Gson.
     *
     * @return A list of loaded items, or an empty list if the resource cannot be found
     */
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

    /**
     * Gets all available items loaded from the repository.
     *
     * @return A list of all available ItemData objects
     */
    public List<ItemData> getAllItems() {
        return allItems;
    }
}
