package com.bingaso.bingo;

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
        getLogger().info("Me encendi jajaj");
    }

    @Override
    public void onDisable() {
        getLogger().info("Me apague jojoj");
    }
}