package com.bingaso.bingo.match;

import com.bingaso.bingo.BingoPlugin;
import com.bingaso.bingo.gui.BingoGuiItem;
import com.bingaso.bingo.player.BingoPlayer;

import org.bukkit.GameMode;
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
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Listens for game-related events to detect when players find Bingo items.
 * This class handles various ways players can acquire items, including picking up items,
 * crafting items, extracting items from furnaces, and transferring items in inventories.
 */
public class BingoMatchListener implements Listener {

    /**
     * Processes an item acquisition by a player during a Bingo game.
     * Checks if the game is in progress, if the player is registered in the game,
     * and if the player belongs to a team before notifying the game manager.
     *
     * @param player The player who acquired the item
     * @param material The material type of the acquired item
     */
    private void processGetItem(Player player, Material material) {
        BingoMatch gameManager = BingoPlugin.getInstance().getBingoMatch();
        if (gameManager.getState() != BingoMatch.State.IN_PROGRESS) return;

        BingoPlayer bingoPlayer
            = gameManager.getBingoPlayerRepository().findByUUID(player.getUniqueId());
        BingoPlugin.getInstance()
            .getBingoMatch()
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
            BingoPlugin.getInstance().getBingoMatch().getState() !=
            BingoMatch.State.IN_PROGRESS
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


    
    /**
     * Handles player join events by creating a BingoPlayer instance.
     *
     * @param event The PlayerJoinEvent
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        // Only add players in survival into the bingo game
        if(player.getGameMode() != GameMode.SURVIVAL) return;

        BingoMatch gameManager = BingoPlugin.getInstance().getBingoMatch();

        // Only add players if the bingo game is in lobby state
        if(gameManager.getState() != BingoMatch.State.LOBBY) return;
        // Add bingo player
        gameManager.addPlayer(player);
    }
    
    /**
     * Handles player quit events by cleaning up the BingoPlayer instance.
     *
     * @param event The PlayerQuitEvent
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        // Only remove players in survival into the bingo game
        if(player.getGameMode() != GameMode.SURVIVAL) return;

        BingoMatch gameManager = BingoPlugin.getInstance().getBingoMatch();

        // Only remove players if the bingo game is in lobby state
        if(gameManager.getState() != BingoMatch.State.LOBBY) return;
        // Remove bingo player
        gameManager.removePlayer(player);
    }

    /**
     * Handles player GameMode change events.
     * Effectively removing the BingoPlayer if the player is no longer in
     * survival, and adding it back if the player returns to survival.
     *
     * @param event The PlayerGameModeChangeEvent
     */
    @EventHandler
    public void onPlayerGameModeChange(PlayerGameModeChangeEvent event) {
        Player player = event.getPlayer();
        GameMode newGameMode = event.getNewGameMode();

        BingoMatch gameManager = BingoPlugin.getInstance().getBingoMatch();

        if (newGameMode == GameMode.SURVIVAL) {
            // Add player back to the game if they return to survival
            if (gameManager.getState() == BingoMatch.State.LOBBY) {
                gameManager.addPlayer(player);
            }
        } else {
            // Remove player from the game if they leave survival
            gameManager.removePlayer(player);
        }
    }
}
