package com.bingaso.bingo.gui;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;

import com.bingaso.bingo.model.BingoCard;
import com.bingaso.bingo.model.BingoItem;
import com.bingaso.bingo.model.BingoPlayer;
import com.bingaso.bingo.model.BingoTeam;
import com.bingaso.bingo.BingoPlugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.bukkit.entity.Player;
import java.util.Map.Entry;

/**
 * GUI for displaying each teams bingo card.
 */
public class BingoCardGui {

    /** Singleton instance */
    public static final BingoCardGui INSTANCE = new BingoCardGui();
    private BingoCardGui() {}

    private final HashMap<Player, BingoTeam> openPlayers = new HashMap<>();

    /**
     * Gets the singleton instance of BingoCardGui.
     * @return The singleton BingoCardGui instance
     */
    public static BingoCardGui getInstance() {
        return INSTANCE;
    }

    /**
     * Opens the GUI for the given player with specified team and player context.
     * @param player the player to open the GUI for
     * @param team the team whose bingo card to display
     */
    public void openForPlayer(Player player, BingoTeam team) {
        BingoPlayer bingoPlayer = BingoPlayer.getBingoPlayer(player.getUniqueId());
        player.openInventory(getInventory(team, bingoPlayer));
        openPlayers.put(player, team);
    }

    /**
     * Returns an unmodifiable list of players who currently have the GUI open.
     * @return unmodifiable list of players with the GUI open
     */
    public List<Player> getOpenPlayers() {
        return java.util.Collections.unmodifiableList(new ArrayList<>(openPlayers.keySet()));
    }

    /**
     * Removes a player from the list of open players.
     * @param player the player to remove
     */
    public void removeOpenPlayer(Player player) {
        openPlayers.remove(player);
    }

    /**
     * Updates all open inventories with current bingo card state.
     * Refreshes the GUI for all players who currently have it open.
     */
    public void updateInventories() {
        HashMap<Player, BingoTeam> copyOpenPlayers = new HashMap<>(this.openPlayers);
        for (Entry<Player, BingoTeam> entry : copyOpenPlayers.entrySet()) {
            Player player = entry.getKey();
            BingoTeam team = entry.getValue();
            openForPlayer(player, team);
        }
    }

    /**
     * Creates an ItemStack representing a specific team.
     * The item appearance changes based on whether the bingo player is part of
     * the team or not.
     * 
     * @param team The team to create an ItemStack for
     * @param bingoPlayer The bingo player context for styling the item
     * @return ItemStack representing the team with appropriate styling and lore
     */
    private GuiItem teamItemStack(BingoTeam team, BingoPlayer bingoPlayer) {
        GuiItem itemStack = new GuiItem(Material.PAPER, "TeamItemStack");
        ItemMeta meta = itemStack.getItemMeta();
        NamedTextColor namedTextColor = NamedTextColor.GREEN;
        if(!team.equals(bingoPlayer.getTeam())) {
            namedTextColor = NamedTextColor.RED;
        }
        meta.displayName(Component.text(
            "Team \"" + team.getName() + "\"",
            namedTextColor,
            TextDecoration.BOLD
        ));
        List<Component> newTeamLore = new ArrayList<>();
        for(BingoPlayer player: team.getPlayers()) {
            newTeamLore.add(Component.text(
                " - " + player.getName(),
                namedTextColor
            ));
        }
        meta.lore(newTeamLore);
        meta.addEnchant(Enchantment.PROTECTION, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    /**
     * Creates an arrow ItemStack for navigating to the previous team.
     * @param team the current team to get the previous team from
     * @return ItemStack representing the previous team navigation arrow
     */
    private GuiItem PreviousTeamItemStack(BingoTeam team) {
        GuiItem itemStack = new GuiItem(Material.ARROW, "PreviousTeamItemStack");
        ItemMeta meta = itemStack.getItemMeta();
        meta.displayName(Component.text("Previous Team"));
        itemStack.setItemMeta(meta);
        itemStack.setCustomString("team", team.getPreviousTeam().getName());
        return itemStack;
    }

    /**
     * Creates an arrow ItemStack for navigating to the next team.
     * @param team the current team to get the next team from
     * @return ItemStack representing the next team navigation arrow
     */
    private GuiItem nextTeamItemStack(BingoTeam team) {
        GuiItem itemStack = new GuiItem(Material.ARROW, "NextTeamItemStack");
        ItemMeta meta = itemStack.getItemMeta();
        meta.displayName(Component.text("Next Team"));
        itemStack.setItemMeta(meta);
        itemStack.setCustomString("team", team.getNextTeam().getName());
        return itemStack;
    }

    /**
     * Creates a green stained glass pane ItemStack to represent a completed bingo item.
     * @param bingoItem the bingo item that has been completed
     * @return GuiItem representing the completed state with green styling
     */
    private GuiItem completedItem(BingoItem bingoItem) {
        GuiItem itemStack = new GuiItem(Material.GREEN_STAINED_GLASS_PANE, "CompletedItem");
        ItemMeta meta = itemStack.getItemMeta();
        meta.displayName(Component.text("Completed: " + bingoItem.getMaterial().toString(), NamedTextColor.GREEN));
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    /**
     * Creates and returns the bingo GUI inventory.
     * Contains all bingo items for the current team and buttons to iterate
     * through the other teams.
     * 
     * @param team The team whose bingo card to display
     * @param bingoPlayer The bingo player context for styling
     * @return The configured bingo card GUI inventory
     */
    private Inventory getInventory(BingoTeam team, BingoPlayer bingoPlayer) {
        BingoCard bingoCard = BingoPlugin.getInstance().getGameManager().getSharedBingoCard();
        Inventory bingoCardGui = Bukkit.createInventory(
            null,
            54,
            Component.text("BingoCard", NamedTextColor.RED, TextDecoration.BOLD)
        );

        // Add 25 items of the bingo card in the center of the inventory
        int i = 0;
        for (BingoItem bingoItem : bingoCard.getItems()) {
            int j = i % 5;
            int k = i / 5;

            GuiItem bingoItemStack = new GuiItem(bingoItem.getMaterial(), "BingoItemStack");
            if(bingoItem.isCompletedBy(team)) {
                bingoItemStack = completedItem(bingoItem);
            }
            bingoCardGui.setItem(2 + k*9 + j, bingoItemStack); // Place in the center 5x5 grid
            i++;
        }
        // Add arrows for navigating teams
        bingoCardGui.setItem(45, PreviousTeamItemStack(team));
        bingoCardGui.setItem(49, teamItemStack(team, bingoPlayer));
        bingoCardGui.setItem(53, nextTeamItemStack(team));
        return bingoCardGui;
    }
}
