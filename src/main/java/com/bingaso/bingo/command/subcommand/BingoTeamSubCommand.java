package com.bingaso.bingo.command.subcommand;

import com.bingaso.bingo.BingoPlugin;
import com.bingaso.bingo.command.BingoCommand.BingoSubCommand;
import com.bingaso.bingo.game.BingoGameManager;
import com.bingaso.bingo.gui.BingoTeamGui;
import com.bingaso.bingo.gui.BingoTeamGui.BingoTeamGuiContext;
import com.bingaso.bingo.team.BingoTeam;
import com.bingaso.bingo.team.BingoTeamManager.MaxPlayersException;
import com.bingaso.bingo.team.BingoTeamManager.MaxTeamsException;
import com.bingaso.bingo.team.BingoTeamManager.TeamNameAlreadyExistsException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Command executor for team-related commands.
 * Handles: /bingoteam create, /bingoteam join <teamId>, /bingoteam leave, /bingoteam list
 */
public class BingoTeamSubCommand implements BingoSubCommand {

    @Override
    public boolean execute(
        @NotNull CommandSender sender,
        @NotNull String[] args
    ) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(
                Component.text(
                    "This command can only be used by players.",
                    NamedTextColor.RED
                )
            );
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
                    player.sendMessage(
                        Component.text(
                            "Usage: /team create <teamName>",
                            NamedTextColor.RED
                        )
                    );
                    return true;
                }
                return handleCreateTeam(player, args[1]);
            case "join":
                if (args.length < 2) {
                    player.sendMessage(
                        Component.text(
                            "Usage: /team join <teamName>",
                            NamedTextColor.RED
                        )
                    );
                    return true;
                }
                return handleJoinTeam(player, args[1]);
            case "leave":
                return handleLeaveTeam(player);
            case "list":
                return handleListTeams(player);
            case "info":
                return handleTeamInfo(player);
            case "select":
                return handleSelectTeam(player);
            default:
                sendHelpMessage(player);
                return true;
        }
    }

    private boolean handleSelectTeam(Player player) {
        BingoTeamGui.getInstance().openForPlayer(
            player,
            new BingoTeamGuiContext()
        );
        return true;
    }

    private boolean handleCreateTeam(Player player, String teamName) {
        BingoGameManager gameManager =
            BingoPlugin.getInstance().getGameManager();

        try {
            BingoTeam bingoTeam = gameManager.createBingoTeam(teamName);
            gameManager.addPlayerToTeam(player, bingoTeam);
            player.sendMessage(
                Component.text(
                    "Team created successfully! Team Name: " +
                    bingoTeam.getName(),
                    NamedTextColor.GREEN
                )
            );
        } catch (MaxPlayersException e) {
            player.sendMessage(
                Component.text("Error: " + e.getMessage(), NamedTextColor.RED)
            );
        } catch (TeamNameAlreadyExistsException e) {
            player.sendMessage(
                Component.text(
                    "A team with this name already exists. Please choose a different name.",
                    NamedTextColor.RED
                )
            );
        } catch (MaxTeamsException e) {
            player.sendMessage(
                Component.text("Error: " + e.getMessage(), NamedTextColor.RED)
            );
        }

        BingoTeamGui.getInstance().updateInventories();
        return true;
    }

    private boolean handleJoinTeam(Player player, String teamName) {
        BingoGameManager gameManager =
            BingoPlugin.getInstance().getGameManager();
        BingoTeam bingoTeam = gameManager.getBingoTeam(teamName);

        if (bingoTeam == null) {
            player.sendMessage(
                Component.text(
                    "Team not found. Please check the team name.",
                    NamedTextColor.RED
                )
            );
            return true;
        }

        try {
            gameManager.addPlayerToTeam(player, bingoTeam);
            player.sendMessage(
                Component.text(
                    "Successfully joined team " + bingoTeam.getName(),
                    NamedTextColor.GREEN
                )
            );
        } catch (MaxPlayersException e) {
            player.sendMessage(
                Component.text("Team is full!", NamedTextColor.RED)
            );
        } catch (Exception e) {
            player.sendMessage(
                Component.text(
                    "An error occurred while joining the team.",
                    NamedTextColor.RED
                )
            );
        }

        BingoTeamGui.getInstance().updateInventories();
        return true;
    }

    private boolean handleLeaveTeam(Player player) {
        BingoGameManager gameManager =
            BingoPlugin.getInstance().getGameManager();
        BingoTeam oldTeam = gameManager.getPlayerTeam(player);
        if (oldTeam == null) {
            player.sendMessage(
                Component.text("You are not in a team.", NamedTextColor.RED)
            );
            return true;
        }
        gameManager.removeBingoPlayer(player);
        player.sendMessage(
            Component.text(
                "Successfully left team " + oldTeam.getName(),
                NamedTextColor.GREEN
            )
        );

        BingoTeamGui.getInstance().updateInventories();
        return true;
    }

    private boolean handleListTeams(Player player) {
        BingoGameManager gameManager =
            BingoPlugin.getInstance().getGameManager();
        List<BingoTeam> teams = gameManager.getTeams();
        if (teams.isEmpty()) {
            player.sendMessage(
                Component.text("No teams available.", NamedTextColor.YELLOW)
            );
            return true;
        }

        player.sendMessage(
            Component.text("Available Teams:", NamedTextColor.GOLD)
        );
        for (BingoTeam team : teams) {
            String status = team.getSize() >= gameManager.getMaxTeamSize()
                ? "Full"
                : "Open";
            player.sendMessage(
                Component.text(
                    team.getName() +
                    " - " +
                    team.getSize() +
                    "/" +
                    gameManager.getMaxTeamSize() +
                    " (" +
                    status +
                    ")",
                    NamedTextColor.AQUA
                )
            );
        }
        return true;
    }

    private boolean handleTeamInfo(Player player) {
        BingoGameManager gameManager =
            BingoPlugin.getInstance().getGameManager();
        BingoTeam bingoTeam = gameManager.getPlayerTeam(player);
        if (bingoTeam == null) {
            player.sendMessage(
                Component.text("You are not in a team.", NamedTextColor.RED)
            );
            return true;
        }

        player.sendMessage(
            Component.text("=== Your Team Info ===", NamedTextColor.GREEN)
        );
        player.sendMessage(
            Component.text(
                "Team Name: " + bingoTeam.getName(),
                NamedTextColor.GREEN
            )
        );
        player.sendMessage(
            Component.text(
                "Team Size: " +
                bingoTeam.getSize() +
                "/" +
                gameManager.getMaxTeamSize(),
                NamedTextColor.GREEN
            )
        );
        player.sendMessage(
            Component.text("Team Members:", NamedTextColor.GREEN)
        );

        bingoTeam
            .getPlayers()
            .forEach(teammate -> {
                String status = teammate.isOnline() ? "[Online]" : "[Offline]";
                NamedTextColor statusColor = teammate.isOnline()
                    ? NamedTextColor.GREEN
                    : NamedTextColor.RED;
                player.sendMessage(
                    Component.text(
                        "  - " + teammate.getName() + " " + status,
                        statusColor
                    )
                );
            });

        return true;
    }

    private void sendHelpMessage(Player player) {
        player.sendMessage(
            Component.text("=== Bingo Team Commands ===", NamedTextColor.GREEN)
        );
        player.sendMessage(
            Component.text(
                "/bingo team create - Create a new team",
                NamedTextColor.GREEN
            )
        );
        player.sendMessage(
            Component.text(
                "/bingo team join <teamId> - Join an existing team",
                NamedTextColor.GREEN
            )
        );
        player.sendMessage(
            Component.text(
                "/bingo team leave - Leave your current team",
                NamedTextColor.GREEN
            )
        );
        player.sendMessage(
            Component.text(
                "/bingo team list - List available teams",
                NamedTextColor.GREEN
            )
        );
        player.sendMessage(
            Component.text(
                "/bingo team info - Show your team information",
                NamedTextColor.GREEN
            )
        );
        player.sendMessage(
            Component.text(
                "/bingo team select - Select your team with gui",
                NamedTextColor.GREEN
            )
        );
    }

    @Override
    public @Nullable List<String> getTabCompletions(
        @NotNull CommandSender sender,
        @NotNull String[] args
    ) {
        if (args.length == 1) {
            return Arrays.asList(
                "create",
                "join",
                "leave",
                "list",
                "info",
                "select"
            );
        }
        BingoGameManager gameManager =
            BingoPlugin.getInstance().getGameManager();

        if (args.length == 2 && args[0].equalsIgnoreCase("join")) {
            List<String> teamNames = gameManager
                .getTeams()
                .stream()
                .map(team -> team.getName())
                .collect(Collectors.toList());
            return teamNames;
        }

        return new ArrayList<>();
    }
}
