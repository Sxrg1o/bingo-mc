package com.bingaso.bingo.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import com.bingaso.bingo.BingoPlugin;

public abstract class BingoGlobalScoreboard {
    
    protected BukkitRunnable updateTask;
    protected final Scoreboard scoreboard;

    public BingoGlobalScoreboard() {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        scoreboard = manager.getNewScoreboard();
    }

    public void start(long period) {
        updateTask = new BukkitRunnable() {
            @Override
            public void run() {
                updateScoreboardForAllPlayers();
            }
        };

        updateTask.runTaskTimer(BingoPlugin.getInstance(), 0L, period);
    }

    public void stop() {
        if (updateTask != null) {
            updateTask.cancel();
            updateTask = null;
        }

        Bukkit.getOnlinePlayers().forEach(player ->
            player.setScoreboard(
                Bukkit.getScoreboardManager().getMainScoreboard()
            )
        );
    }

    public boolean isActive() {
        return updateTask != null && !updateTask.isCancelled();
    }

    protected void updateScoreboardForAllPlayers() {
        updateScoreboard();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getScoreboard()
                == Bukkit.getScoreboardManager().getMainScoreboard()) {
                player.setScoreboard(scoreboard);
            }
        }
    }

    public abstract void updateScoreboard();
}
