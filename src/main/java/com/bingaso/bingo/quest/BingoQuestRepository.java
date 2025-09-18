package com.bingaso.bingo.quest;

import com.bingaso.bingo.BingoPlugin;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.*;
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
public class BingoQuestRepository {

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
    public BingoQuestRepository() {
        this.allItems = loadItems();
    }

    /**
     * Loads item data from the plugin data folder or falls back to the scores.json resource file.
     * First attempts to load from data/scores.json, then falls back to the resource file.
     *
     * @return A list of loaded items, or an empty list if no data can be found
     */
    private List<ItemData> loadItems() {
        File dataFolder = BingoPlugin.getInstance().getDataFolder();
        File scoresFile = new File(dataFolder, "scores.json");
        
        // Try to load from plugin data folder first
        if (scoresFile.exists()) {
            try (FileReader reader = new FileReader(scoresFile, StandardCharsets.UTF_8)) {
                Gson gson = new Gson();
                Type listType = new TypeToken<ArrayList<ItemData>>() {}.getType();
                List<ItemData> loadedItems = gson.fromJson(reader, listType);
                
                BingoPlugin.getInstance()
                    .getLogger()
                    .info("Loaded " + loadedItems.size() + " items from plugin data folder successfully.");
                
                return loadedItems;
            } catch (IOException e) {
                BingoPlugin.getInstance()
                    .getLogger()
                    .warning("Failed to load scores.json from data folder: " + e.getMessage());
            }
        }
        
        // Fall back to resource file
        InputStream stream = BingoPlugin.getInstance().getResource("scores.json");

        if (stream == null) {
            BingoPlugin.getInstance()
                .getLogger()
                .severe("scores.json not found in resources.");
            return Collections.emptyList();
        }

        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<ItemData>>() {}.getType();
        InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);
        List<ItemData> loadedItems = gson.fromJson(reader, listType);

        BingoPlugin.getInstance()
            .getLogger()
            .info("Loaded " + loadedItems.size() + " items from resources successfully.");

        return loadedItems;
    }

    /**
     * Saves the current items to a JSON file in the plugin's data folder.
     * Creates the data folder if it doesn't exist.
     *
     * @param items The list of items to save
     * @return true if the save was successful, false otherwise
     */
    public boolean saveItems(List<ItemData> items) {
        File dataFolder = BingoPlugin.getInstance().getDataFolder();
        
        // Create data folder if it doesn't exist
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        
        File scoresFile = new File(dataFolder, "scores.json");
        
        try (FileWriter writer = new FileWriter(scoresFile, StandardCharsets.UTF_8)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(items, writer);
            
            BingoPlugin.getInstance()
                .getLogger()
                .info("Saved " + items.size() + " items to scores.json successfully.");
            
            return true;
        } catch (IOException e) {
            BingoPlugin.getInstance()
                .getLogger()
                .severe("Failed to save scores.json: " + e.getMessage());
            return false;
        }
    }

    /**
     * Saves the current items from the repository to the plugin's data folder.
     *
     * @return true if the save was successful, false otherwise
     */
    public boolean saveItems() {
        return saveItems(this.allItems);
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
