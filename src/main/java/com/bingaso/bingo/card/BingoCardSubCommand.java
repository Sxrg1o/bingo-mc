package com.bingaso.bingo.card;

import com.bingaso.bingo.BingoPlugin;
import com.bingaso.bingo.card.BingoCardGui.BingoCardGuiContext;
import com.bingaso.bingo.command.BingoSubCommand;
import com.bingaso.bingo.match.BingoMatch;
import com.bingaso.bingo.team.BingoTeam;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.List;

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
    public boolean execute(@NotNull CommandSender sender, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Component.text("This command can only be used by players.", NamedTextColor.RED));
            return true;
        }
        
        Player player = (Player) sender;
        
        if (args.length > 0) {
            sendHelpMessage(player);
            return true;
        }

        BingoMatch gameManager = BingoPlugin.getInstance().getBingoMatch();
        BingoTeam bingoTeamFromPlayer = gameManager.getBingoTeamFromPlayer(player);
        BingoTeam bingoTeamToShow = null;

        // if player has no team then it shows arbitrarely any team
        if(bingoTeamFromPlayer != null) bingoTeamToShow = bingoTeamFromPlayer;
        if(!gameManager.getBingoTeamRepository().isEmpty()) {
            bingoTeamToShow = gameManager.getBingoTeamRepository().findAll().getFirst();
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

    @Override
    public @Nullable List<String> getTabCompletions(@NotNull CommandSender sender, @NotNull String[] args) {
        // TODO Auto-generated method stub
        return null;
    }

    private void sendHelpMessage(Player player) {
        player.sendMessage(Component.text("=== Bingo Card Commands ===", NamedTextColor.GREEN));
        player.sendMessage(Component.text("/bingo card - Shows the bingo card GUI", NamedTextColor.GREEN));
    }
}
