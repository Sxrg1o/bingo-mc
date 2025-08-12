package com.bingaso.bingo.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import com.bingaso.bingo.model.BingoItem;
import com.bingaso.bingo.model.BingoPlayer;
import com.bingaso.bingo.model.BingoTeam;
import com.bingaso.bingo.model.DifficultyLevel;
import com.bingaso.bingo.model.GameMode;
import com.bingaso.bingo.model.TeamMode;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class GuiItemFactory {
    
    /**
     * Creates an ItemStack representing a specific team.
     * The item appearance changes based on whether the bingoTeamOwned is the
     * same as the displayed.
     * @param bingoTeamToShow The team that will appear in the item
     * @param bingoTeamFromWatcher The bingo team from the player watching
     * @return ItemStack representing the team with appropriate styling and lore
     */
    public static GuiItem createTeamGuiItem(BingoTeam bingoTeamToShow, BingoTeam bingoTeamFromWatcher) {
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
        GuiItem itemStack = new GuiItem(Material.PAPER, "bingo_card_team_gui_item");
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
    public static GuiItem createPreviousTeamGuiItem(BingoTeam team) {
        // item itself
        GuiItem itemStack = new GuiItem(Material.ARROW, "bingo_card_previous_team_gui_item");
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.displayName(Component.text("Previous Team"));
        itemMeta.addEnchant(Enchantment.PROTECTION, 1, true);
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemStack.setItemMeta(itemMeta);
        itemStack.setCustomString("team", team.getPreviousTeam().getName());
        return itemStack;
    }

    /**
     * Creates an arrow ItemStack for navigating to the next team.
     * @param team the current team to get the next team from
     * @return ItemStack representing the next team navigation arrow
     */
    public static GuiItem createNextTeamGuiItem(BingoTeam team) {
        // item itself
        GuiItem itemStack = new GuiItem(Material.ARROW, "bingo_card_next_team_gui_item");
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.displayName(Component.text("Next Team"));
        itemMeta.addEnchant(Enchantment.PROTECTION, 1, true);
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemStack.setItemMeta(itemMeta);
        itemStack.setCustomString("team", team.getNextTeam().getName());
        return itemStack;
    }

    /**
     * Creates a green stained glass pane ItemStack to represent a completed bingo item.
     * @param bingoItem The bingo item that has been completed
     * @param bingoTeamThatCompleted The bingo team that completed the item
     * @param bingoTeamFromWatcher The bingo team from the player watching
     * @return GuiItem representing the completed state with green styling
     */
    public static GuiItem createCompletedGuiItem(BingoItem bingoItem, BingoTeam bingoTeamThatCompleted, BingoTeam bingoTeamFromWatcher) {
        // style depending on team ownership
        Material material = Material.GREEN_STAINED_GLASS_PANE;
        NamedTextColor namedTextColor = NamedTextColor.GREEN;
        if(!bingoTeamThatCompleted.equals(bingoTeamFromWatcher)) {
            namedTextColor = NamedTextColor.RED;
            material = Material.RED_STAINED_GLASS_PANE;
        }
        
        // lore
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("Completed by: " + bingoTeamThatCompleted.getName(), namedTextColor));
        Long completionMilliseconds = bingoItem.getCompletionMilliseconds(bingoTeamThatCompleted);
        if (completionMilliseconds != null) {
            long totalSeconds = completionMilliseconds / 1000;
            int millisRemain = (int) (completionMilliseconds % 1000);
            LocalTime time = LocalTime.ofSecondOfDay(totalSeconds);
            String formattedTime = time.format(DateTimeFormatter.ofPattern("HH:mm:ss")) + "." + String.format("%03d", millisRemain);
            lore.add(Component.text("Completed at: " + formattedTime, NamedTextColor.GOLD));
        }

        // item itself
        GuiItem itemStack = new GuiItem(material, "bingo_card_completed_gui_item");
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.displayName(Component.text("Completed: " + bingoItem.getMaterial().toString(), namedTextColor));
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
    public static GuiItem createNewTeamGuiItem() {
        // item itself
        GuiItem itemStack = new GuiItem(Material.WHITE_WOOL, "bingo_team_new_team_gui_item");
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
    public static GuiItem createJoinTeamGuiItem(BingoTeam team) {
        // Variables for the item
        Material material = Material.GREEN_WOOL;
        NamedTextColor namedTextColor = NamedTextColor.GREEN;
        String displayNameText = (
            "Team \"" + team.getName() + "\n" +
            "(" + team.getSize() + "/" + BingoTeam.getTeamsMaxSize() + ")"
        );
        String loreFirstLine = "Click to join the Team!";

        // style depending on team size
        if(team.getSize() >= BingoTeam.getTeamsMaxSize()) {
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
        GuiItem itemStack = new GuiItem(material, "bingo_team_join_team_gui_item");
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
    public static GuiItem createTeamModeGuiItem(TeamMode currentMode) {
        // lore
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("Click to cycle to the next mode.",NamedTextColor.GRAY));
        lore.add(
            Component.text("Current: ", NamedTextColor.GRAY)
            .append(Component.text(currentMode.name(), NamedTextColor.YELLOW)));

        // item itself
        GuiItem itemStack = new GuiItem(Material.PLAYER_HEAD, "bingo_config_team_mode_gui_item");
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
    public static GuiItem createDifficultyGuiItem(DifficultyLevel currentLevel) {
        // lore
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("Click to cycle to the next level.", NamedTextColor.GRAY));
        lore.add(Component.text("Current: ", NamedTextColor.GRAY).append(Component.text(currentLevel.name(), NamedTextColor.YELLOW)));

        // item itself
        GuiItem itemStack = new GuiItem(Material.DIAMOND_SWORD, "bingo_config_difficulty_gui_item");
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
    public static GuiItem createDurationGuiItem(int currentDuration) {
        // lore
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("Left-click to add 5 minutes.", NamedTextColor.GRAY));
        lore.add(Component.text("Right-click to remove 5 minutes.", NamedTextColor.GRAY));
        lore.add(Component.text("Current: ", NamedTextColor.GRAY).append(Component.text(currentDuration + " minutes", NamedTextColor.YELLOW)));

        // item itself
        GuiItem itemStack = new GuiItem(Material.CLOCK, "bingo_config_duration_gui_item");
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
    public static GuiItem createGameModeGuiItem(GameMode currentMode) {
        // lore
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("Click to cycle to the next mode.",NamedTextColor.GRAY));
        lore.add(Component.text("Current: ", NamedTextColor.GRAY).append(Component.text(currentMode.name(), NamedTextColor.YELLOW)));

        // item itself
        GuiItem itemStack = new GuiItem(Material.ENCHANTED_BOOK, "config_gamemode");
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.lore(lore);
        itemMeta.displayName(Component.text("Game Mode", NamedTextColor.AQUA));
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
}
