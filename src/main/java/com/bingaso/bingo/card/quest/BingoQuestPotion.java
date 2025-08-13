package com.bingaso.bingo.card.quest;

import org.bukkit.Material;
import org.bukkit.potion.PotionEffectType;

/**
 * Represents a potion quest on a Bingo card that players need to create during a game.
 * 
 * This class is serializable to allow saving and loading of bingo cards.
 * Note: Serialization compatibility may be affected by Bukkit Material enum changes
 * between different server versions.
 * 
 * @since 1.0
 */
public class BingoQuestPotion extends BingoQuest {

    private static final long serialVersionUID = 2L;

    private final Material material;
    private final PotionEffectType potionEffect;

    /**
     * Creates a new QuestPotion for a potion.
     *
     * @param material The potion material (should be a potion type)
     * @param potionEffect The potion effect type
     * @since 1.0
     */
    public BingoQuestPotion(Material material, PotionEffectType potionEffect) {
        this.material = material;
        this.potionEffect = potionEffect;
    }

    /**
     * Gets the material that this potion represents.
     *
     * @return The Minecraft material of this potion
     * @since 1.0
     */
    public Material getMaterial() {
        return material;
    }

    /**
     * Gets the potion effect type for this potion.
     *
     * @return The potion effect type
     * @since 1.0
     */
    public PotionEffectType getPotionEffect() {
        return potionEffect;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        BingoQuestPotion questPotion = (BingoQuestPotion) obj;
        return material == questPotion.material &&
               potionEffect == questPotion.potionEffect;
    }

    @Override
    public int hashCode() {
        int result = material != null ? material.hashCode() : 0;
        result = 31 * result + (potionEffect != null ? potionEffect.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "QuestPotion{material=" + material + ", potionEffect=" + potionEffect + "}";
    }
}
