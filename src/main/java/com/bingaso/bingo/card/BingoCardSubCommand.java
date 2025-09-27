package com.bingaso.bingo.card;

import com.bingaso.bingo.BingoPlugin;
import com.bingaso.bingo.card.BingoCardGui.BingoCardGuiContext;
import com.bingaso.bingo.command.BingoSubCommand;
import com.bingaso.bingo.match.BingoMatch;
import com.bingaso.bingo.match.managers.MatchLifecycleManager.State;
import com.bingaso.bingo.team.BingoTeam;
import java.util.Collections;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Command executor for bingo card-related commands.
 * Handles: /bingocard
 */
public class BingoCardSubCommand implements BingoSubCommand {

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
        BingoMatch gameManager = BingoPlugin.getInstance().getBingoMatch();

        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            if (!player.isOp()) {
                player.sendMessage(
                    Component.text("No permissions.", NamedTextColor.RED)
                );
                return true;
            }

            if (gameManager.getState() != State.LOBBY) {
                player.sendMessage(
                    Component.text("You are in a game.", NamedTextColor.RED)
                );
                return true;
            }

            gameManager.generateNewBingoCard();
            player.sendMessage(
                Component.text("Bingo card reloaded.", NamedTextColor.GREEN)
            );
            return true;
        }

        if (args.length == 0) {
            BingoTeam bingoTeamFromPlayer = gameManager.getBingoTeamFromPlayer(
                player
            );
            BingoTeam bingoTeamToShow = null;

            // if player has no team then it shows arbitrarely any team
            if (bingoTeamFromPlayer != null) bingoTeamToShow =
                bingoTeamFromPlayer;
            if (!gameManager.getBingoTeamRepository().isEmpty()) {
                bingoTeamToShow = gameManager
                    .getBingoTeamRepository()
                    .findAll()
                    .getFirst();
            }

            BingoCardGui.getInstance().openForPlayer(
                player,
                new BingoCardGuiContext(
                    bingoTeamToShow,
                    bingoTeamFromPlayer,
                    BingoPlugin.getInstance().getBingoMatch().getBingoCard()
                )
            );
            return true;
        }
        sendHelpMessage(player);

        return true;
    }

    @Override
    public @Nullable List<String> getTabCompletions(
        @NotNull CommandSender sender,
        @NotNull String[] args
    ) {
        if (args.length == 1 && sender.isOp()) {
            return Collections.singletonList("reload");
        }
        return null;
    }

    private void sendHelpMessage(Player player) {
        player.sendMessage(
            Component.text("=== Bingo Card Commands ===", NamedTextColor.GOLD)
        );
        player.sendMessage(
            Component.text("/bingo card", NamedTextColor.GREEN).append(
                Component.text(" - Shows the bingo card GUI.")
            )
        );
        if (player.isOp()) {
            player.sendMessage(
                Component.text(
                    "/bingo card reload",
                    NamedTextColor.AQUA
                ).append(Component.text(" - Generates a new card."))
            );
        }
    }
}
