package com.bingaso.bingo.command.subcommand;

import com.bingaso.bingo.BingoPlugin;
import com.bingaso.bingo.command.BingoCommand.BingoSubCommand;
import com.bingaso.bingo.gui.BingoCardGui;
import com.bingaso.bingo.gui.BingoCardGui.BingoCardGuiContext;
import com.bingaso.bingo.model.BingoPlayer;

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

        BingoPlayer bingoPlayer = BingoPlayer.getBingoPlayer(player.getUniqueId());
        BingoCardGui.getInstance().openForPlayer(
            player,
            new BingoCardGuiContext(
                bingoPlayer.getTeam(),
                bingoPlayer.getTeam(),
                BingoPlugin.getInstance().getGameManager().getSharedBingoCard()
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
        player.sendMessage(Component.text("/bingocard - Shows the bingo card GUI", NamedTextColor.GREEN));
    }
}
