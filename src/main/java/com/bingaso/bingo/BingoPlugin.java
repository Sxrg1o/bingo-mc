package com.bingaso.bingo;

import com.bingaso.bingo.card.BingoCardGuiListener;
import com.bingaso.bingo.command.BingoCommand;
import com.bingaso.bingo.match.BingoMatch;
import com.bingaso.bingo.match.BingoMatchListener;
import com.bingaso.bingo.match.BingoMatchSettingsGuiListener;
import com.bingaso.bingo.team.select.BingoTeamSelectGuiListener;

import org.bukkit.plugin.java.JavaPlugin;

public final class BingoPlugin extends JavaPlugin {

    private static BingoPlugin INSTANCE;

    public static BingoPlugin getInstance() {
        return INSTANCE;
    }

    private BingoMatch bingoMatch;

    @Override
    public void onEnable() {
        INSTANCE = this;
        getLogger().info("Bingo plugin enabled!");

        // Register gui listeners
        getServer()
            .getPluginManager()
            .registerEvents(new BingoCardGuiListener(), this);
        getServer()
            .getPluginManager()
            .registerEvents(new BingoTeamSelectGuiListener(), this);
        getServer()
            .getPluginManager()
            .registerEvents(new BingoMatchSettingsGuiListener(), this);

        // Register match listener
        getServer().getPluginManager().registerEvents(new BingoMatchListener(), this);

        // Register bingo command
        getCommand("bingo").setExecutor(new BingoCommand());
        getCommand("bingo").setTabCompleter(new BingoCommand());

        bingoMatch = new BingoMatch();
    }

    @Override
    public void onDisable() {
        getLogger().info("Bingo plugin disabled!");
    }

    public BingoMatch getBingoMatch() {
        return bingoMatch;
    }
}
