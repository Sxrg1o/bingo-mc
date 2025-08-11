package com.bingaso.bingo.gui;

import com.bingaso.bingo.game.MatchSettings;
import com.bingaso.bingo.model.DifficultyLevel;
import com.bingaso.bingo.model.GameMode;
import com.bingaso.bingo.model.TeamMode;
import java.util.Arrays;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * GUI for configuring Bingo game settings.
 * Provides a user interface for operators to modify game settings such as
 * game mode, team mode, difficulty level, and game duration.
 * Uses the singleton pattern for global access.
 */
public class ConfigGui {

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

    /**
     * Opens the configuration GUI for a player with the current match settings.
     * Creates an inventory with configuration options for game mode, team mode,
     * difficulty level, and game duration.
     *
     * @param player The player to open the GUI for
     * @param settings The current match settings to display
     */
    public void openForPlayer(Player player, MatchSettings settings) {
        Inventory gui = Bukkit.createInventory(
            null,
            27,
            Component.text("Bingo Configuration")
        );

        gui.setItem(10, createGameModeItem(settings.getGameMode()));
        gui.setItem(12, createTeamModeItem(settings.getTeamMode()));
        gui.setItem(14, createDifficultyItem(settings.getDifficultyLevel()));
        gui.setItem(16, createDurationItem(settings.getGameDuration()));

        player.openInventory(gui);
    }

    /**
     * Creates a GUI item for selecting the game mode.
     * The item displays the current game mode and instructions for changing it.
     *
     * @param currentMode The currently selected game mode
     * @return A configured GuiItem for game mode selection
     */
    private GuiItem createGameModeItem(GameMode currentMode) {
        GuiItem item = new GuiItem(Material.ENCHANTED_BOOK, "config_gamemode");
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("Game Mode", NamedTextColor.AQUA));
        meta.lore(
            Arrays.asList(
                Component.text(
                    "Click to cycle to the next mode.",
                    NamedTextColor.GRAY
                ),
                Component.text("Current: ", NamedTextColor.GRAY).append(
                    Component.text(currentMode.name(), NamedTextColor.YELLOW)
                )
            )
        );
        item.setItemMeta(meta);
        return item;
    }

    /**
     * Creates a GUI item for selecting the team mode.
     * The item displays the current team mode and instructions for changing it.
     *
     * @param currentMode The currently selected team mode
     * @return A configured GuiItem for team mode selection
     */
    private GuiItem createTeamModeItem(TeamMode currentMode) {
        GuiItem item = new GuiItem(Material.PLAYER_HEAD, "config_teammode");
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("Team Mode", NamedTextColor.AQUA));
        meta.lore(
            Arrays.asList(
                Component.text(
                    "Click to cycle to the next mode.",
                    NamedTextColor.GRAY
                ),
                Component.text("Current: ", NamedTextColor.GRAY).append(
                    Component.text(currentMode.name(), NamedTextColor.YELLOW)
                )
            )
        );
        item.setItemMeta(meta);
        return item;
    }

    /**
     * Creates a GUI item for selecting the difficulty level.
     * The item displays the current difficulty level and instructions for changing it.
     *
     * @param currentLevel The currently selected difficulty level
     * @return A configured GuiItem for difficulty selection
     */
    private GuiItem createDifficultyItem(DifficultyLevel currentLevel) {
        GuiItem item = new GuiItem(Material.DIAMOND_SWORD, "config_difficulty");
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("Difficulty", NamedTextColor.AQUA));
        meta.lore(
            Arrays.asList(
                Component.text(
                    "Click to cycle to the next level.",
                    NamedTextColor.GRAY
                ),
                Component.text("Current: ", NamedTextColor.GRAY).append(
                    Component.text(currentLevel.name(), NamedTextColor.YELLOW)
                )
            )
        );
        item.setItemMeta(meta);
        return item;
    }

    /**
     * Creates a GUI item for adjusting the game duration.
     * The item displays the current duration in minutes and instructions for increasing
     * or decreasing it using left and right clicks.
     *
     * @param currentDuration The current game duration in minutes
     * @return A configured GuiItem for duration adjustment
     */
    private GuiItem createDurationItem(int currentDuration) {
        GuiItem item = new GuiItem(Material.CLOCK, "config_duration");
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("Game Duration", NamedTextColor.AQUA));
        meta.lore(
            Arrays.asList(
                Component.text(
                    "Left-click to add 5 minutes.",
                    NamedTextColor.GRAY
                ),
                Component.text(
                    "Right-click to remove 5 minutes.",
                    NamedTextColor.GRAY
                ),
                Component.text("Current: ", NamedTextColor.GRAY).append(
                    Component.text(
                        currentDuration + " minutes",
                        NamedTextColor.YELLOW
                    )
                )
            )
        );
        item.setItemMeta(meta);
        return item;
    }
}
