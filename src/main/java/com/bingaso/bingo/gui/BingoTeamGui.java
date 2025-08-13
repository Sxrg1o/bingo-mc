package com.bingaso.bingo.gui;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

import com.bingaso.bingo.BingoPlugin;
import com.bingaso.bingo.game.BingoGameManager;
import com.bingaso.bingo.team.BingoTeam;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

/**
 * GUI for displaying and managing teams in the Bingo game.
 * Provides interface for creating new teams and joining existing ones.
 */
public class BingoTeamGui extends BingoGui {

    /** Singleton instance */
    public static final BingoTeamGui INSTANCE = new BingoTeamGui();
    private BingoTeamGui() {}
    
    /**
     * Gets the singleton instance of TeamsGui.
     * @return The singleton TeamsGui instance
     */
    public static BingoTeamGui getInstance() {
        return INSTANCE;
    }

    /** Context necessary to open this inventory */
    public static class BingoTeamGuiContext extends GuiContext {
        public BingoTeamGuiContext() {}
    }

    /**
     * Creates and returns the teams GUI inventory.
     * Contains items for all existing teams plus an option to create
     * a new team.
     * 
     * @return The configured teams GUI inventory
     */
    @Override
    public Inventory getInventory(GuiContext context) {
        Inventory inventory = Bukkit.createInventory(
            null,
            27,
            Component.text("Choose your team", NamedTextColor.GOLD, TextDecoration.BOLD)
        );

        BingoGameManager gameManager = BingoPlugin.getInstance().getGameManager();
        // Adds a new wool for each team
        for(BingoTeam t: gameManager.getTeams()) {
            inventory.addItem(BingoGuiItemFactory.createJoinTeamGuiItem(t));
        }

        // Adds a wool to create a team
        inventory.addItem(BingoGuiItemFactory.createNewTeamGuiItem());
        return inventory;
    }
}
