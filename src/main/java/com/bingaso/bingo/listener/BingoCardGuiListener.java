package com.bingaso.bingo.listener;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import com.bingaso.bingo.gui.BingoCardGui;
import com.bingaso.bingo.gui.GuiItem;
import com.bingaso.bingo.model.BingoCard;
import com.bingaso.bingo.model.BingoTeam;

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
        if (BingoCardGui.getInstance().getOpenPlayers().contains(player)) {
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
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        if (!BingoCardGui.getInstance().getOpenPlayers().contains(player)) {
            return;
        }

        event.setCancelled(true);

        // Check if the clicked item is the next or previous team arrow
        if(
            GuiItem.isGuiItem(clickedItem, "NextTeamItemStack") ||
            GuiItem.isGuiItem(clickedItem, "PreviousTeamItemStack")
        ) {
            String teamName = GuiItem.getCustomString(clickedItem, "team");
            if(teamName == null) return;

            BingoCardGui.getInstance().openForPlayer(
                player,
                BingoTeam.getTeamByName(teamName),
                new BingoCard()
            );
            return;
        }
    }
}
