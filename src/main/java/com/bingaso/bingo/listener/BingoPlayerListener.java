package com.bingaso.bingo.listener;

import com.bingaso.bingo.model.BingoPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class BingoPlayerListener implements Listener {
    
    /**
     * Handles player join events by creating a BingoPlayer instance.
     *
     * @param event The PlayerJoinEvent
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        // Check if BingoPlayer already exists
        BingoPlayer existingBingoPlayer = BingoPlayer.getBingoPlayer(player.getUniqueId());
        
        if (existingBingoPlayer == null) {
            // Create new BingoPlayer
            new BingoPlayer(player);
        }
    }
    
    /**
     * Handles player quit events by cleaning up the BingoPlayer instance.
     *
     * @param event The PlayerQuitEvent
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        BingoPlayer bingoPlayer = BingoPlayer.getBingoPlayer(player.getUniqueId());
        
        if (bingoPlayer != null) {
            bingoPlayer.cleanup();
        }
    }
}
