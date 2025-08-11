package com.bingaso.bingo.command;

import com.bingaso.bingo.BingoPlugin;
import com.bingaso.bingo.game.GameState;
import com.bingaso.bingo.gui.ConfigGui;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Command handler for the Bingo configuration command.
 * This command opens the configuration GUI that allows server operators
 * to modify game settings while in the lobby state.
 */
public class BingoConfigCommand implements CommandExecutor {

    @Override
    public boolean onCommand(
        CommandSender sender,
        Command command,
        String label,
        String[] args
    ) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;

        if (!player.isOp()) {
            player.sendMessage(
                "§cYou do not have permission to use this command."
            );
            return true;
        }

        if (
            BingoPlugin.getInstance().getGameManager().getCurrentState() !=
            GameState.LOBBY
        ) {
            player.sendMessage(
                "§cMatch settings can only be changed while in the lobby."
            );
            return true;
        }

        ConfigGui.getInstance().openForPlayer(
            player,
            BingoPlugin.getInstance().getGameManager().getCurrentMatchSettings()
        );
        return true;
    }
}
