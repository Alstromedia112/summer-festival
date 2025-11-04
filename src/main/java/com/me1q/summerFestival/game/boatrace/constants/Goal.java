package com.me1q.summerFestival.game.boatrace.constants;

public enum Goal {
    MARKER_NAME("§e§lゴールマーカー"),
    MARKER_LORE("§7右クリックでゴール地点を設置"),
    ARMOR_STAND_NAME("§e§lゴール地点"),
    PDC_KEY_GOAL_OWNER("boatrace_goal_owner");

    private final String text;

    Goal(String text) {
        this.text = text;
    }

    public String text() {
        return text;
    }
}