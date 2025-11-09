package com.me1q.summerFestival.game.boatrace.constants;

import org.bukkit.DyeColor;

public enum BannerColor {
    RED(DyeColor.RED, "赤"),
    BLUE(DyeColor.BLUE, "青"),
    GREEN(DyeColor.GREEN, "緑"),
    YELLOW(DyeColor.YELLOW, "黄"),
    WHITE(DyeColor.WHITE, "白");

    private final DyeColor dyeColor;
    private final String displayName;

    BannerColor(DyeColor dyeColor, String displayName) {
        this.dyeColor = dyeColor;
        this.displayName = displayName;
    }

    public DyeColor getDyeColor() {
        return dyeColor;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static final int PLAYERS_PER_COLOR = 4;
    public static final int MAX_TOTAL_PLAYERS = values().length * PLAYERS_PER_COLOR;
}

