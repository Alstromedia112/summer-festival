package com.me1q.summerFestival.game.shooting.session;

import net.kyori.adventure.text.format.NamedTextColor;

public enum ScoreRank {
    LEGENDARY(100, "どうやってここまで？", NamedTextColor.DARK_PURPLE),
    EXCELLENT(50, "素晴らしい！", NamedTextColor.BLUE),
    GREAT(20, "よくできました！", NamedTextColor.LIGHT_PURPLE),
    GOOD(15, "いい感じです！", NamedTextColor.GREEN),
    NORMAL(10, "まずまずです！", NamedTextColor.GOLD),
    BEGINNER(0, "練習あるのみです！", NamedTextColor.RED);

    private final int threshold;
    private final String message;
    private final NamedTextColor color;

    ScoreRank(int threshold, String message, NamedTextColor color) {
        this.threshold = threshold;
        this.message = message;
        this.color = color;
    }

    public String getMessage() {
        return message;
    }

    public NamedTextColor getColor() {
        return color;
    }

    public static ScoreRank fromScore(int score) {
        for (ScoreRank rank : values()) {
            if (score >= rank.threshold) {
                return rank;
            }
        }
        return BEGINNER;
    }
}