package com.bingaso.bingo.quest;

/**
 * Represents an advancement quest on a Bingo card that players need to achieve during a game.
 * 
 * This class is serializable to allow saving and loading of bingo cards.
 * 
 * @since 1.0
 */
public class BingoQuestAdvancement extends BingoQuest {

    private static final long serialVersionUID = 2L;

    private final String achievementKey;

    /**
     * Creates a new QuestAdvancement for an achievement.
     *
     * @param achievementKey The achievement identifier key
     * @since 1.0
     */
    public BingoQuestAdvancement(String achievementKey) {
        super(achievementKey);
        this.achievementKey = achievementKey;
    }

    /**
     * Gets the achievement key for this advancement.
     *
     * @return The achievement key
     * @since 1.0
     */
    public String getAchievementKey() {
        return achievementKey;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        BingoQuestAdvancement questAdvancement = (BingoQuestAdvancement) obj;
        return achievementKey != null ? achievementKey.equals(questAdvancement.achievementKey) : questAdvancement.achievementKey == null;
    }

    @Override
    public int hashCode() {
        return achievementKey != null ? achievementKey.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "QuestAdvancement{achievementKey='" + achievementKey + "'}";
    }
}
