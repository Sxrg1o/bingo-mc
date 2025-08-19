package com.bingaso.bingo.team.select.handlers;

import com.bingaso.bingo.BingoPlugin;
import com.bingaso.bingo.command.BingoSubCommand;
import com.bingaso.bingo.match.BingoMatch;
import com.bingaso.bingo.player.BingoPlayer;
import com.bingaso.bingo.team.BingoTeam;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TeamInfoHandler implements BingoSubCommand {
    
    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Component.text("This command can only be used by players.", NamedTextColor.RED));
            return true;
        }
        
        Player player = (Player) sender;
        BingoMatch gameManager = BingoPlugin.getInstance().getBingoMatch();
        BingoPlayer bingoPlayer = gameManager.getBingoPlayerRepository().findByUUID(player.getUniqueId());
        if(bingoPlayer == null) {
            player.sendMessage(Component.text("You are not playing bingo...", NamedTextColor.RED));
            return true;
        }
        BingoTeam bingoTeam = gameManager.getBingoTeamRepository().findTeamByPlayer(bingoPlayer);
        if (bingoTeam == null) {
            player.sendMessage(Component.text("You are not in a team.", NamedTextColor.RED));
            return true;
        }

        player.sendMessage(Component.text("=== Your Team Info ===", NamedTextColor.GREEN));
        player.sendMessage(Component.text("Team Name: " + bingoTeam.getName(), NamedTextColor.GREEN));
        player.sendMessage(Component.text("Team Size: " + bingoTeam.getSize() + "/" + gameManager.getMatchSettings().getMaxTeamSize(), NamedTextColor.GREEN));
        player.sendMessage(Component.text("Team Members:", NamedTextColor.GREEN));

        bingoTeam.getPlayers().forEach(teammate -> {
            String status = teammate.isOnline() ? "[Online]" : "[Offline]";
            NamedTextColor statusColor = teammate.isOnline() ? NamedTextColor.GREEN : NamedTextColor.RED;
            player.sendMessage(Component.text("  - " + teammate.getName() + " " + status, statusColor));
        });
        
        return true;
    }
    
    public void sendHelpMessage(Player player) {
        player.sendMessage(Component.text("/bingo team info - Show your team information", NamedTextColor.GREEN));
    }
    
    @Override
    public @Nullable List<String> getTabCompletions(@NotNull CommandSender sender, @NotNull String[] args) {
        return new ArrayList<>();
    }
}
