package com.me1q.summerFestival.games.tag.player;

import net.kyori.adventure.text.format.NamedTextColor;

public enum PlayerRole {
    TAGGER("鬼", NamedTextColor.RED),
    RUNNER("逃走者", NamedTextColor.BLUE);

    private final String displayName;
    private final NamedTextColor color;

    PlayerRole(String displayName, NamedTextColor color) {
        this.displayName = displayName;
        this.color = color;
    }

    public String getDisplayName() {
        return displayName;
    }

    public NamedTextColor getColor() {
        return color;
    }
}

