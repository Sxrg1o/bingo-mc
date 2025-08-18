package com.bingaso.bingo.player;

import java.util.List;
import java.util.UUID;

public interface BingoPlayerRepositoryReadOnly {
    /**
     * Find all {@link BingoPlayer}s in the repository.
     * 
     * @return An immutable list of all {@link BingoPlayer}s.
     * @since 1.0
     */
    public List<BingoPlayer> findAll();

    /**
     * Finds a {@link BingoPlayer} by its uuid.
     * 
     * @param uuid The {@link UUID} of the {@link BingoPlayer}.
     * @return The {@link BingoPlayer} with the given uuid, or null if not
     * found.
     * @since 1.0
     */
    public BingoPlayer findByUUID(UUID uuid);

    /**
     * Checks if a {@link BingoPlayer} with the specified {@link UUID} exists
     * in the repository.
     * 
     * @param uuid The {@link UUID} to check.
     * @return true if a player with this uuid exists, false otherwise.
     * @since 1.0
     */
    public boolean existsByUUID(UUID uuid);
}
