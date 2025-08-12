package com.bingaso.bingo.gui;

import com.bingaso.bingo.game.MatchSettings;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

/**
 * GUI for configuring Bingo game settings.
 * Provides a user interface for operators to modify game settings such as
 * game mode, team mode, difficulty level, and game duration.
 * Uses the singleton pattern for global access.
 */
public class ConfigGui extends AbstractGui {

    /** Singleton instance of the ConfigGui */
    public static final ConfigGui INSTANCE = new ConfigGui();

    /**
     * Private constructor to enforce singleton pattern.
     */
    private ConfigGui() {}

    /**
     * Gets the singleton instance of ConfigGui.
     *
     * @return The singleton ConfigGui instance
     */
    public static ConfigGui getInstance() {
        return INSTANCE;
    }

    /** Context necessary to open this inventory */
    public static class ConfigGuiContext extends AbstractGuiContext {
        public MatchSettings matchSettings;

        public ConfigGuiContext(MatchSettings matchSettings) {
            this.matchSettings = matchSettings;
        }
    }

    /**
     * Creates and returns the config GUI inventory.
     * Creates an inventory with configuration options for game mode, team mode,
     * difficulty level, and game duration.
     * 
     * @param context The context for styling
     * @return The configured config GUI inventory
     * @throws IllegalArgumentException if the context is invalid
     */
    @Override
    public Inventory getInventory(AbstractGuiContext context) {
        if(context instanceof ConfigGuiContext) {
            return getInventory((ConfigGuiContext) context);
        } else {
            throw new IllegalArgumentException("Invalid context");
        }
    }

    /**
     * Creates and returns the config GUI inventory.
     * Creates an inventory with configuration options for game mode, team mode,
     * difficulty level, and game duration.
     * 
     * @param context The context for styling
     * @return The configured config GUI inventory
     */
    public Inventory getInventory(ConfigGuiContext context) {
        MatchSettings matchSettings = context.matchSettings;
        Inventory inventory = Bukkit.createInventory(
            null,
            27,
            Component.text("Bingo Configuration")
        );

        inventory.setItem(10, GuiItemFactory.createGameModeGuiItem(matchSettings.getGameMode()));
        inventory.setItem(12, GuiItemFactory.createTeamModeGuiItem(matchSettings.getTeamMode()));
        inventory.setItem(14, GuiItemFactory.createDifficultyGuiItem(matchSettings.getDifficultyLevel()));
        inventory.setItem(16, GuiItemFactory.createDurationGuiItem(matchSettings.getGameDuration()));

        return inventory;
    }
}
