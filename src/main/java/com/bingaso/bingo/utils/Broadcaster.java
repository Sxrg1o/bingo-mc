package com.bingaso.bingo.utils;

import com.bingaso.bingo.model.BingoTeam;
import java.util.List;
import java.util.stream.Collectors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class Broadcaster {

    private final Component prefix = Component.text(
        "[BINGO] ",
        NamedTextColor.RED,
        TextDecoration.BOLD
    );

    public void announce(Component message) {
        Bukkit.broadcast(prefix.append(message));
    }

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

    public void announceItemFound(BingoTeam team, Material item) {
        Component message = Component.text(team.getName(), NamedTextColor.AQUA)
            .append(Component.text(" team", NamedTextColor.GRAY))
            .append(Component.text(" has found ", NamedTextColor.GRAY))
            .append(Component.text(item.name(), NamedTextColor.GREEN));

        announce(message);
    }

    public void sendMessage(Player player, Component message) {
        player.sendMessage(prefix.append(message));
    }

    public void sendError(Player player, String errorMessage) {
        Component message = Component.text(
            errorMessage,
            NamedTextColor.DARK_RED
        );
        sendMessage(player, message);
    }

    public void announceStart() {
        Component message = Component.text(
            "Bingo game has started!",
            NamedTextColor.GOLD
        );
        announce(message);
    }
}
