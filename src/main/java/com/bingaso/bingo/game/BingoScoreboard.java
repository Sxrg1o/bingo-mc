package com.bingaso.bingo.game;

import com.bingaso.bingo.BingoPlugin;
import com.bingaso.bingo.team.BingoTeam;
import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

/**
 * Manages the in-game scoreboard display for Bingo matches.
 * This class handles creating, updating, and removing scoreboards that show
 * game information such as elapsed time and team scores to all players.
 */
public class BingoScoreboard {

    private final BingoGameManager gameManager;
    private BukkitRunnable updateTask;

    /**
     * Creates a new BingoScoreboard with the specified game manager.
     *
     * @param gameManager The game manager that provides game state information
     */
    public BingoScoreboard(BingoGameManager gameManager) {
        this.gameManager = gameManager;
    }

    /**
     * Starts the scoreboard update task.
     * This schedules a repeating task that updates the scoreboard for all players
     * once per second while the game is in progress.
     */
    public void start() {
        this.updateTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (gameManager.getCurrentState() != GameState.IN_PROGRESS) {
                    return;
                }
                updateScoreboardForAllPlayers();
            }
        };

        updateTask.runTaskTimer(BingoPlugin.getInstance(), 0L, 20L);
    }

    /**
     * Stops the scoreboard update task and resets all players to the main scoreboard.
     * This should be called when the game ends or is interrupted.
     */
    public void stop() {
        if (this.updateTask != null) {
            this.updateTask.cancel();
            this.updateTask = null;
        }

        gameManager
            .getOnlinePlayersInMatch()
            .forEach(player ->
                player.setScoreboard(
                    Bukkit.getScoreboardManager().getMainScoreboard()
                )
            );
    }

    /**
     * Updates the scoreboard for all players currently in the match.
     * This method is called periodically while the game is in progress.
     */
    private void updateScoreboardForAllPlayers() {
        List<Player> players = gameManager.getOnlinePlayersInMatch();
        for (Player player : players) {
            updatePlayerScoreboard(player);
        }
    }

    /**
     * Updates the scoreboard display for a specific player.
     * Shows game time and team scores sorted by number of found items.
     *
     * @param player The player whose scoreboard should be updated
     * @deprecated This implementation may be revised in the future
     */
    @Deprecated
    private void updatePlayerScoreboard(Player player) {
        Scoreboard scoreboard = player.getScoreboard();
        if (scoreboard == Bukkit.getScoreboardManager().getMainScoreboard()) {
            ScoreboardManager manager = Bukkit.getScoreboardManager();
            scoreboard = manager.getNewScoreboard();
            player.setScoreboard(scoreboard);
        }

        Objective objective = scoreboard.getObjective("bingo");
        if (objective == null) {
            objective = scoreboard.registerNewObjective(
                "bingo",
                "dummy",
                Component.text(
                    " BINGO ",
                    NamedTextColor.AQUA,
                    TextDecoration.BOLD
                )
            );
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        }

        for (String entry : scoreboard.getEntries()) {
            scoreboard.resetScores(entry);
        }

        long elapsedSeconds = gameManager.getElapsedSeconds();
        Duration duration = Duration.ofSeconds(elapsedSeconds);
        String timeFormatted = String.format(
            "%02d:%02d",
            duration.toMinutesPart(),
            duration.toSecondsPart()
        );

        objective.getScore("§eTime: §f" + timeFormatted).setScore(15);
        objective.getScore("§r").setScore(14);

        List<BingoTeam> sortedTeams = gameManager.getTeams();
        sortedTeams.sort(
            Comparator.comparingInt((BingoTeam team) ->
                team.getFoundItems().size()
            ).reversed()
        );

        int scoreIndex = 13;
        for (BingoTeam team : sortedTeams) {
            Component teamLine = Component.text(
                team.getName() + ": ",
                team.getColor()
            ).append(
                Component.text(
                    team.getFoundItems().size(),
                    NamedTextColor.YELLOW
                )
            );

            objective.getScore(legacy(teamLine)).setScore(scoreIndex);
            scoreIndex--;
        }
    }

    /**
     * Converts an Adventure Component to a legacy string format for use with the scoreboard API.
     *
     * @param component The component to convert
     * @return A legacy string representation of the component
     */
    private String legacy(Component component) {
        return net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacySection().serialize(
            component
        );
    }
}
