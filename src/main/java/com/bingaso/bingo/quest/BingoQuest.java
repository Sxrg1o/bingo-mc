package com.bingaso.bingo.quest;

import java.io.Serializable;

/**
 * Interface representing a quest on a Bingo card that players need to complete during a game.
 * Each BingoQuest implementation can track which teams have completed it.
 * 
 * This interface supports various quest types including items, potions, and advancements.
 * 
 * Implementations should be serializable to allow saving and loading of bingo cards.
 * 
 * @since 1.0
 */
public abstract class BingoQuest implements Serializable {

    private String questName;
    BingoQuest(String questName) {
        this.questName = questName;
    }
    
    public String getQuestName() {
        return questName;
    }

    void setQuestName(String questName) {
        this.questName = questName;
    }
}
