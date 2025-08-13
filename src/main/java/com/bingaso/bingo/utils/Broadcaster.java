package com.bingaso.bingo.utils;

import java.util.List;
import java.util.stream.Collectors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import com.bingaso.bingo.team.BingoTeam;

/**
 * Handles broadcasting messages to players in the Bingo game.
 * This utility class provides methods for sending formatted messages to players,
 * announcing game events like item discoveries and winners, and displaying error messages.
 * All messages are prefixed with a standard Bingo tag for consistency.
 */
public class Broadcaster {

    private final Component prefix = Component.text(
        "[BINGO] ",
        NamedTextColor.RED,
        TextDecoration.BOLD
    );

    /**
     * Broadcasts a message to all online players with the Bingo prefix.
     *
     * @param message The message component to broadcast
     */
    public void announce(Component message) {
        Bukkit.broadcast(prefix.append(message));
    }

    /**
     * Announces the winners of a Bingo game to all players.
     * Handles both single winner and multiple winners (draw) scenarios.
     * Plays a victory sound for all online players.
     *
     * @param winners A list of teams that won the match
     */
    public void announceWinners(List<BingoTeam> winners) {
        if (winners.isEmpty()) {
            announce(
                Component.text(
                    "Match ended without winners. (idk how)",
                    NamedTextColor.GRAY
                )
            );
            return;
        }

        Component winnerMessage;
        if (winners.size() == 1) {
            String winnerName = winners.get(0).getName();
            winnerMessage = Component.text(
                winnerName,
                NamedTextColor.YELLOW
            ).append(Component.text(" team has won!", NamedTextColor.GOLD));
        } else {
            String teamNames = winners
                .stream()
                .map(BingoTeam::getName)
                .collect(Collectors.joining("', '"));

            winnerMessage = Component.text("Draw between ", NamedTextColor.GOLD)
                .append(Component.text(teamNames, NamedTextColor.YELLOW))
                .append(Component.text("'!", NamedTextColor.GOLD));
        }

        announce(winnerMessage);

        Bukkit.getOnlinePlayers().forEach(p ->
            p.playSound(
                p.getLocation(),
                Sound.UI_TOAST_CHALLENGE_COMPLETE,
                1.0f,
                1.0f
            )
        );
    }

    /**
     * Announces when a team finds an item on their Bingo card.
     * The announcement includes the team name and the item found.
     *
     * @param team The team that found the item
     * @param item The material type that was found
     */
    public void announceItemFound(BingoTeam team, Material item) {
        Component message = Component.text(team.getName(), NamedTextColor.AQUA)
            .append(Component.text(" team", NamedTextColor.GRAY))
            .append(Component.text(" has found ", NamedTextColor.GRAY))
            .append(Component.text(item.name(), NamedTextColor.GREEN));

        announce(message);
    }

    /**
     * Sends a prefixed message to a specific player.
     *
     * @param player The player to receive the message
     * @param message The message component to send
     */
    public void sendMessage(Player player, Component message) {
        player.sendMessage(prefix.append(message));
    }

    /**
     * Sends an error message to a specific player.
     * Error messages are displayed in dark red for emphasis.
     *
     * @param player The player to receive the error message
     * @param errorMessage The error message text
     */
    public void sendError(Player player, String errorMessage) {
        Component message = Component.text(
            errorMessage,
            NamedTextColor.DARK_RED
        );
        sendMessage(player, message);
    }

    /**
     * Announces the start of a Bingo game to all players.
     * This is typically called when transitioning to the IN_PROGRESS state.
     */
    public void announceStart() {
        Component message = Component.text(
            "Bingo game has started!",
            NamedTextColor.GOLD
        );
        announce(message);
    }
}
