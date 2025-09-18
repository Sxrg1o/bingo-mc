package com.bingaso.bingo.team.command;

import com.bingaso.bingo.BingoPlugin;
import com.bingaso.bingo.command.BingoSubCommand;
import com.bingaso.bingo.match.BingoMatch;
import com.bingaso.bingo.team.BingoTeam;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class BingoTeamListHandler implements BingoSubCommand {
    
    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Component.text("This command can only be used by players.", NamedTextColor.RED));
            return true;
        }
        
        Player player = (Player) sender;
        BingoMatch gameManager = BingoPlugin.getInstance().getBingoMatch();

        List<BingoTeam> teams = gameManager.getBingoTeamRepository().findAll();
        if (teams.isEmpty()) {
            player.sendMessage(Component.text("No teams available.", NamedTextColor.YELLOW));
            return true;
        }
        
        player.sendMessage(Component.text("Available Teams:", NamedTextColor.GOLD));
        for (BingoTeam team : teams) {
            String status = team.getSize() >= gameManager.getMatchSettings().getMaxTeamSize() ? "Full" : "Open";
            player.sendMessage(Component.text(team.getName() + " - " + team.getSize() + "/" + gameManager.getMatchSettings().getMaxTeamSize() + " (" + status + ")", NamedTextColor.AQUA));
        }
        return true;
    }
    
    public void sendHelpMessage(Player player) {
        player.sendMessage(Component.text("/bingo team list - List available teams", NamedTextColor.GREEN));
    }
    
    @Override
    public @Nullable List<String> getTabCompletions(@NotNull CommandSender sender, @NotNull String[] args) {
        return new ArrayList<>();
    }
}
