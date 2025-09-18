package com.bingaso.bingo.team.command;

import com.bingaso.bingo.command.BingoSubCommand;
import com.bingaso.bingo.team.select.BingoTeamSelectGui;
import com.bingaso.bingo.team.select.BingoTeamSelectGui.BingoTeamSelectGuiContext;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class BingoTeamSelectHandler implements BingoSubCommand {
    
    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Component.text("This command can only be used by players.", NamedTextColor.RED));
            return true;
        }
        
        Player player = (Player) sender;
        BingoTeamSelectGui.getInstance().openForPlayer(
            player,
            new BingoTeamSelectGuiContext()
        );
        return true;
    }
    
    public void sendHelpMessage(Player player) {
        player.sendMessage(Component.text("/bingo team select - Select your team with gui", NamedTextColor.GREEN));
    }
    
    @Override
    public @Nullable List<String> getTabCompletions(@NotNull CommandSender sender, @NotNull String[] args) {
        return new ArrayList<>();
    }
}
