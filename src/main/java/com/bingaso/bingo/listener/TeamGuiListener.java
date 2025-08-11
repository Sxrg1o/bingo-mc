package com.bingaso.bingo.listener;

import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import com.bingaso.bingo.gui.GuiItem;
import com.bingaso.bingo.gui.TeamsGui;
import com.bingaso.bingo.model.BingoPlayer;
import com.bingaso.bingo.model.BingoTeam;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

/**
 * Listener for handling interactions with the Teams GUI.
 * Manages team creation and joining through inventory click events.
 */
public class TeamGuiListener implements Listener {

    /**
     * Handles inventory close events for the Teams GUI.
     * Removes the player from the list of players with the GUI open.
     * 
     * @param event The inventory close event
     */
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        if (TeamsGui.getInstance().getOpenPlayers().contains(player)) {
            TeamsGui.getInstance().removeOpenPlayer(player);
        }
    }

    /**
     * Handles inventory click events for the Teams GUI.
     * Processes team creation and team joining based on the clicked item.
     * 
     * @param event The inventory click event
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        BingoPlayer bingoPlayer = BingoPlayer.getBingoPlayer(player.getUniqueId());
        if (!TeamsGui.getInstance().getOpenPlayers().contains(player)) {
            return;
        }

        event.setCancelled(true);

        // Check first gui item
        if(GuiItem.isGuiItem(clickedItem, "NewTeamItemStack")) {
            String randomTeamName = "Team-" + UUID.randomUUID().toString().substring(0, 8);
            BingoTeam newTeam;
            try {
                newTeam = new BingoTeam(randomTeamName);
            } catch (BingoTeam.TeamNameAlreadyExistsException e) {
                player.sendMessage(Component.text(
                    "Haha random collision, try again.",
                    NamedTextColor.RED
                ));
                return;
            }
            player.sendMessage(Component.text(
                "Succesfully created new team with name \"" + newTeam.getName() + "\".",
                NamedTextColor.GREEN
            ));
            try {
                newTeam.addPlayer(bingoPlayer);
                player.sendMessage(Component.text(
                    "Added you to the new team.",
                    NamedTextColor.GREEN
                ));
                return;
            } catch (BingoTeam.MaxPlayersException e) {
                player.sendMessage(Component.text(
                    "Couldn't add you to the new team.",
                    NamedTextColor.RED
                ));
                return;
            }
        }

        // Check second gui item
        if(GuiItem.isGuiItem(clickedItem, "TeamItemStack")) {
            String teamName = GuiItem.getCustomString(clickedItem, "team");
            if(teamName == null) return;
            BingoTeam team = BingoTeam.getTeamByName(teamName);
            if(team == null) {
                player.sendMessage(Component.text(
                    "Team not found, couldn't join.",
                    NamedTextColor.RED
                ));
                return;
            } 

            try {
                team.addPlayer(bingoPlayer);
                player.sendMessage(Component.text(
                    "You have joined the team \"" + team.getName() + "\".",
                    NamedTextColor.GREEN
                ));
                return;
            } catch (BingoTeam.MaxPlayersException e) {
                player.sendMessage(Component.text(
                    "Couldn't join the team, it is full.",
                    NamedTextColor.RED
                ));
                return;
            }
        }
    }
}
