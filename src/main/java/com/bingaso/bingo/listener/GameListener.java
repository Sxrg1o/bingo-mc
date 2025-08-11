package com.bingaso.bingo.listener;

import com.bingaso.bingo.game.GameManager;
import com.bingaso.bingo.game.GameState;
import com.bingaso.bingo.gui.GuiItem;
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

public class GameListener implements Listener {

    private final GameManager gameManager;

    public GameListener(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    private void processGetItem(Player player, Material material) {
        if (gameManager.getCurrentState() != GameState.IN_PROGRESS) return;

        BingoPlayer bingoPlayer = BingoPlayer.getBingoPlayer(
            player.getUniqueId()
        );
        if (bingoPlayer == null || bingoPlayer.getTeam() == null) {
            return;
        }

        gameManager.onPlayerFindsItem(bingoPlayer, material);
    }

    @EventHandler
    public void onPlayerPickupItem(EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            Material material = event.getItem().getItemStack().getType();
            processGetItem(player, material);
        }
    }

    @EventHandler
    public void onCraftItem(CraftItemEvent event) {
        Player player = (Player) event.getWhoClicked();
        Material material = event.getRecipe().getResult().getType();
        processGetItem(player, material);
    }

    @EventHandler
    public void onFurnaceExtract(FurnaceExtractEvent event) {
        Player player = event.getPlayer();
        Material material = event.getItemType();
        processGetItem(player, material);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        ItemStack clickedItem = event.getCurrentItem();

        if (GuiItem.getCustomString(clickedItem, "custom_id") != null) return;

        if (gameManager.getCurrentState() != GameState.IN_PROGRESS) return;

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
}
