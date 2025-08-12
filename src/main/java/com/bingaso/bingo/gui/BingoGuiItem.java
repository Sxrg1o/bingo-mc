package com.bingaso.bingo.gui;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import com.bingaso.bingo.BingoPlugin;

/**
 * Extended ItemStack class for GUI items with custom persistent data storage.
 * Provides utilities for storing and retrieving custom string data in items
 * using Bukkit's persistent data container system.
 */
public class BingoGuiItem extends ItemStack{
    
    /**
     * Creates a new GuiItem with the specified material and custom identifier.
     * 
     * @param material the material type for this GUI item
     * @param customId the unique identifier for this GUI item type
     */
    public BingoGuiItem(Material material, String customId) {
        super(material);
        setCustomString("custom_id", customId);
    }

    /**
     * Stores a custom key-value pair in the item's persistent data container.
     *
     * @param key The key to store (must be unique per plugin)
     * @param value The value to store
     * @throws IllegalArgumentException if itemStack is null or has no metadata
     */
    public void setCustomString(String key, String value) {
        ItemMeta itemMeta = this.getItemMeta();
        if (itemMeta == null) {
            throw new IllegalArgumentException("ItemStack must have metadata to set custom data");
        }
        NamespacedKey namespacedKey = new NamespacedKey(BingoPlugin.getInstance(), key);
        itemMeta.getPersistentDataContainer().set(namespacedKey, PersistentDataType.STRING, value);
        this.setItemMeta(itemMeta);
    }

    /**
     * Checks if an ItemStack is a GuiItem with the specified custom identifier.
     * 
     * @param itemStack the ItemStack to check
     * @param customId the custom identifier to match against
     * @return true if the ItemStack is a GuiItem with the matching customId, false otherwise
     */
    public static boolean isGuiItem(ItemStack itemStack, String customId) {
        String value = getCustomString(itemStack, "custom_id");
        return value != null && value.equals(customId);
    }

    /**
     * Checks if an ItemStack is a GuiItem.
     * 
     * @param itemStack the ItemStack to check
     * @return true if the ItemStack is a GuiItem false otherwise
     */
    public static boolean isGuiItem(ItemStack itemStack) {
        return getCustomString(itemStack, "custom_id") != null;
    }

    /**
     * Retrieves a custom string value from the item's persistent data container.
     *
     * @param itemStack The ItemStack to query
     * @param key The key to retrieve
     * @return The stored value, or null if not present
     */
    public static String getCustomString(ItemStack itemStack, String key) {
        if (itemStack == null) {
            return null;
        }
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) {
            return null;
        }
        NamespacedKey namespacedKey = new NamespacedKey(BingoPlugin.getInstance(), key);
        return itemMeta.getPersistentDataContainer().get(namespacedKey, PersistentDataType.STRING);
    }
}
