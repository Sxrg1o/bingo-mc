package com.bingaso.bingo.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.Collections;

public abstract class BingoGui {

    /** If context is needed to open the inventory, this structure is used */
    public static abstract class GuiContext {}

    /** Map that contains all players that have this inventory opened */
    private final HashMap<Player, GuiContext> openPlayers = new HashMap<>();
    /**
     * Opens the GUI for the given player with specified team and player context.
     * @param player The Player to open the GUI for
     * @param context The context containing other information needed to
     * display the GUI
     */
    public void openForPlayer(Player player, GuiContext context) {
        player.openInventory(getInventory(context));
        openPlayers.put(player, context);
    }

    /**
     * Checks if a player has the inventory open
     * @param player The Player to verify if has the inventory open
     * @return The configured bingo card GUI inventory
     */
    public boolean isOpenBy(Player player) {
        return openPlayers.keySet().contains(player);
    }

    /**
     * Returns an unmodifiable list of players who currently have the GUI open.
     * @return unmodifiable list of players with the GUI open
     */
    public List<Player> getOpenPlayers() {
        return Collections.unmodifiableList(new ArrayList<>(openPlayers.keySet()));
    }

    /**
     * Removes a player from the list of open players.
     * @param player The player to remove
     */
    public void removeOpenPlayer(Player player) {
        openPlayers.remove(player);
    }

    /**
     * Updates all open inventories with current bingo card state.
     * Refreshes the GUI for all players who currently have it open.
     */
    public void updateInventories() {
        HashMap<Player, GuiContext> copyOpenPlayers = new HashMap<>(openPlayers);
        for (Entry<Player, GuiContext> entry : copyOpenPlayers.entrySet()) {
            Player player = entry.getKey();
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