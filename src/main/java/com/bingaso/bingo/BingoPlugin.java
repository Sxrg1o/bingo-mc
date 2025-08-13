package com.bingaso.bingo;

import com.bingaso.bingo.command.BingoCommand;
import com.bingaso.bingo.game.BingoGameManager;
import com.bingaso.bingo.listener.BingoCardGuiListener;
import com.bingaso.bingo.listener.BingoPlayerManagerListener;
import com.bingaso.bingo.listener.BingoTeamGuiListener;
import com.bingaso.bingo.listener.BingoConfigGuiListener;
import com.bingaso.bingo.listener.GameListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class BingoPlugin extends JavaPlugin {

    private static BingoPlugin INSTANCE = new BingoPlugin();

    public static BingoPlugin getInstance() {
        return INSTANCE;
    }

    private BingoGameManager gameManager;

    @Override
    public void onEnable() {
        getLogger().info("Bingo plugin enabled!");

        // Register event listeners
        getServer()
            .getPluginManager()
            .registerEvents(new BingoPlayerManagerListener(), this);
        getServer()
            .getPluginManager()
            .registerEvents(new BingoCardGuiListener(), this);
        getServer()
            .getPluginManager()
            .registerEvents(new BingoTeamGuiListener(), this);
        getServer()
            .getPluginManager()
            .registerEvents(new BingoConfigGuiListener(), this);
        getServer().getPluginManager().registerEvents(new GameListener(), this);

        // Register bingo command
        getCommand("bingo").setExecutor(new BingoCommand());
        getCommand("bingo").setTabCompleter(new BingoCommand());

        this.gameManager = new BingoGameManager();
    }

    @Override
    public void onDisable() {
        getLogger().info("Bingo plugin disabled!");
    }

    public BingoGameManager getGameManager() {
        return gameManager;
    }
}
