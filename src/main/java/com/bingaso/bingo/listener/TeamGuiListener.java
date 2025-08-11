package com.bingaso.bingo.listener;

import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import com.bingaso.bingo.BingoPlugin;
import com.bingaso.bingo.gui.TeamsGui;
import com.bingaso.bingo.model.BingoPlayer;
import com.bingaso.bingo.model.BingoTeam;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class TeamGuiListener implements Listener {

    public final NamespacedKey TEAM_NAME = new NamespacedKey(BingoPlugin.getInstance(), "team_name");

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        // Check if the clicked inventory is null or the clicked item is null or air
        ItemStack clickedItem = event.getCurrentItem();
        Inventory clickedInventory = event.getClickedInventory();
        if (event.getClickedInventory() == null || clickedItem == null || clickedItem.getType() == Material.AIR) {
            return;
        }

        // Check if the inventory matches
        if (clickedInventory.equals(TeamsGui.getIntentory())) {
            return; // If it's not our inventory, do nothing.
        }
        event.setCancelled(true);
        
        Player player = (Player) event.getWhoClicked();
        BingoPlayer bingoPlayer = BingoPlayer.getBingoPlayer(player.getUniqueId());

        // Create new team click
        if(clickedItem.equals(TeamsGui.NewTeamItemStack())) {
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
        
        // Otherwise joins to the existent team
        ItemMeta clickedItemMeta = clickedItem.getItemMeta();
        String teamName;
        try {
            teamName = clickedItemMeta.getPersistentDataContainer().get(
                TEAM_NAME,
                PersistentDataType.STRING
            );
        } catch (Exception e) {
            return; // If the item does not have the team_name key, do nothing.
        }
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
