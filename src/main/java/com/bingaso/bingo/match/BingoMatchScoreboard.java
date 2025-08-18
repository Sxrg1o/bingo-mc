package com.bingaso.bingo.match;

import com.bingaso.bingo.scoreboard.BingoGlobalScoreboard;
import com.bingaso.bingo.team.BingoTeam;

import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;

/**
 * Manages the in-game scoreboard display for Bingo matches.
 * This class handles creating, updating, and removing scoreboards that show
 * game information such as elapsed time and team scores to all players.
 */
public class BingoMatchScoreboard extends BingoGlobalScoreboard {

    private final BingoMatch bingoMatch;

    /**
     * Creates a new BingoScoreboard with the specified game manager.
     *
     * @param gameManager The game manager that provides game state information
     */
    public BingoMatchScoreboard(BingoMatch gameManager) {
        super();
        this.bingoMatch = gameManager;
    }

    /**
     * Shows game time and team scores sorted by number of found items.
     *
     * @param player The player whose scoreboard should be updated
     */
    @Override
    public void updateScoreboard() {
        Objective objective = scoreboard.getObjective("bingo");
        if (objective == null) {
            objective = scoreboard.registerNewObjective(
                "bingo",
                Criteria.DUMMY,
                Component.text(
                    "BINGO",
                    NamedTextColor.AQUA,
                    TextDecoration.BOLD
                )
            );
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        }

        for (String entry : scoreboard.getEntries()) {
            scoreboard.resetScores(entry);
        }

        long elapsedSeconds = bingoMatch.getMatchDurationSeconds(Instant.now());
        Duration duration = Duration.ofSeconds(elapsedSeconds);
        String timeFormatted = String.format(
            "%02d:%02d:%02d",
            duration.toHoursPart(),
            duration.toMinutesPart(),
            duration.toSecondsPart()
        );

        objective.getScore("§eTime: §f" + timeFormatted).setScore(15);
        objective.getScore("§r").setScore(14);

        List<BingoTeam> sortedTeams
            = bingoMatch.getBingoTeamRepository().findAll();
        sortedTeams.sort(
            Comparator.comparingInt((BingoTeam team) ->
                team.getCompletedQuests().size()
            ).reversed()
        );

        int scoreIndex = 13;
        for (BingoTeam team : sortedTeams) {
            Component teamLine = Component.text(
                team.getName() + ": " + team.getCompletedQuests().size(),
                team.getColor()
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
