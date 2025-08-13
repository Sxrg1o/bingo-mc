package com.bingaso.bingo.listener;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import com.bingaso.bingo.BingoPlugin;
import com.bingaso.bingo.game.BingoGameManager;
import com.bingaso.bingo.gui.BingoCardGui;
import com.bingaso.bingo.gui.BingoCardGui.BingoCardGuiContext;
import com.bingaso.bingo.gui.BingoGuiItem;

/**
 * Listener for handling interactions with the Bingo Card GUI.
 * Manages card iteration through inventory click events.
 */
public class BingoCardGuiListener implements Listener {

    /**
     * Handles inventory close events for the Bingo Card GUI.
     * Removes the player from the list of players with the GUI open.
     * 
     * @param event The inventory close event
     */
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        if (BingoCardGui.getInstance().isOpenBy(player)) {
            BingoCardGui.getInstance().removeOpenPlayer(player);
        }
    }

    /**
     * Handles inventory click events for the Bingo Card GUI.
     * Processes card iteration based on the clicked item.
     * 
     * @param event The inventory click event
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        BingoGameManager gameManager = BingoPlugin.getInstance().getGameManager();
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        if (!BingoCardGui.getInstance().isOpenBy(player)) {
            return;
        }

        event.setCancelled(true);

        // Check if the clicked item is the next or previous team arrow
        if(
            BingoGuiItem.isGuiItem(clickedItem, "NextTeamItemStack") ||
            BingoGuiItem.isGuiItem(clickedItem, "PreviousTeamItemStack")
        ) {
            String teamName = BingoGuiItem.getCustomString(clickedItem, "team");
            if(teamName == null) return;

            BingoCardGui.getInstance().openForPlayer(
                player,
                new BingoCardGuiContext(
                    gameManager.getBingoTeam(teamName),
                    gameManager.getPlayerTeam(player),
                    BingoPlugin.getInstance().getGameManager().getSharedBingoCard())
            );
            return;
        }
    }
}
