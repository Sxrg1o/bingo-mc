package com.bingaso.bingo.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import com.bingaso.bingo.model.BingoPlayer;
import java.util.Collections;

public abstract class BingoGui {

    /** If context is needed to open the inventory, this structure is used */
    public static abstract class GuiContext {}

    /** Map that contains all players that have this inventory opened */
    private final HashMap<BingoPlayer, GuiContext> openPlayers = new HashMap<>();

    /**
     * Opens the GUI for the given player with specified team and player context.
     * @param player The player to open the GUI for
     * @param context The context containing other information needed to
     * display the GUI
     */
    public void openForPlayer(Player player, GuiContext context) {
        BingoPlayer bingoPlayer = BingoPlayer.getBingoPlayer(player);
        openForPlayer(bingoPlayer, player, context);
    }

    /**
     * Opens the GUI for the given player with specified team and player context.
     * @param bingoPlayer The BingoPlayer to open the GUI for
     * @param context The context containing other information needed to
     * display the GUI
     */
    public void openForPlayer(BingoPlayer bingoPlayer, GuiContext context) {
        Player player = bingoPlayer.getOnlinePlayer();
        if(player == null) return;
        openForPlayer(bingoPlayer, player, context);
    }

    /**
     * Opens the GUI for the given player with specified team and player context.
     * @param bingoPlayer The BingoPlayer to open the GUI for
     * @param player The Player to open the GUI for
     * @param context The context containing other information needed to
     * display the GUI
     */
    private void openForPlayer(BingoPlayer bingoPlayer, Player player, GuiContext context) {
        player.openInventory(getInventory(context));
        openPlayers.put(bingoPlayer, context);
    }

    /**
     * Checks if a player has the inventory open
     * @param player The BingoPlayer to verify if has the inventory open
     * @return The configured bingo card GUI inventory
     */
    public boolean isOpenBy(BingoPlayer bingoPlayer) {
        return openPlayers.keySet().contains(bingoPlayer);
    }

    /**
     * Checks if a player has the inventory open
     * @param player The Player to verify if has the inventory open
     * @return The configured bingo card GUI inventory
     */
    public boolean isOpenBy(Player player) {
        return openPlayers.keySet().contains(BingoPlayer.getBingoPlayer(player));
    }

    /**
     * Returns an unmodifiable list of players who currently have the GUI open.
     * @return unmodifiable list of players with the GUI open
     */
    public List<BingoPlayer> getOpenPlayers() {
        return Collections.unmodifiableList(new ArrayList<>(openPlayers.keySet()));
    }

    /**
     * Removes a player from the list of open players.
     * @param bingoPlayer The BingoPlayer to remove
     */
    public void removeOpenPlayer(BingoPlayer bingoPlayer) {
        openPlayers.remove(bingoPlayer);
    }

    /**
     * Removes a player from the list of open players.
     * @param player The player to remove
     */
    public void removeOpenPlayer(Player player) {
        openPlayers.remove(BingoPlayer.getBingoPlayer(player));
    }

    /**
     * Updates all open inventories with current bingo card state.
     * Refreshes the GUI for all players who currently have it open.
     */
    public void updateInventories() {
        HashMap<BingoPlayer, GuiContext> copyOpenPlayers = new HashMap<>(this.openPlayers);
        for (Entry<BingoPlayer, GuiContext> entry : copyOpenPlayers.entrySet()) {
            BingoPlayer player = entry.getKey();
            GuiContext context = entry.getValue();
            openForPlayer(player, context);
        }
    }

    /**
     * Gets the inventory for the given BingoPlayer and context.
     * Context is class dependant.
     *
     * @param context The context containing information to open the inventory
     * @return The configured inventory for the given context
     */
    public abstract Inventory getInventory(GuiContext context);
}