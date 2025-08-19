package com.bingaso.bingo.team.select;

import com.bingaso.bingo.command.BingoSubCommand;
import com.bingaso.bingo.team.select.handlers.*;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Command executor for team-related commands.
 * Handles: /bingoteam create, /bingoteam join <teamId>, /bingoteam leave, /bingoteam list
 */
public class BingoTeamSubCommand implements BingoSubCommand {
    
    private final Map<String, BingoSubCommand> handlers;
    private final CreateTeamHandler createHandler;
    private final JoinTeamHandler joinHandler;
    private final LeaveTeamHandler leaveHandler;
    private final ListTeamsHandler listHandler;
    private final TeamInfoHandler infoHandler;
    private final SelectTeamHandler selectHandler;
    
    public BingoTeamSubCommand() {
        this.createHandler = new CreateTeamHandler();
        this.joinHandler = new JoinTeamHandler();
        this.leaveHandler = new LeaveTeamHandler();
        this.listHandler = new ListTeamsHandler();
        this.infoHandler = new TeamInfoHandler();
        this.selectHandler = new SelectTeamHandler();
        
        this.handlers = new HashMap<>();
        handlers.put("create", createHandler);
        handlers.put("join", joinHandler);
        handlers.put("leave", leaveHandler);
        handlers.put("list", listHandler);
        handlers.put("info", infoHandler);
        handlers.put("select", selectHandler);
    }
    
    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Component.text("This command can only be used by players.", NamedTextColor.RED));
            return true;
        }
        
        Player player = (Player) sender;
        
        if (args.length == 0) {
            sendHelpMessage(player);
            return true;
        }
        
        String subCommand = args[0].toLowerCase();
        BingoSubCommand handler = handlers.get(subCommand);
        
        if (handler != null) {
            String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
            return handler.execute(sender, subArgs);
        } else {
            sendHelpMessage(player);
            return true;
        }
    }
    
    private void sendHelpMessage(Player player) {
        player.sendMessage(Component.text("=== Bingo Team Commands ===", NamedTextColor.GREEN));
        createHandler.sendHelpMessage(player);
        joinHandler.sendHelpMessage(player);
        leaveHandler.sendHelpMessage(player);
        listHandler.sendHelpMessage(player);
        infoHandler.sendHelpMessage(player);
        selectHandler.sendHelpMessage(player);
    }
    
    @Override
    public @Nullable List<String> getTabCompletions(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length == 1) {
            return Arrays.asList("create", "join", "leave", "list", "info", "select");
        }
        
        if (args.length >= 2) {
            String subCommand = args[0].toLowerCase();
            BingoSubCommand handler = handlers.get(subCommand);
            if (handler != null) {
                return handler.getTabCompletions(sender, args);
            }
        }
        
        return new ArrayList<>();
    }
}