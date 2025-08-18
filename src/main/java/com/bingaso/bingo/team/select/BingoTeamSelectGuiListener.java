package com.bingaso.bingo.team.select;

import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import com.bingaso.bingo.BingoPlugin;
import com.bingaso.bingo.gui.BingoGuiItem;
import com.bingaso.bingo.match.BingoMatch;
import com.bingaso.bingo.match.BingoMatch.MaxPlayersException;
import com.bingaso.bingo.team.BingoTeam;
import com.bingaso.bingo.team.BingoTeamRepository.TeamNameAlreadyExistsException;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

/**
 * Listener for handling interactions with the Teams GUI.
 * Manages team creation and joining through inventory click events.
 */
public class BingoTeamSelectGuiListener implements Listener {

    /**
     * Handles inventory close events for the Teams GUI.
     * Removes the player from the list of players with the GUI open.
     * 
     * @param event The inventory close event
     */
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        if (BingoTeamSelectGui.getInstance().isOpenBy(player)) {
            BingoTeamSelectGui.getInstance().removeOpenPlayer(player);
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
        // Check no air conditions
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        BingoMatch bingoMatch = BingoPlugin.getInstance().getBingoMatch();
        if (!BingoTeamSelectGui.getInstance().isOpenBy(player)) {
            return;
        }

        event.setCancelled(true);

        // Check first gui item
        if(BingoGuiItem.isGuiItem(clickedItem, "NewTeamItemStack")) {
            String randomTeamName = "Team-" + UUID.randomUUID().toString().substring(0, 8);
            BingoTeam newTeam;
            try {
                newTeam = bingoMatch.createBingoTeam(randomTeamName);
            } catch (TeamNameAlreadyExistsException e) {
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
                bingoMatch.addPlayerToBingoTeam(player, newTeam);
                player.sendMessage(Component.text(
                    "Added you to the new team.",
                    NamedTextColor.GREEN
                ));
            } catch (MaxPlayersException e) {
                player.sendMessage(Component.text(
                    "Couldn't add you to the new team.",
                    NamedTextColor.RED
                ));
            }
            BingoTeamSelectGui.getInstance().updateInventories();
        }

        // Check second gui item
        if(BingoGuiItem.isGuiItem(clickedItem, "TeamItemStack")) {
            String teamName = BingoGuiItem.getCustomString(clickedItem, "team");
            if(teamName == null) return;
            BingoTeam team
                = bingoMatch.getBingoTeamRepository().findByName(teamName);
            if(team == null) {
                player.sendMessage(Component.text(
                    "Team not found, couldn't join.",
                    NamedTextColor.RED
                ));
                return;
            } 

            try {
                bingoMatch.addPlayerToBingoTeam(player, team);
                player.sendMessage(Component.text(
                    "You have joined the team \"" + team.getName() + "\".",
                    NamedTextColor.GREEN
                ));
                BingoTeamSelectGui.getInstance().updateInventories();
            } catch (MaxPlayersException e) {
                player.sendMessage(Component.text(
                    "Couldn't join the team, it is full.",
                    NamedTextColor.RED
                ));
            }
        }
    }
}
