package com.bingaso.bingo.match.managers;

import com.bingaso.bingo.BingoPlugin;
import com.bingaso.bingo.match.BingoMatch;
import com.bingaso.bingo.match.BingoMatchSettings;
import com.bingaso.bingo.team.BingoTeam;
import com.bingaso.bingo.utils.Broadcaster;
import java.time.Instant;
import java.util.List;
import org.bukkit.scheduler.BukkitTask;

/**
 * Manages the lifecycle of a Bingo match, including start and end states.
 * <p>
 * This class is responsible for tracking the state of the match, handling
 * the timing for timed matches, and coordinating the transition between states.
 * </p>
 *
 * @since 1.0
 */
public class MatchLifecycleManager {

    /** Current state of the match */
    private BingoMatch.State state = BingoMatch.State.LOBBY;
    /** Timestamp when the match started */
    private Instant startInstant;
    /** Scheduled task to end the match (for timed matches) */
    private BukkitTask endGameTask;

    /** Utility for broadcasting messages */
    private final Broadcaster broadcaster = new Broadcaster();
    /** Match settings configuration */
    private final BingoMatchSettings settings;

    /**
     * Creates a new match lifecycle manager with the specified settings.
     *
     * @param settings The match settings to use for configuring the match lifecycle
     */
    public MatchLifecycleManager(BingoMatchSettings settings) {
        this.settings = settings;
    }

    /**
     * Starts the match.
     * <p>
     * This method:
     * <ul>
     *   <li>Sets the match state to IN_PROGRESS</li>
     *   <li>Records the start time</li>
     *   <li>For timed matches, schedules the end game event</li>
     *   <li>Broadcasts a match start announcement</li>
     * </ul>
     * </p>
     *
     * @param onTimedEnd Callback to execute when a timed match ends naturally
     */
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

    /**
     * Ends the match and announces the winners.
     * <p>
     * This method:
     * <ul>
     *   <li>Verifies the match is in progress before ending</li>
     *   <li>Sets the state to FINISHING, then to LOBBY</li>
     *   <li>Broadcasts the winners</li>
     *   <li>Cancels any scheduled end-game task</li>
     * </ul>
     * </p>
     *
     * @param winners List of teams that won the match
     */
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

    /**
     * Gets the current state of the match.
     *
     * @return The current match state
     */
    public BingoMatch.State getState() {
        return state;
    }

    /**
     * Gets the timestamp when the match started.
     *
     * @return The start time of the match, or null if the match hasn't started
     */
    public Instant getStartInstant() {
        return startInstant;
    }
}
