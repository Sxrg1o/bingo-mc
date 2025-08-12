package com.bingaso.bingo.listener;

import com.bingaso.bingo.BingoPlugin;
import com.bingaso.bingo.game.GameState;
import com.bingaso.bingo.gui.BingoGuiItem;
import com.bingaso.bingo.model.BingoPlayer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.FurnaceExtractEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

/**
 * Listens for game-related events to detect when players find Bingo items.
 * This class handles various ways players can acquire items, including picking up items,
 * crafting items, extracting items from furnaces, and transferring items in inventories.
 */
public class GameListener implements Listener {

    /**
     * Processes an item acquisition by a player during a Bingo game.
     * Checks if the game is in progress, if the player is registered in the game,
     * and if the player belongs to a team before notifying the game manager.
     *
     * @param player The player who acquired the item
     * @param material The material type of the acquired item
     */
    private void processGetItem(Player player, Material material) {
        if (
            BingoPlugin.getInstance().getGameManager().getCurrentState() !=
            GameState.IN_PROGRESS
        ) return;

        BingoPlayer bingoPlayer = BingoPlayer.getBingoPlayer(
            player.getUniqueId()
        );
        if (bingoPlayer == null || bingoPlayer.getTeam() == null) {
            return;
        }

        BingoPlugin.getInstance()
            .getGameManager()
            .onPlayerFindsItem(bingoPlayer, material);
    }

    /**
     * Handles item pickup events.
     * Detects when a player picks up an item from the ground and processes it
     * for potential Bingo card completion.
     *
     * @param event The entity pickup item event
     */
    @EventHandler
    public void onPlayerPickupItem(EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            Material material = event.getItem().getItemStack().getType();
            processGetItem(player, material);
        }
    }

    /**
     * Handles item crafting events.
     * Detects when a player crafts an item and processes it
     * for potential Bingo card completion.
     *
     * @param event The craft item event
     */
    @EventHandler
    public void onCraftItem(CraftItemEvent event) {
        Player player = (Player) event.getWhoClicked();
        Material material = event.getRecipe().getResult().getType();
        processGetItem(player, material);
    }

    /**
     * Handles furnace extraction events.
     * Detects when a player removes an item from a furnace and processes it
     * for potential Bingo card completion.
     *
     * @param event The furnace extract event
     */
    @EventHandler
    public void onFurnaceExtract(FurnaceExtractEvent event) {
        Player player = event.getPlayer();
        Material material = event.getItemType();
        processGetItem(player, material);
    }

    /**
     * Handles inventory click events.
     * Detects when a player shifts items between inventories and processes them
     * for potential Bingo card completion. Only considers non-GUI items when
     * moving items from non-player inventories (like chests, hoppers, etc.)
     * to the player inventory.
     *
     * @param event The inventory click event
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        ItemStack clickedItem = event.getCurrentItem();

        if (BingoGuiItem.getCustomString(clickedItem, "custom_id") != null) return;

        if (
            BingoPlugin.getInstance().getGameManager().getCurrentState() !=
            GameState.IN_PROGRESS
        ) return;

        Player player = (Player) event.getWhoClicked();

        if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
            if (
                event.getClickedInventory() != null &&
                event.getClickedInventory().getType() != InventoryType.PLAYER
            ) {
                Material material = clickedItem.getType();
                processGetItem(player, material);
            }
        }
    }

    // TODO: Inventory click but with drag
}
