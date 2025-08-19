package com.bingaso.bingo.gui;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import de.rapha149.signgui.SignGUI;
import de.rapha149.signgui.SignGUIAction;
import de.rapha149.signgui.exception.SignGUIVersionException;

public class BingoTextCaptureGui {

    private CompletableFuture<String> resultFuture;
    public static HashMap<Player, BingoTextCaptureGui> activeCaptures = new HashMap<>();

    public BingoTextCaptureGui(Player player) {
        activeCaptures.put(player, this);
        this.resultFuture = new CompletableFuture<>();
    }
    /**
     * Opens a sign GUI for a player with a pre-populated sign
     *
     * @param player      The player who will see the sign GUI.
     * @param initialText The text that will appear on the Sign at first.
     */
    public void open(Player player, String initialText) {
        try {
            SignGUI gui = SignGUI.builder()
                .setLine(1, "Enter above")
                .setLine(2, initialText)
                .setType(Material.OAK_SIGN)
                .setHandler((p, result) -> {
                    String playerInput = result.getLineWithoutColor(0);
                    // The user has not entered anything on line 1, so we open
                    // the sign again
                    if (playerInput.isEmpty()) {
                        return List.of(SignGUIAction.displayNewLines(null, "Enter above", initialText));
                    }
                    // Store the result and complete the future
                    resultFuture.complete(playerInput);
                    // Clean up the active capture
                    activeCaptures.remove(p);
                    // Close the sign by not returning any actions
                    return Collections.emptyList();
            }).build();
            gui.open(player);
        } catch (SignGUIVersionException e) {
            // This error is thrown if SignGUI does not support this server version (yet).
            resultFuture.completeExceptionally(e);
            activeCaptures.remove(player);
        }
    }
    
    public CompletableFuture<String> getResultFuture() {
        return resultFuture;
    }
}
