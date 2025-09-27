package com.bingaso.bingo.match;

import com.bingaso.bingo.BingoPlugin;
import com.bingaso.bingo.card.BingoCardGui;
import com.bingaso.bingo.card.BingoCardGui.BingoCardGuiContext;
import com.bingaso.bingo.gui.BingoGuiItem;
import com.bingaso.bingo.gui.BingoGuiItemFactory;
import com.bingaso.bingo.match.managers.MatchLifecycleManager.State;
import com.bingaso.bingo.player.BingoPlayer;
import com.bingaso.bingo.team.BingoTeam;
import com.bingaso.bingo.team.select.BingoTeamSelectGui;
import com.bingaso.bingo.team.select.BingoTeamSelectGui.BingoTeamSelectGuiContext;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.FurnaceExtractEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
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
        if (gameManager.getState() != State.IN_PROGRESS) return;

        BingoPlayer bingoPlayer = gameManager
            .getBingoPlayerRepository()
            .findByUUID(player.getUniqueId());
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
        Player player = (Player) event.getWhoClicked();
        BingoMatch gameManager = BingoPlugin.getInstance().getBingoMatch();

        if (BingoGuiItem.getCustomString(clickedItem, "custom_id") != null) {
            return;
        }

        if (gameManager.getState() != State.IN_PROGRESS) return;

        if (
            event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY &&
            event.getClickedInventory() != null &&
            event.getClickedInventory().getType() != InventoryType.PLAYER &&
            clickedItem != null
        ) {
            processGetItem(player, clickedItem.getType());
        }

        if (
            event.getClickedInventory() != null &&
            event.getClickedInventory().getType() == InventoryType.PLAYER &&
            clickedItem != null
        ) {
            if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                gameManager
                    .getRobbersModeService()
                    .handleItemLoss(player, clickedItem.getType());
            } else if (
                event.getCursor() != null &&
                event.getCursor().getType() != Material.AIR &&
                event.getRawSlot() >= player.getInventory().getSize()
            ) {
                gameManager
                    .getRobbersModeService()
                    .handleItemLoss(player, event.getCursor().getType());
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        BingoMatch bingoMatch = BingoPlugin.getInstance().getBingoMatch();

        bingoMatch.getRobbersModeService().scheduleInventoryCheck(player);

        BingoPlugin.getInstance()
            .getServer()
            .getScheduler()
            .runTaskLater(
                BingoPlugin.getInstance(),
                () -> bingoMatch.checkInventoryForNewItems(player),
                1L
            );
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack draggedItem = event.getOldCursor();

        if (
            draggedItem == null || draggedItem.getType() == Material.AIR
        ) return;

        boolean movedOutOfPlayerInventory = false;
        for (Integer slot : event.getRawSlots()) {
            if (slot >= player.getInventory().getSize()) {
                movedOutOfPlayerInventory = true;
                break;
            }
        }

        if (movedOutOfPlayerInventory) {
            BingoPlugin.getInstance()
                .getBingoMatch()
                .getRobbersModeService()
                .handleItemLoss(player, draggedItem.getType());
        }
    }

    // Lobby Listeners

    /**
     * Handles player join events by creating a BingoPlayer instance.
     *
     * @param event The PlayerJoinEvent
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        // Only add players in survival or adventure into the bingo game
        if (
            player.getGameMode() != GameMode.SURVIVAL &&
            player.getGameMode() != GameMode.ADVENTURE
        ) return;

        BingoMatch gameManager = BingoPlugin.getInstance().getBingoMatch();
        gameManager.updatePlayerTabName(player);

        // Only add players if the bingo game is in lobby state
        if (gameManager.getState() != State.LOBBY) {
            player.setGameMode(GameMode.SURVIVAL);
            return;
        }

        // Add bingo player
        gameManager.addPlayer(player);
        player.setGameMode(GameMode.ADVENTURE);
        // Give them the team selection item
        player
            .getInventory()
            .addItem(BingoGuiItemFactory.createTeamSelectionItem());
    }

    /**
     * Handles player quit events by cleaning up the BingoPlayer instance.
     *
     * @param event The PlayerQuitEvent
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        // Only remove players in adventure or survival from the bingo game
        if (
            player.getGameMode() != GameMode.ADVENTURE &&
            player.getGameMode() != GameMode.SURVIVAL
        ) return;

        BingoMatch gameManager = BingoPlugin.getInstance().getBingoMatch();

        // Only remove players if the bingo game is in lobby state
        if (gameManager.getState() != State.LOBBY) return;

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

        if (
            newGameMode == GameMode.SURVIVAL ||
            newGameMode == GameMode.ADVENTURE
        ) {
            // Add player back to the game if they return to survival or adventure
            if (gameManager.getState() == State.LOBBY) {
                gameManager.addPlayer(player);
            }
        } else {
            // Remove player from the game if they leave survival or adventure
            gameManager.removePlayer(player);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        BingoMatch gameManager = BingoPlugin.getInstance().getBingoMatch();

        if (
            event.getAction() == Action.RIGHT_CLICK_AIR ||
            event.getAction() == Action.RIGHT_CLICK_BLOCK
        ) {
            ItemStack itemInHand = player.getInventory().getItemInMainHand();
            String customId = BingoGuiItem.getCustomString(
                itemInHand,
                "custom_id"
            );
            if (customId != null) {
                switch (customId) {
                    case "bingo_match_bingo_card_item":
                        BingoTeam bingoTeamFromPlayer =
                            gameManager.getBingoTeamFromPlayer(player);
                        BingoTeam bingoTeamToShow = bingoTeamFromPlayer;
                        BingoCardGui.getInstance().openForPlayer(
                            player,
                            new BingoCardGuiContext(
                                bingoTeamFromPlayer,
                                bingoTeamToShow,
                                gameManager.getBingoCard()
                            )
                        );
                        event.setCancelled(true);
                        return;
                    case "bingo_lobby_team_selection_item":
                        BingoTeamSelectGui.getInstance().openForPlayer(
                            player,
                            new BingoTeamSelectGuiContext()
                        );
                        event.setCancelled(true);
                        return;
                }
            }
        }

        if (gameManager.getState() != State.IN_PROGRESS) return;

        if (
            event.getAction() == Action.RIGHT_CLICK_BLOCK &&
            event.getItem() != null
        ) {
            if (event.getItem().getType().isBlock()) {
                gameManager
                    .getRobbersModeService()
                    .handleItemLoss(player, event.getItem().getType());
            } else if (
                event.getItem().getType() == Material.WATER_BUCKET ||
                event.getItem().getType() == Material.LAVA_BUCKET
            ) {
                gameManager
                    .getRobbersModeService()
                    .handleItemLoss(player, event.getItem().getType());
            }
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        if (
            BingoPlugin.getInstance().getBingoMatch().getState() != State.LOBBY
        ) return;

        event.setCancelled(true); // Prevent damage during the lobby state
    }

    @EventHandler
    public void onPlayerHunger(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        if (
            BingoPlugin.getInstance().getBingoMatch().getState() != State.LOBBY
        ) return;

        event.setCancelled(true); // Prevent hunger during the lobby state
    }

    @EventHandler
    public void onPlayerDropEvent(PlayerDropItemEvent event) {
        BingoMatch gameManager = BingoPlugin.getInstance().getBingoMatch();
        if (gameManager.getState() == State.IN_PROGRESS) {
            gameManager
                .getRobbersModeService()
                .handleItemLoss(
                    event.getPlayer(),
                    event.getItemDrop().getItemStack().getType()
                );
            if (
                "bingo_match_bingo_card_item".equals(
                    BingoGuiItem.getCustomString(
                        event.getItemDrop().getItemStack(),
                        "custom_id"
                    )
                )
            ) {
                event.setCancelled(true);
            }
        } else if (gameManager.getState() == State.LOBBY) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        BingoMatch gameManager = BingoPlugin.getInstance().getBingoMatch();
        if (gameManager.getState() != State.IN_PROGRESS) return;

        Player player = event.getEntity();
        for (ItemStack itemStack : event.getDrops()) {
            gameManager
                .getRobbersModeService()
                .handleItemLoss(player, itemStack.getType());
        }
        event
            .getDrops()
            .removeIf(item ->
                "bingo_match_bingo_card_item".equals(
                    BingoGuiItem.getCustomString(item, "custom_id")
                )
            );
    }

    @EventHandler
    public void onPlayerConsume(PlayerItemConsumeEvent event) {
        BingoMatch gameManager = BingoPlugin.getInstance().getBingoMatch();
        if (gameManager.getState() != State.IN_PROGRESS) return;

        Player player = event.getPlayer();
        ItemStack consumedItem = event.getItem();

        gameManager
            .getRobbersModeService()
            .handleItemLoss(player, consumedItem.getType());
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        BingoMatch gameManager = BingoPlugin.getInstance().getBingoMatch();

        if (gameManager.getState() == State.IN_PROGRESS) {
            player
                .getInventory()
                .addItem(BingoGuiItemFactory.createBingoCardItem());
        }
    }
}
