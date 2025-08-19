package com.bingaso.bingo.team.select;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

import com.bingaso.bingo.BingoPlugin;
import com.bingaso.bingo.gui.BingoGui;
import com.bingaso.bingo.gui.BingoGuiItemFactory;
import com.bingaso.bingo.match.BingoMatch;
import com.bingaso.bingo.team.BingoTeam;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

/**
 * GUI for displaying and managing teams in the Bingo game.
 * Provides interface for creating new teams and joining existing ones.
 */
public class BingoTeamSelectGui extends BingoGui {

    /** Singleton instance */
    public static final BingoTeamSelectGui INSTANCE = new BingoTeamSelectGui();
    private BingoTeamSelectGui() {}
    
    /**
     * Gets the singleton instance of TeamsGui.
     * @return The singleton TeamsGui instance
     */
    public static BingoTeamSelectGui getInstance() {
        return INSTANCE;
    }

    /** Context necessary to open this inventory */
    public static class BingoTeamSelectGuiContext extends GuiContext {
        public BingoTeamSelectGuiContext() {}
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
            Component.text("Team Selection", NamedTextColor.GOLD, TextDecoration.BOLD)
        );

        BingoMatch gameMatch = BingoPlugin.getInstance().getBingoMatch();
        // Adds a new wool for each team
        for(BingoTeam t: gameMatch.getBingoTeamRepository().findAll()) {
            inventory.addItem(BingoGuiItemFactory.createJoinTeamGuiItem(t));
        }

        // Adds a wool to create a team
        inventory.addItem(BingoGuiItemFactory.createNewTeamGuiItem());
        return inventory;
    }
}
