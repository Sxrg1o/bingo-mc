package com.bingaso.bingo.card.quest;

import java.util.Map;
import java.util.HashMap;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

/**
 * Represents an item quest on a Bingo card that players need to find during a game.
 * This includes both basic materials and enchanted items.
 * 
 * This class is serializable to allow saving and loading of bingo cards.
 * Note: Serialization compatibility may be affected by Bukkit Material enum changes
 * between different server versions.
 * 
 * @since 1.0
 */
public class BingoQuestItem extends BingoQuest {

    private static final long serialVersionUID = 2L;

    private final Material material;
    private final Map<Enchantment, Integer> enchantments;
    private final boolean isEnchanted;

    /**
     * Creates a new QuestItem for the specified material.
     *
     * @param material The Minecraft material that this item represents
     * @since 1.0
     */
    public BingoQuestItem(Material material) {
        this.material = material;
        this.enchantments = new HashMap<>();
        this.isEnchanted = false;
    }

    /**
     * Creates a new QuestItem for an enchanted item.
     *
     * @param material The base material for the enchanted item
     * @param enchantments Map of enchantments and their levels
     * @since 1.0
     */
    public BingoQuestItem(Material material, Map<Enchantment, Integer> enchantments) {
        this.material = material;
        this.enchantments = new HashMap<>(enchantments);
        this.isEnchanted = !enchantments.isEmpty();
    }

    /**
     * Gets the material that this item represents.
     *
     * @return The Minecraft material of this item
     * @since 1.0
     */
    public Material getMaterial() {
        return material;
    }

    /**
     * Gets the enchantments for this item.
     *
     * @return Map of enchantments and their levels, empty if no enchantments
     * @since 1.0
     */
    public Map<Enchantment, Integer> getEnchantments() {
        return new HashMap<>(enchantments);
    }

    /**
     * Checks if this item is an enchanted item.
     *
     * @return true if this item has enchantments, false otherwise
     * @since 1.0
     */
    public boolean isEnchanted() {
        return isEnchanted;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        BingoQuestItem questItem = (BingoQuestItem) obj;
        return isEnchanted == questItem.isEnchanted &&
               material == questItem.material &&
               enchantments.equals(questItem.enchantments);
    }

    @Override
    public int hashCode() {
        int result = material != null ? material.hashCode() : 0;
        result = 31 * result + enchantments.hashCode();
        result = 31 * result + (isEnchanted ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        if (isEnchanted) {
            return "QuestItem{material=" + material + ", enchantments=" + enchantments + "}";
        } else {
            return "QuestItem{material=" + material + "}";
        }
    }
}
