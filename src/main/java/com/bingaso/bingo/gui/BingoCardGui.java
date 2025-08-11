package com.bingaso.bingo.gui;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;

import com.bingaso.bingo.model.BingoCard;
import com.bingaso.bingo.model.BingoItem;
import com.bingaso.bingo.model.BingoPlayer;
import com.bingaso.bingo.model.BingoTeam;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Player;

/**
 * GUI for displaying each teams bingo card.
 */
public class BingoCardGui {

    /** Singleton instance */
    public static final BingoCardGui INSTANCE = new BingoCardGui();
    private BingoCardGui() {}
    
    private final List<Player> openPlayers = new ArrayList<>(); 
    
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
     * @param bingoCard the bingo card to display in the GUI
     */
    public void openForPlayer(Player player, BingoTeam team, BingoCard bingoCard) {
        BingoPlayer bingoPlayer = BingoPlayer.getBingoPlayer(player.getUniqueId());
        player.openInventory(getInventory(team, bingoPlayer, bingoCard));
        if (!openPlayers.contains(player)) {
            openPlayers.add(player);
        }
    }

    /**
     * Returns an unmodifiable list of players who currently have the GUI open.
     * @return unmodifiable list of players with the GUI open
     */
    public List<Player> getOpenPlayers() {
        return java.util.Collections.unmodifiableList(openPlayers);
    }

    /**
     * Removes a player from the list of open players.
     * @param player the player to remove
     */
    public void removeOpenPlayer(Player player) {
        openPlayers.remove(player);
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
    public GuiItem TeamItemStack(BingoTeam team, BingoPlayer bingoPlayer) {
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
    public GuiItem PreviousTeamItemStack(BingoTeam team) {
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
    public GuiItem NextTeamItemStack(BingoTeam team) {
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
    public GuiItem CompletedItem(BingoItem bingoItem) {
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
     * @param bingoCard The bingo card containing the items to display
     * @return The configured bingo card GUI inventory
     */
    public Inventory getInventory(BingoTeam team, BingoPlayer bingoPlayer, BingoCard bingoCard) {
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

            ItemStack bingoItemStack = new ItemStack(bingoItem.getMaterial());
            if(bingoItem.isCompletedBy(team)) {
                bingoItemStack = CompletedItem(bingoItem);
            }
            bingoCardGui.setItem(11 + k*9 + j, bingoItemStack); // Place in the center 5x5 grid
            i++;
        }
        // Add arrows for navigating teams
        bingoCardGui.setItem(45, PreviousTeamItemStack(team));
        bingoCardGui.setItem(49, TeamItemStack(team, bingoPlayer));
        bingoCardGui.setItem(53, NextTeamItemStack(team));
        return bingoCardGui;
    }
}
