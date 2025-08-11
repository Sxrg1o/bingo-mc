package com.bingaso.bingo;

import com.bingaso.bingo.command.TeamCommand;
import com.bingaso.bingo.listener.BingoPlayerListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class BingoPlugin extends JavaPlugin {

    // This class is a singleton
    public final static BingoPlugin INSTANCE = new BingoPlugin();
    private BingoPlugin() {}
    public static BingoPlugin getInstance() {
        return INSTANCE;
    }

    @Override
    public void onEnable() {
        getLogger().info("Bingo plugin enabled!");
        
        // Register event listeners
        getServer().getPluginManager().registerEvents(new BingoPlayerListener(), this);
        
        // Register commands
        getCommand("team").setExecutor(new TeamCommand());
        getCommand("team").setTabCompleter(new TeamCommand());
        
        getLogger().info("Team system initialized successfully!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Bingo plugin disabled!");
    }
}