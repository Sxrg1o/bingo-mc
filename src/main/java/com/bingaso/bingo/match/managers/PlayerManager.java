package com.bingaso.bingo.match.managers;

import com.bingaso.bingo.player.BingoPlayer;
import com.bingaso.bingo.player.BingoPlayerRepository;
import com.bingaso.bingo.player.BingoPlayerRepository.PlayerAlreadyExistsException;
import com.bingaso.bingo.player.BingoPlayerRepositoryInMemory;
import java.util.List;
import java.util.UUID;
import org.bukkit.entity.Player;

/**
 * Manages players in a Bingo match.
 * <p>
 * This class is responsible for tracking players participating in a bingo match,
 * providing methods to add, remove, and retrieve players.
 * </p>
 *
 * @since 1.0
 */
public class PlayerManager {

    /** Repository for storing and retrieving player data */
    private final BingoPlayerRepository playerRepository =
        new BingoPlayerRepositoryInMemory();

    /**
     * Adds a player to the match.
     * <p>
     * Creates a new BingoPlayer instance from the Bukkit player and stores it in the repository.
     * If the player already exists in the repository, the exception is caught but not propagated.
     * </p>
     *
     * @param player The Bukkit player to add to the match
     */
    public void addPlayer(Player player) {
        BingoPlayer bingoPlayer = new BingoPlayer(player);
        try {
            playerRepository.save(bingoPlayer);
        } catch (PlayerAlreadyExistsException e) {
            // Log or handle exception as needed
        }
    }

    /**
     * Removes a player from the match.
     * <p>
     * Finds the player in the repository by UUID and removes them if found.
     * </p>
     *
     * @param player The Bukkit player to remove from the match
     */
    public void removePlayer(Player player) {
        BingoPlayer bingoPlayer = playerRepository.findByUUID(
            player.getUniqueId()
        );
        if (bingoPlayer != null) {
            playerRepository.remove(bingoPlayer);
        }
    }

    /**
     * Gets a BingoPlayer by their UUID.
     *
     * @param uuid The UUID of the player to find
     * @return The BingoPlayer, or null if not found
     */
    public BingoPlayer getBingoPlayer(UUID uuid) {
        return playerRepository.findByUUID(uuid);
    }

    /**
     * Gets all players currently in the match.
     *
     * @return A list of all BingoPlayers in the match
     */
    public List<BingoPlayer> getAllPlayers() {
        return playerRepository.findAll();
    }

    /**
     * Gets the player repository used by this manager.
     *
     * @return The player repository
     */
    public BingoPlayerRepository getPlayerRepository() {
        return playerRepository;
    }

    /**
     * Clears all players from the repository.
     * <p>
     * This is typically called at the end of a match.
     * </p>
     */
    public void clear() {
        playerRepository.clear();
    }
}
