package com.bingaso.bingo.match.managers;

import com.bingaso.bingo.player.BingoPlayer;
import com.bingaso.bingo.player.BingoPlayerRepository;
import com.bingaso.bingo.player.BingoPlayerRepository.PlayerAlreadyExistsException;
import com.bingaso.bingo.player.BingoPlayerRepositoryInMemory;
import java.util.List;
import java.util.UUID;
import org.bukkit.entity.Player;

public class PlayerManager {

    private final BingoPlayerRepository playerRepository =
        new BingoPlayerRepositoryInMemory();

    public void addPlayer(Player player) {
        BingoPlayer bingoPlayer = new BingoPlayer(player);
        try {
            playerRepository.save(bingoPlayer);
        } catch (PlayerAlreadyExistsException e) {
            // Log or handle exception as needed
        }
    }

    public void removePlayer(Player player) {
        BingoPlayer bingoPlayer = playerRepository.findByUUID(
            player.getUniqueId()
        );
        if (bingoPlayer != null) {
            playerRepository.remove(bingoPlayer);
        }
    }

    public BingoPlayer getBingoPlayer(UUID uuid) {
        return playerRepository.findByUUID(uuid);
    }

    public List<BingoPlayer> getAllPlayers() {
        return playerRepository.findAll();
    }

    public BingoPlayerRepository getPlayerRepository() {
        return playerRepository;
    }

    public void clear() {
        playerRepository.clear();
    }
}
