package com.me1q.summerFestival.game.boatrace.banner;

import com.me1q.summerFestival.game.boatrace.constants.BannerColor;

public class PlayerBanner {

    private final BannerColor color;
    private final int number;

    public PlayerBanner(BannerColor color, int number) {
        if (number < 1 || number > BannerColor.PLAYERS_PER_COLOR) {
            throw new IllegalArgumentException(
                "Number must be between 1 and " + BannerColor.PLAYERS_PER_COLOR);
        }
        this.color = color;
        this.number = number;
    }

    public BannerColor getColor() {
        return color;
    }

    public int getNumber() {
        return number;
    }

    @Override
    public String toString() {
        return color.getDisplayName() + number;
    }
}

