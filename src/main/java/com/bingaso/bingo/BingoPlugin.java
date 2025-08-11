package com.bingaso.bingo;

import com.bingaso.bingo.command.TeamCommand;
import com.bingaso.bingo.listener.BingoPlayerListener;
import com.bingaso.bingo.game.CardGenerator;
import com.bingaso.bingo.game.GameManager;
import com.bingaso.bingo.utils.Broadcaster;
import com.bingaso.bingo.utils.ItemRepository;
import org.bukkit.plugin.java.JavaPlugin;

public final class BingoPlugin extends JavaPlugin {

    private static BingoPlugin instance;

    public static BingoPlugin getInstance() {
        return instance;
    }

    private ItemRepository itemRepository;
    private CardGenerator cardGenerator;
    private Broadcaster broadcaster;
    private GameManager gameManager;

    @Override
    public void onEnable() {
        getLogger().info("Bingo plugin enabled!");
        
        // Register event listeners
        getServer().getPluginManager().registerEvents(new BingoPlayerListener(), this);
        
        // Register commands
        getCommand("team").setExecutor(new TeamCommand());
        getCommand("team").setTabCompleter(new TeamCommand());
        
        getLogger().info("Team system initialized successfully!");
        instance = this;

        this.itemRepository = new ItemRepository();
        this.broadcaster = new Broadcaster();
        this.cardGenerator = new CardGenerator(this.itemRepository);
        this.gameManager = new GameManager(
            this.cardGenerator,
            this.broadcaster
        );

        getLogger().info("Me encendi jajaj");
    }

    @Override
    public void onDisable() {
        getLogger().info("Bingo plugin disabled!");
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public Broadcaster getBroadcaster() {
        return broadcaster;
    }
}
