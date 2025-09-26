package com.bingaso.bingo.match.managers;

import com.bingaso.bingo.BingoPlugin;
import com.bingaso.bingo.match.BingoMatch;
import com.bingaso.bingo.match.BingoMatchSettings;
import com.bingaso.bingo.team.BingoTeam;
import com.bingaso.bingo.utils.Broadcaster;
import java.time.Instant;
import java.util.List;
import org.bukkit.scheduler.BukkitTask;

public class MatchLifecycleManager {

    private BingoMatch.State state = BingoMatch.State.LOBBY;
    private Instant startInstant;
    private BukkitTask endGameTask;

    private final Broadcaster broadcaster = new Broadcaster();
    private final BingoMatchSettings settings;

    public MatchLifecycleManager(BingoMatchSettings settings) {
        this.settings = settings;
    }

    public void start(Runnable onTimedEnd) {
        state = BingoMatch.State.IN_PROGRESS;
        startInstant = Instant.now();

        if (settings.getGameMode() == BingoMatchSettings.GameMode.TIMED) {
            long durationInTicks = settings.getGameDuration() * 60 * 20L;
            endGameTask = BingoPlugin.getInstance()
                .getServer()
                .getScheduler()
                .runTaskLater(
                    BingoPlugin.getInstance(),
                    onTimedEnd,
                    durationInTicks
                );
        }

        broadcaster.announceStart();
    }

    public void end(List<BingoTeam> winners) {
        if (state != BingoMatch.State.IN_PROGRESS) return;

        state = BingoMatch.State.FINISHING;
        broadcaster.announceWinners(winners);

        if (endGameTask != null) {
            endGameTask.cancel();
            endGameTask = null;
        }

        state = BingoMatch.State.LOBBY;
    }

    public BingoMatch.State getState() {
        return state;
    }

    public Instant getStartInstant() {
        return startInstant;
    }
}
