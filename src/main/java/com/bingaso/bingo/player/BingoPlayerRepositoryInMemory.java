package com.bingaso.bingo.player;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.jetbrains.annotations.NotNull;

/**
 * Manages BingoPlayer instances, providing functionality to create, retrieve,
 * and remove BingoPlayer objects associated with Bukkit Player instances.
 * 
 * This class maintains a mapping between player UUIDs and their corresponding 
 * BingoPlayer instances to ensure consistent player data management throughout
 * the bingo game.
 * @since 1.0
 */
public class BingoPlayerRepositoryInMemory implements BingoPlayerRepository {

    private final HashMap<UUID, BingoPlayer> uuidPlayers = new HashMap<>();
    
    /**
     * Constructs a new BingoPlayerManager with an empty player registry.
     */
    public BingoPlayerRepositoryInMemory() {}

    /**
     * Saves a {@link BingoPlayer} in the hashmap.
     * 
     * @param bingoPlayer The {@link BingoPlayer} to save.
     * @throws PlayerAlreadyExistsException if a {@link BingoPlayer} with the
     * same {@link UUID} already exists.
     * @since 1.0
     */
    @Override
    public void save(@NotNull BingoPlayer bingoPlayer)
        throws PlayerAlreadyExistsException {
        if(existsByUUID(bingoPlayer.getUniqueId())) {
            throw new PlayerAlreadyExistsException(bingoPlayer.getUniqueId());
        }
        uuidPlayers.put(bingoPlayer.getUniqueId(), bingoPlayer);
    }

    /**
     * Removes a {@link BingoPlayer} from the hashmap.
     * 
     * @param bingoPlayer The {@link BingoPlayer} to remove.
     * @return True if the player was removed, false if it didn't exist in the
     * hashmap.
     * @since 1.0
     */
    @Override
    public boolean remove(@NotNull BingoPlayer bingoPlayer) {
        return uuidPlayers.remove(bingoPlayer.getUniqueId()) != null;
    }

    /**
     * Find all {@link BingoPlayer}s in the hashmap.
     * 
     * @return An immutable list of all {@link BingoPlayer}s.
     * @since 1.0
     */
    @Override
    public List<BingoPlayer> findAll() {
        return List.copyOf(uuidPlayers.values());
    }

    /**
     * Finds a {@link BingoPlayer} by its uuid.
     * 
     * @param uuid The {@link UUID} of the {@link BingoPlayer}.
     * @return The {@link BingoPlayer} with the given uuid, or null if not
     * found.
     * @since 1.0
     */
    @Override
    public BingoPlayer findByUUID(UUID uuid) {
        return uuidPlayers.get(uuid);
    }

    /**
     * Checks if a {@link BingoPlayer} with the specified {@link UUID} exists
     * in the hashmap.
     * 
     * @param uuid The {@link UUID} to check.
     * @return true if a player with this uuid exists, false otherwise.
     * @since 1.0
     */
    @Override
    public boolean existsByUUID(UUID uuid) {
        return uuidPlayers.containsKey(uuid);
    }

    /**
     * Clears all {@link BingoPlayer}s mapppings.
     * 
     * @since 1.0
     */
    @Override
    public void clear() {
        uuidPlayers.clear();
    }
}
