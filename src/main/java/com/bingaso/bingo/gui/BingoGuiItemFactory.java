package com.bingaso.bingo.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;

import java.time.Instant;

import com.bingaso.bingo.BingoPlugin;
import com.bingaso.bingo.card.BingoCardGenerator.DifficultyLevel;
import com.bingaso.bingo.match.BingoMatch;
import com.bingaso.bingo.match.BingoMatchSettings.GameMode;
import com.bingaso.bingo.match.BingoMatchSettings.TeamMode;
import com.bingaso.bingo.quest.BingoQuest;
import com.bingaso.bingo.quest.BingoQuestItem;
import com.bingaso.bingo.player.BingoPlayer;
import com.bingaso.bingo.team.BingoTeam;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class BingoGuiItemFactory {
    
    /**
     * Creates an ItemStack representing a specific team.
     * The item appearance changes based on whether the bingoTeamOwned is the
     * same as the displayed.
     * @param bingoTeamToShow The team that will appear in the item
     * @param bingoTeamFromWatcher The bingo team from the player watching
     * @return ItemStack representing the team with appropriate styling and lore
     */
    public static BingoGuiItem createTeamGuiItem(BingoTeam bingoTeamToShow, BingoTeam bingoTeamFromWatcher) {
        // style depending on team ownership
        NamedTextColor namedTextColor = NamedTextColor.GREEN;
        if(!bingoTeamToShow.equals(bingoTeamFromWatcher)) {
            namedTextColor = NamedTextColor.RED;
        }

        // lore
        List<Component> lore = new ArrayList<>();
        for(BingoPlayer player: bingoTeamToShow.getPlayers()) {
            lore.add(Component.text(
                " - " + player.getName(),
                namedTextColor
            ));
        }
        
        // item itself
        BingoGuiItem itemStack = new BingoGuiItem(Material.PAPER, "bingo_card_team_gui_item");
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.displayName(Component.text(
            "Team \"" + bingoTeamToShow.getName() + "\"",
            namedTextColor,
            TextDecoration.BOLD
        ));
        itemMeta.lore(lore);
        itemMeta.addEnchant(Enchantment.PROTECTION, 1, true);
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    /**
     * Creates an arrow ItemStack for navigating to the previous team.
     * @param team the current team to get the previous team from
     * @return ItemStack representing the previous team navigation arrow
     */
    public static BingoGuiItem createPreviousTeamGuiItem(BingoTeam team) {
        BingoMatch gameManager = BingoPlugin.getInstance().getBingoMatch();

        // item itself
        BingoGuiItem itemStack = new BingoGuiItem(Material.ARROW, "bingo_card_previous_team_gui_item");
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.displayName(Component.text("Previous Team"));
        itemMeta.addEnchant(Enchantment.PROTECTION, 1, true);
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemStack.setItemMeta(itemMeta);
        itemStack.setCustomString("team", gameManager.getBingoTeamRepository().getPreviousTeam(team).getName());
        return itemStack;
    }

    /**
     * Creates an arrow ItemStack for navigating to the next team.
     * @param team the current team to get the next team from
     * @return ItemStack representing the next team navigation arrow
     */
    public static BingoGuiItem createNextTeamGuiItem(BingoTeam team) {
        BingoMatch gameManager = BingoPlugin.getInstance().getBingoMatch();

        // item itself
        BingoGuiItem itemStack = new BingoGuiItem(Material.ARROW, "bingo_card_next_team_gui_item");
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.displayName(Component.text("Next Team"));
        itemMeta.addEnchant(Enchantment.PROTECTION, 1, true);
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemStack.setItemMeta(itemMeta);
        itemStack.setCustomString("team", gameManager.getBingoTeamRepository().getNextTeam(team).getName());
        return itemStack;
    }

    /**
     * Creates a green stained glass pane ItemStack to represent a completed bingo quest.
     * @param bingoQuest The bingo quest that has been completed
     * @param bingoTeamThatCompleted The bingo team that completed the quest
     * @param bingoTeamFromWatcher The bingo team from the player watching
     * @return GuiItem representing the completed state with green styling
     */
    public static BingoGuiItem createCompletedGuiItem(
        BingoQuest bingoQuest,
        BingoTeam bingoTeamThatCompleted,
        BingoTeam bingoTeamFromWatcher
    ) {
        // style depending on team ownership
        Material material = Material.GREEN_STAINED_GLASS_PANE;
        NamedTextColor namedTextColor = NamedTextColor.GREEN;
        if(bingoTeamFromWatcher == null || !bingoTeamThatCompleted.equals(bingoTeamFromWatcher)) {
            namedTextColor = NamedTextColor.RED;
            material = Material.RED_STAINED_GLASS_PANE;
        }
        
        // lore
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("Completed by: " + bingoTeamThatCompleted.getName(), namedTextColor));
        
        // Get completion instant
        Instant completionInstant = bingoTeamThatCompleted.getCompletionInstant(bingoQuest);

        BingoMatch gameManager = BingoPlugin.getInstance().getBingoMatch();
        long milliseconds = gameManager.getMatchDurationMilliseconds(completionInstant);
        
        if (completionInstant != null) {
            long totalSeconds = milliseconds / 1000;
            long hours = totalSeconds / 3600;
            long minutes = (totalSeconds % 3600) / 60;
            long seconds = totalSeconds % 60;
            long millis = milliseconds % 1000;
            String formattedTime = String.format("%02d:%02d:%02d.%03d", hours, minutes, seconds, millis);
            lore.add(Component.text("Completed at: " + formattedTime, NamedTextColor.GOLD));
        }

        // item itself
        BingoGuiItem itemStack = new BingoGuiItem(material, "bingo_card_completed_gui_item");
        ItemMeta itemMeta = itemStack.getItemMeta();

        // Display name based on quest type
        String questName = "Unknown Quest";
        if (bingoQuest instanceof BingoQuestItem) {
            BingoQuestItem questItem = (BingoQuestItem) bingoQuest;
            questName = questItem.getMaterial().toString();
        }
        
        itemMeta.displayName(Component.text("Completed: " + questName, namedTextColor));
        itemMeta.lore(lore);
        itemMeta.addEnchant(Enchantment.PROTECTION, 1, true);
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    /**
     * Creates an ItemStack for creating a new team.
     * @return GuiItem representing the "Create New Team" option
     */
    public static BingoGuiItem createNewTeamGuiItem() {
        // item itself
        BingoGuiItem itemStack = new BingoGuiItem(Material.WHITE_WOOL, "bingo_team_new_team_gui_item");
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.displayName(Component.text("Create New Team", NamedTextColor.WHITE, TextDecoration.BOLD));
        itemMeta.lore(Arrays.asList(Component.text("Click to create a new Team", NamedTextColor.WHITE)));
        itemMeta.addEnchant(Enchantment.PROTECTION, 1, true);
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    /**
     * Creates an ItemStack representing a specific team to join.
     * The item appearance changes based on whether the team is full or not.
     * 
     * @param team The team to create an ItemStack for
     * @return GuiItem representing the team with appropriate styling and lore
     */
    public static BingoGuiItem createJoinTeamGuiItem(BingoTeam team) {
        BingoMatch gameManager = BingoPlugin.getInstance().getBingoMatch();

        // Variables for the item
        Material material = Material.GREEN_WOOL;
        NamedTextColor namedTextColor = NamedTextColor.GREEN;
        String displayNameText = (
            "Team \"" + team.getName() + "\"" +
            "(" + team.getSize() + "/" + gameManager.getMatchSettings().getMaxTeamSize() + ")"
        );
        String loreFirstLine = "Click to join the Team!";

        // style depending on team size
        if(team.getSize() >= gameManager.getMatchSettings().getMaxTeamSize()) {
            material = Material.RED_WOOL;
            loreFirstLine = "Team full!";
            namedTextColor = NamedTextColor.RED;
        }

        // lore
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text(
            loreFirstLine,
            namedTextColor,
            TextDecoration.BOLD
        ));
        for(BingoPlayer player: team.getPlayers()) {
            lore.add(Component.text(
                " - " + player.getName(),
                namedTextColor
            ));
        }

        // item itself
        BingoGuiItem itemStack = new BingoGuiItem(material, "bingo_team_join_team_gui_item");
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.displayName(Component.text(
            displayNameText,
            namedTextColor,
            TextDecoration.BOLD
        ));
        itemMeta.lore(lore);
        itemStack.setItemMeta(itemMeta);
        itemStack.setCustomString("team", team.getName());
        return itemStack;
    }


    /**
     * Creates a GUI item for selecting the team mode.
     * The item displays the current team mode and instructions for changing it.
     *
     * @param currentMode The currently selected team mode
     * @return A configured GuiItem for team mode selection
     */
    public static BingoGuiItem createTeamModeGuiItem(TeamMode currentMode) {
        // lore
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("Click to cycle to the next mode.",NamedTextColor.GRAY));
        lore.add(
            Component.text("Current: ", NamedTextColor.GRAY)
            .append(Component.text(currentMode.name(), NamedTextColor.YELLOW)));

        // item itself
        BingoGuiItem itemStack = new BingoGuiItem(Material.PLAYER_HEAD, "bingo_config_team_mode_gui_item");
        ItemMeta itemMeta = itemStack.getItemMeta();
        
        itemMeta.displayName(Component.text("Team Mode", NamedTextColor.AQUA));
        itemMeta.lore(lore);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    /**
     * Creates a GUI item for selecting the difficulty level.
     * The item displays the current difficulty level and instructions for changing it.
     *
     * @param currentLevel The currently selected difficulty level
     * @return A configured GuiItem for difficulty selection
     */
    public static BingoGuiItem createDifficultyGuiItem(DifficultyLevel currentLevel) {
        // lore
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("Click to cycle to the next level.", NamedTextColor.GRAY));
        lore.add(Component.text("Current: ", NamedTextColor.GRAY).append(Component.text(currentLevel.name(), NamedTextColor.YELLOW)));

        // item itself
        BingoGuiItem itemStack = new BingoGuiItem(Material.DIAMOND_SWORD, "bingo_config_difficulty_gui_item");
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.displayName(Component.text("Difficulty", NamedTextColor.AQUA));
        itemMeta.lore(lore);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    /**
     * Creates a GUI item for adjusting the game duration.
     * The item displays the current duration in minutes and instructions for increasing
     * or decreasing it using left and right clicks.
     *
     * @param currentDuration The current game duration in minutes
     * @return A configured GuiItem for duration adjustment
     */
    public static BingoGuiItem createDurationGuiItem(int currentDuration) {
        // lore
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("Left-click to add 5 minutes.", NamedTextColor.GRAY));
        lore.add(Component.text("Right-click to remove 5 minutes.", NamedTextColor.GRAY));
        lore.add(Component.text("Current: ", NamedTextColor.GRAY).append(Component.text(currentDuration + " minutes", NamedTextColor.YELLOW)));

        // item itself
        BingoGuiItem itemStack = new BingoGuiItem(Material.CLOCK, "bingo_config_duration_gui_item");
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.displayName(Component.text("Game Duration", NamedTextColor.AQUA));
        itemMeta.lore(lore);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    /**
     * Creates a GUI item for selecting the game mode.
     * The item displays the current game mode and instructions for changing it.
     *
     * @param currentMode The currently selected game mode
     * @return A configured GuiItem for game mode selection
     */
    public static BingoGuiItem createGameModeGuiItem(GameMode currentMode) {
        // lore
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("Click to cycle to the next mode.",NamedTextColor.GRAY));
        lore.add(Component.text("Current: ", NamedTextColor.GRAY).append(Component.text(currentMode.name(), NamedTextColor.YELLOW)));

        // item itself
        BingoGuiItem itemStack = new BingoGuiItem(Material.ENCHANTED_BOOK, "bingo_config_gamemode_gui_item");
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.lore(lore);
        itemMeta.displayName(Component.text("Game Mode", NamedTextColor.AQUA));
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
}
