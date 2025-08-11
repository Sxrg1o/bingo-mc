package com.bingaso.bingo.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.Arrays;
import java.util.List;

import com.bingaso.bingo.BingoPlugin;
import com.bingaso.bingo.model.BingoPlayer;
import com.bingaso.bingo.model.BingoTeam;

public class TeamsGui {
    private TeamsGui() {}

    public final static NamespacedKey TEAM_ID_KEY = new NamespacedKey(BingoPlugin.getInstance(), "team_id");

    public static ItemStack NewTeamItemStack() {
        ItemStack newTeamItemStack = new ItemStack(Material.WHITE_WOOL);
        ItemMeta newTeamItemMeta = newTeamItemStack.getItemMeta();
        newTeamItemMeta.displayName(
            Component.text("Create New Team", NamedTextColor.WHITE, TextDecoration.BOLD));
        newTeamItemMeta.lore(Arrays.asList(
            Component.text("Click to create a new Team", NamedTextColor.WHITE)));
        newTeamItemStack.setItemMeta(newTeamItemMeta);
        return newTeamItemStack;
    }

    public static ItemStack TeamItemStack(BingoTeam team) {
        Material newTeamMaterial = Material.GREEN_WOOL;
        String newTeamDisplayName = (
            "Team \"" + team.getName() + "\n" +
            "(" + team.getSize() + "/" + BingoTeam.getTeamsMaxSize() + ")"
        );
        String newTeamFirstLoreLine = "Click to join the Team!";
        NamedTextColor newTeamNamedTextColor = NamedTextColor.GREEN;
        if(team.getSize() >= BingoTeam.getTeamsMaxSize()) {
            newTeamMaterial = Material.RED_WOOL;
            newTeamFirstLoreLine = "Team full!";
            newTeamNamedTextColor = NamedTextColor.RED;
        }

        ItemStack newTeamItemStack = new ItemStack(newTeamMaterial);
        ItemMeta newTeamItemMeta = newTeamItemStack.getItemMeta();

        // Add the team_id_key into the item
        newTeamItemMeta.getPersistentDataContainer().set(
            TEAM_ID_KEY,
            PersistentDataType.STRING,
            team.getUniqueId().toString());

        newTeamItemMeta.displayName(Component.text(
            newTeamDisplayName,
            newTeamNamedTextColor,
            TextDecoration.BOLD
        ));
        List<Component> newTeamLore = Arrays.asList(Component.text(
            newTeamFirstLoreLine,
            newTeamNamedTextColor,
            TextDecoration.BOLD
        ));
        for(BingoPlayer player: team.getPlayers()) {
            newTeamLore.add(Component.text(
                " - " + player.getName(),
                newTeamNamedTextColor
            ));
        }
        newTeamItemMeta.lore(newTeamLore);
        newTeamItemStack.setItemMeta(newTeamItemMeta);
        return newTeamItemStack;
    }

    public static Inventory getIntentory() {
        Inventory teamsGui = Bukkit.createInventory(
            null,
            27,
            Component.text("TeamGUI", NamedTextColor.RED, TextDecoration.BOLD)
        );

        // Adds a new wool for each team
        for(BingoTeam t: BingoTeam.getAllTeams()) {
            teamsGui.addItem(TeamItemStack(t));
        }

        // Adds a wool to create a team
        teamsGui.addItem(NewTeamItemStack());
        return teamsGui;
    }
}
