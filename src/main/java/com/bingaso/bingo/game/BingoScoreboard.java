package com.bingaso.bingo.game;

import com.bingaso.bingo.BingoPlugin;
import com.bingaso.bingo.model.BingoTeam;
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

public class BingoScoreboard {

    private final GameManager gameManager;
    private BukkitRunnable updateTask;

    public BingoScoreboard(GameManager gameManager) {
        this.gameManager = gameManager;
    }

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

    private void updateScoreboardForAllPlayers() {
        List<Player> players = gameManager.getOnlinePlayersInMatch();
        for (Player player : players) {
            updatePlayerScoreboard(player);
        }
    }

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
                NamedTextColor.WHITE
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

    private String legacy(Component component) {
        return net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacySection().serialize(
            component
        );
    }
}
