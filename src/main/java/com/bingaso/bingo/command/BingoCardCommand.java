package com.bingaso.bingo.command;

import com.bingaso.bingo.BingoPlugin;
import com.bingaso.bingo.gui.BingoCardGui;
import com.bingaso.bingo.gui.BingoCardGui.BingoCardGuiContext;
import com.bingaso.bingo.model.BingoPlayer;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Command executor for bingo card-related commands.
 * Handles: /bingocard
 */
public class BingoCardCommand implements CommandExecutor {
    
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

    private void sendHelpMessage(Player player) {
        player.sendMessage(Component.text("=== Bingo Card Commands ===", NamedTextColor.GREEN));
        player.sendMessage(Component.text("/bingocard - Shows the bingo card GUI", NamedTextColor.GREEN));
    }
}
