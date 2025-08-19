package com.bingaso.bingo.card;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import com.bingaso.bingo.BingoPlugin;
import com.bingaso.bingo.card.BingoCardGui.BingoCardGuiContext;
import com.bingaso.bingo.gui.BingoGuiItem;
import com.bingaso.bingo.match.BingoMatch;

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
        BingoMatch gameManager = BingoPlugin.getInstance().getBingoMatch();
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
            BingoGuiItem.isGuiItem(clickedItem, "bingo_card_next_team_gui_item") ||
            BingoGuiItem.isGuiItem(clickedItem, "bingo_card_previous_team_gui_item")
        ) {
            String teamName = BingoGuiItem.getCustomString(clickedItem, "team");
            if(teamName == null) return;

            BingoCardGui.getInstance().openForPlayer(
                player,
                new BingoCardGuiContext(
                    gameManager.getBingoTeamRepository().findByName(teamName),
                    gameManager.getBingoTeamFromPlayer(player),
                    BingoPlugin.getInstance().getBingoMatch().getBingoCard())
            );
            return;
        }
    }
}
