package com.bingaso.bingo.match;

import com.bingaso.bingo.BingoPlugin;
import com.bingaso.bingo.card.BingoCardGenerator.DifficultyLevel;
import com.bingaso.bingo.gui.BingoGuiItem;
import com.bingaso.bingo.match.BingoMatchSettings.GameMode;
import com.bingaso.bingo.match.BingoMatchSettings.TeamMode;
import com.bingaso.bingo.match.BingoMatchSettingsGui.ConfigGuiContext;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Listener that handles interactions with the Bingo configuration GUI.
 * Processes click events on configuration items to update game settings.
 */
public class BingoMatchSettingsGuiListener implements Listener {

    /**
     * Handles inventory click events in the configuration GUI.
     * Updates match settings based on which configuration option was clicked.
     * Cycles through available options for game mode, team mode, and difficulty level.
     * Increases or decreases game duration based on left or right clicks.
     *
     * @param event The inventory click event
     */
    @Deprecated
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals("Bingo Configuration")) return;

        event.setCancelled(true);

        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null) return;

        Player player = (Player) event.getWhoClicked();
        String customId = BingoGuiItem.getCustomString(
            clickedItem,
            "custom_id"
        );
        if (customId == null) return;

        BingoMatchSettings settings = BingoPlugin.getInstance()
            .getBingoMatch()
            .getMatchSettings();

        switch (customId) {
            case "bingo_config_gamemode_gui_item":
                GameMode[] gameModes = GameMode.values();
                GameMode nextGameMode = gameModes[(settings
                        .getGameMode()
                        .ordinal() +
                    1) %
                gameModes.length];
                settings.setGameMode(nextGameMode);
                break;
            case "bingo_config_team_mode_gui_item":
                TeamMode[] teamModes = TeamMode.values();
                TeamMode nextTeamMode = teamModes[(settings
                        .getTeamMode()
                        .ordinal() +
                    1) %
                teamModes.length];
                settings.setTeamMode(nextTeamMode);
                break;
            case "bingo_config_difficulty_gui_item":
                DifficultyLevel[] difficulties = DifficultyLevel.values();
                DifficultyLevel nextDifficulty = difficulties[(settings
                        .getDifficultyLevel()
                        .ordinal() +
                    1) %
                difficulties.length];
                settings.setDifficultyLevel(nextDifficulty);
                BingoPlugin.getInstance()
                    .getBingoMatch()
                    .generateNewBingoCard();
                break;
            case "bingo_config_duration_gui_item":
                int currentDuration = settings.getGameDuration();
                if (event.isLeftClick()) {
                    settings.setGameDuration(currentDuration + 5);
                } else if (event.isRightClick()) {
                    settings.setGameDuration(Math.max(1, currentDuration - 5));
                }
                break;
        }

        BingoMatchSettingsGui.getInstance().openForPlayer(
            player,
            new ConfigGuiContext(settings)
        );
    }
}
