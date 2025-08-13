package com.bingaso.bingo.listener;

import com.bingaso.bingo.BingoPlugin;
import com.bingaso.bingo.game.BingoGameManager;
import com.bingaso.bingo.game.BingoGameManager.GameState;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class BingoPlayerManagerListener implements Listener {
    
    /**
     * Handles player join events by creating a BingoPlayer instance.
     *
     * @param event The PlayerJoinEvent
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        // Only add players in survival into the bingo game
        if(player.getGameMode() != GameMode.SURVIVAL) return;

        BingoGameManager gameManager = BingoPlugin.getInstance().getGameManager();

        // Only add players if the bingo game is in lobby state
        if(gameManager.getCurrentState() != GameState.LOBBY) return;
        // Add bingo player
        gameManager.addPlayer(player);
    }
    
    /**
     * Handles player quit events by cleaning up the BingoPlayer instance.
     *
     * @param event The PlayerQuitEvent
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        // Only remove players in survival into the bingo game
        if(player.getGameMode() != GameMode.SURVIVAL) return;

        BingoGameManager gameManager = BingoPlugin.getInstance().getGameManager();

        // Only remove players if the bingo game is in lobby state
        if(gameManager.getCurrentState() != GameState.LOBBY) return;
        // Remove bingo player
        gameManager.removeBingoPlayer(player);
    }

    /**
     * Handles player GameMode change events.
     * Effectively removing the BingoPlayer if the player is no longer in
     * survival, and adding it back if the player returns to survival.
     */
}
