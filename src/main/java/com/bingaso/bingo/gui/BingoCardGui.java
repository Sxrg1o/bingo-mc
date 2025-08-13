package com.bingaso.bingo.gui;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

import com.bingaso.bingo.card.BingoCard;
import com.bingaso.bingo.card.quest.BingoQuest;
import com.bingaso.bingo.card.quest.BingoQuestItem;
import com.bingaso.bingo.team.BingoTeam;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

/**
 * GUI for displaying each teams bingo card.
 */
public class BingoCardGui extends BingoGui {

    /** Singleton instance */
    public static final BingoCardGui INSTANCE = new BingoCardGui();
    private BingoCardGui() {}

    /**
     * Gets the singleton instance of BingoCardGui.
     * @return The singleton BingoCardGui instance
     */
    public static BingoCardGui getInstance() {
        return INSTANCE;
    }

    /** Context necessary to open this inventory */
    public static class BingoCardGuiContext extends GuiContext {
        public BingoTeam bingoTeamToShow;
        public BingoTeam bingoTeamFromWatcher;
        public BingoCard bingoCard;

        public BingoCardGuiContext(BingoTeam bingoTeamToShow, BingoTeam bingoTeamFromWatcher, BingoCard bingoCard) {
            this.bingoTeamToShow = bingoTeamToShow;
            this.bingoTeamFromWatcher = bingoTeamFromWatcher;
            this.bingoCard = bingoCard;
        }
    }

    /**
     * Creates and returns the bingo GUI inventory.
     * Contains all bingo items for the current team and buttons to iterate
     * through the other teams.
     * 
     * @param context The context for styling
     * @return The configured bingo card GUI inventory
     * @throws IllegalArgumentException if the context is invalid
     */
    @Override
    public Inventory getInventory(GuiContext context) {
        if(context instanceof BingoCardGuiContext) {
            return getInventory((BingoCardGuiContext) context);
        } else {
            throw new IllegalArgumentException("Invalid context");
        }
    }

    /**
     * Creates and returns the bingo GUI inventory.
     * Contains all bingo items for the current team and buttons to iterate
     * through the other teams.
     * 
     * @param context The context for styling
     * @return The configured bingo card GUI inventory
     */
    public Inventory getInventory(BingoCardGuiContext context) {
        BingoTeam bingoTeamToShow = context.bingoTeamToShow;
        BingoTeam bingoTeamFromWatcher = context.bingoTeamFromWatcher;
        BingoCard bingoCard = context.bingoCard;

        Inventory inventory = Bukkit.createInventory(
            null,
            54,
            Component.text("Bingo Card", NamedTextColor.GOLD, TextDecoration.BOLD)
        );

        // Add 25 items of the bingo card in the center of the inventory
        int i = 0;
        for (BingoQuest bingoQuest : bingoCard.getItems()) {
            int j = i % 5;
            int k = i / 5;

            BingoGuiItem bingoItemStack;
            
            // Handle different quest types
            if (bingoQuest instanceof BingoQuestItem) {
                BingoQuestItem questItem = (BingoQuestItem) bingoQuest;
                bingoItemStack = new BingoGuiItem(questItem.getMaterial(), "BingoItemStack");
            } else {
                // For other quest types, use a default material (placeholder)
                bingoItemStack = new BingoGuiItem(org.bukkit.Material.PAPER, "BingoItemStack");
            }
            
            if(bingoQuest.isCompletedBy(bingoTeamToShow)) {
                bingoItemStack = BingoGuiItemFactory.createCompletedGuiItem(bingoQuest, bingoTeamToShow, bingoTeamFromWatcher);
            }
            inventory.setItem(2 + k*9 + j, bingoItemStack); // Place in the center 5x5 grid
            i++;
        }
        // Add arrows for navigating teams
        inventory.setItem(45, BingoGuiItemFactory.createPreviousTeamGuiItem(bingoTeamToShow));
        inventory.setItem(49, BingoGuiItemFactory.createTeamGuiItem(bingoTeamToShow, bingoTeamFromWatcher));
        inventory.setItem(53, BingoGuiItemFactory.createNextTeamGuiItem(bingoTeamToShow));
        return inventory;
    }
}
