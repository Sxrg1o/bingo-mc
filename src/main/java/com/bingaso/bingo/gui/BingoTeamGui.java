package com.bingaso.bingo.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.meta.ItemMeta;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.bingaso.bingo.model.BingoPlayer;
import com.bingaso.bingo.model.BingoTeam;
import org.bukkit.entity.Player;

/**
 * GUI for displaying and managing teams in the Bingo game.
 * Provides interface for creating new teams and joining existing ones.
 */
public class BingoTeamGui {

    /** Singleton instance */
    public static final BingoTeamGui INSTANCE = new BingoTeamGui();
    private BingoTeamGui() {}
    
    /** List of players who currently have the teams GUI open */
    private final List<Player> openPlayers = new ArrayList<>();
    
    /**
     * Gets the singleton instance of TeamsGui.
     * @return The singleton TeamsGui instance
     */
    public static BingoTeamGui getInstance() {
        return INSTANCE;
    }

    /**
     * Opens the GUI for the given player.
     * @param player the player to open the GUI for
     */
    public void openForPlayer(Player player) {
        player.openInventory(getInventory());
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
     * Updates all open team GUI inventories with current team state.
     * Refreshes the GUI for all players who currently have it open.
     */
    public void updateInventories() {
        List<Player> copyOpenPlayers = new ArrayList<>(openPlayers);
        for (Player player : copyOpenPlayers) {
            openForPlayer(player);
        }
    }

    /**
     * Creates an ItemStack for creating a new team.
     * @return GuiItem representing the "Create New Team" option
     */
    private GuiItem newTeamItemStack() {
        GuiItem itemStack = new GuiItem(Material.WHITE_WOOL, "NewTeamItemStack");
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.displayName(Component.text("Create New Team", NamedTextColor.WHITE, TextDecoration.BOLD));
        itemMeta.lore(Arrays.asList(Component.text("Click to create a new Team", NamedTextColor.WHITE)));
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    /**
     * Creates an ItemStack representing a specific team.
     * The item appearance changes based on whether the team is full or not.
     * 
     * @param team The team to create an ItemStack for
     * @return GuiItem representing the team with appropriate styling and lore
     */
    private GuiItem teamItemStack(BingoTeam team) {
        Material material = Material.GREEN_WOOL;
        String displayNameText = (
            "Team \"" + team.getName() + "\n" +
            "(" + team.getSize() + "/" + BingoTeam.getTeamsMaxSize() + ")"
        );
        String loreFirstLine = "Click to join the Team!";
        NamedTextColor namedTextColor = NamedTextColor.GREEN;
        if(team.getSize() >= BingoTeam.getTeamsMaxSize()) {
            material = Material.RED_WOOL;
            loreFirstLine = "Team full!";
            namedTextColor = NamedTextColor.RED;
        }

        GuiItem newTeamItemStack = new GuiItem(material, "TeamItemStack");
        ItemMeta newTeamItemMeta = newTeamItemStack.getItemMeta();
        newTeamItemMeta.displayName(Component.text(
            displayNameText,
            namedTextColor,
            TextDecoration.BOLD
        ));
        List<Component> newTeamLore = new ArrayList<>();
        newTeamLore.add(Component.text(
            loreFirstLine,
            namedTextColor,
            TextDecoration.BOLD
        ));
        for(BingoPlayer player: team.getPlayers()) {
            newTeamLore.add(Component.text(
                " - " + player.getName(),
                namedTextColor
            ));
        }
        newTeamItemMeta.lore(newTeamLore);
        newTeamItemStack.setItemMeta(newTeamItemMeta);
        newTeamItemStack.setCustomString("team", team.getName());
        return newTeamItemStack;
    }

    /**
     * Creates and returns the teams GUI inventory.
     * Contains items for all existing teams plus an option to create
     * a new team.
     * 
     * @return The configured teams GUI inventory
     */
    private Inventory getInventory() {
        Inventory teamsGui = Bukkit.createInventory(
            null,
            27,
            Component.text("TeamGUI", NamedTextColor.RED, TextDecoration.BOLD)
        );

        // Adds a new wool for each team
        for(BingoTeam t: BingoTeam.getAllTeams()) {
            teamsGui.addItem(teamItemStack(t));
        }

        // Adds a wool to create a team
        teamsGui.addItem(newTeamItemStack());
        return teamsGui;
    }
}
