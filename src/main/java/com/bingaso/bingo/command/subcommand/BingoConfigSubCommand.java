package com.bingaso.bingo.command.subcommand;

import com.bingaso.bingo.BingoPlugin;
import com.bingaso.bingo.command.BingoCommand.BingoSubCommand;
import com.bingaso.bingo.game.BingoGameManager.GameState;
import com.bingaso.bingo.game.MatchSettings;
import com.bingaso.bingo.gui.BingoConfigGui;
import com.bingaso.bingo.gui.BingoConfigGui.ConfigGuiContext;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Command handler for the Bingo configuration command.
 * This command opens the configuration GUI that allows server operators
 * to modify game settings while in the lobby state.
 */
public class BingoConfigSubCommand implements BingoSubCommand {

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String[] args) {
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
        
        MatchSettings settings = BingoPlugin.getInstance()
            .getGameManager()
            .getCurrentMatchSettings();

        BingoConfigGui.getInstance().openForPlayer(
            player,
            new ConfigGuiContext(settings)
        );
        return true;
    }

    @Override
    public @Nullable List<String> getTabCompletions(@NotNull CommandSender sender, @NotNull String[] args) {
        // TODO Auto-generated method stub
        return null;
    }
}
