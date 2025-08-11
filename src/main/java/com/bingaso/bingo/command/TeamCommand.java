package com.bingaso.bingo.command;

import com.bingaso.bingo.model.BingoPlayer;
import com.bingaso.bingo.model.BingoTeam;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Command executor for team-related commands.
 * Handles: /team create, /team join <teamId>, /team leave, /team list
 */
public class TeamCommand implements CommandExecutor, TabCompleter {
    
    @Override
    public boolean onCommand(
        CommandSender sender,
        Command command,
        String label,
        String[] args
    ) {
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
        
        switch (subCommand) {
            case "create":
                if (args.length < 2) {
                    player.sendMessage(Component.text("Usage: /team create <teamName>", NamedTextColor.RED));
                    return true;
                }
                return handleCreateTeam(player, args[1]);
                
            case "join":
                if (args.length < 2) {
                    player.sendMessage(Component.text("Usage: /team join <teamName>",NamedTextColor.RED));
                    return true;
                }
                return handleJoinTeam(player, args[1]);
                
            case "leave":
                return handleLeaveTeam(player);
                
            case "list":
                return handleListTeams(player);
                
            case "info":
                return handleTeamInfo(player);
                
            default:
                sendHelpMessage(player);
                return true;
        }
    }
    
    private boolean handleCreateTeam(Player player, String teamName) {
        BingoPlayer bingoPlayer = BingoPlayer.getBingoPlayer(player.getUniqueId());

        try {
            BingoTeam team = new BingoTeam(teamName);
            team.addPlayer(bingoPlayer);
            player.sendMessage(Component.text("Team created successfully! Team Name: " + team.getName(), NamedTextColor.GREEN));
        } catch (BingoTeam.MaxPlayersException e) {
            player.sendMessage(Component.text("Error: " + e.getMessage(), NamedTextColor.RED));
        } catch (BingoTeam.TeamNameAlreadyExistsException e) {
            player.sendMessage(Component.text("A team with this name already exists. Please choose a different name.", NamedTextColor.RED));
        }
        return true;
    }
    
    private boolean handleJoinTeam(Player player, String teamName) {
        BingoTeam team = BingoTeam.getTeamByName(teamName);
        if(team == null) {
            player.sendMessage(Component.text("Team not found. Please check the team name.", NamedTextColor.RED));
            return true;
        }
        BingoPlayer bingoPlayer = BingoPlayer.getBingoPlayer(player.getUniqueId());

        try {
            team.addPlayer(bingoPlayer);
            player.sendMessage(Component.text("Successfully joined team " + team.getName(), NamedTextColor.GREEN));
        } catch (BingoTeam.MaxPlayersException e) {
            player.sendMessage(Component.text("Team is full!", NamedTextColor.RED));
        } catch (Exception e) {
            player.sendMessage(Component.text("An error occurred while joining the team.", NamedTextColor.RED));
        }
        return true;
    }
    
    private boolean handleLeaveTeam(Player player) {
        BingoPlayer bingoPlayer = BingoPlayer.getBingoPlayer(player.getUniqueId());
        BingoTeam team = bingoPlayer.getTeam();
        if (team == null) {
            player.sendMessage(Component.text("You are not in a team.", NamedTextColor.RED));
            return true;
        }

        try {
            team.removePlayer(bingoPlayer);
            player.sendMessage(Component.text("Successfully left team " + team.getName(), NamedTextColor.GREEN));
        } catch (Exception e) {
            player.sendMessage(Component.text("An error occurred while leaving the team.", NamedTextColor.RED));
        }
        return true;
    }
    
    private boolean handleListTeams(Player player) {
        List<BingoTeam> teams = BingoTeam.getAllTeams();
        if (teams.isEmpty()) {
            player.sendMessage(Component.text("No teams available.", NamedTextColor.YELLOW));
            return true;
        }
        
        player.sendMessage(Component.text("Available Teams:", NamedTextColor.GOLD));
        for (BingoTeam team : teams) {
            String status = team.getSize() >= BingoTeam.getTeamsMaxSize() ? "Full" : "Open";
            player.sendMessage(Component.text(team.getName() + " - " + team.getSize() + "/" + BingoTeam.getTeamsMaxSize() + " (" + status + ")", NamedTextColor.AQUA));
        }
        return true;
    }
    
    private boolean handleTeamInfo(Player player) {
        BingoPlayer bingoPlayer = BingoPlayer.getBingoPlayer(player.getUniqueId());
        BingoTeam team = bingoPlayer.getTeam();
        if (team == null) {
            player.sendMessage(Component.text("You are not in a team.",NamedTextColor.RED));
            return true;
        }

        player.sendMessage(Component.text("=== Your Team Info ===", NamedTextColor.GREEN));
        player.sendMessage(Component.text("Team Name: " + team.getName(), NamedTextColor.GREEN));
        player.sendMessage(Component.text("Team Size: " + team.getSize() + "/" + BingoTeam.getTeamsMaxSize(), NamedTextColor.GREEN));
        player.sendMessage(Component.text("Team Members:", NamedTextColor.GREEN));

        team.getPlayers().forEach(teammate -> {
            String status = teammate.isOnline() ? "[Online]" : "[Offline]";
            NamedTextColor statusColor = teammate.isOnline() ? NamedTextColor.GREEN : NamedTextColor.RED;
            player.sendMessage(Component.text("  - " + teammate.getName() + " " + status,statusColor));
        });
        
        return true;
    }
    
    private void sendHelpMessage(Player player) {
        player.sendMessage(Component.text("=== Team Commands ===", NamedTextColor.GREEN));
        player.sendMessage(Component.text("/team create - Create a new team", NamedTextColor.GREEN));
        player.sendMessage(Component.text("/team join <teamId> - Join an existing team", NamedTextColor.GREEN));
        player.sendMessage(Component.text("/team leave - Leave your current team", NamedTextColor.GREEN));
        player.sendMessage(Component.text("/team list - List available teams", NamedTextColor.GREEN));
        player.sendMessage(Component.text("/team info - Show your team information", NamedTextColor.GREEN));
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("create", "join", "leave", "list", "info");
        }
        
        if (args.length == 2 && args[0].equalsIgnoreCase("join")) {
            List<String> teamNames = BingoTeam.getAllTeams().stream()
                .map(team -> team.getName())
                .collect(Collectors.toList());
            return teamNames;
        }
        
        return new ArrayList<>();
    }
}
