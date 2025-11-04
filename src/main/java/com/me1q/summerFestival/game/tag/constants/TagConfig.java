package com.me1q.summerFestival.game.tag.constants;

public enum TagConfig {
    MIN_PLAYERS(2),
    MIN_DURATION_SECONDS(30),
    BLINDNESS_DURATION_TICKS(60),
    SLOWNESS_DURATION_TICKS(60),
    SLOWNESS_AMPLIFIER(10),
    VICTORY_DISPLAY_DELAY_TICKS(100);

    private final int value;

    TagConfig(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }
}

