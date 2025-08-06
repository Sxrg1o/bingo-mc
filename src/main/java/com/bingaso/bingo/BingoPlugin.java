package com.bingaso.bingo;

import org.bukkit.plugin.java.JavaPlugin;

public final class BingoPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("Me encendi jajaj");
    }

    @Override
    public void onDisable() {
        getLogger().info("Me apague jojoj");
    }
}