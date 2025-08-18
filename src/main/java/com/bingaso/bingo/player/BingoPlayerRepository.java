package com.bingaso.bingo.player;

import java.util.UUID;

import org.jetbrains.annotations.NotNull;

public interface BingoPlayerRepository extends BingoPlayerRepositoryReadOnly {

    /**
     * Thrown when attempting to create a player with a UUID that already exists.
     * 
     * @since 1.0
     */
    public static class PlayerAlreadyExistsException extends Exception {
        /** 
         * @param uuid The UUID that already exists.
         */
        public PlayerAlreadyExistsException(UUID uuid) {
            super("A player with the UUID '" + uuid + "' already exists.");
        }
    }

    /**
     * Saves a {@link BingoPlayer} in the repository.
     * 
     * @param bingoPlayer The {@link BingoPlayer} to save.
     * @throws PlayerAlreadyExistsException if a {@link BingoPlayer} with the
     * same {@link UUID} already exists.
     * @since 1.0
     */
    public void save(@NotNull BingoPlayer bingoPlayer)
        throws PlayerAlreadyExistsException;

    /**
     * Removes a {@link BingoPlayer} from the repository.
     * 
     * @param bingoPlayer The {@link BingoPlayer} to remove.
     * @return True if the player was removed, false if it didn't exist in the
     * repository.
     * @since 1.0
     */
    public boolean remove(@NotNull BingoPlayer bingoPlayer);
}