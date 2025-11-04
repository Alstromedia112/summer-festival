package com.me1q.summerFestival.game.shooting.constants;

public enum ShootingConfig {
    GAME_DURATION_SECONDS(30),
    TIME_BAR_LENGTH(20),
    ARROW_CLEANUP_DELAY_TICKS(1),
    REQUIRED_COORDINATE_ARGS(7);

    private final int value;

    ShootingConfig(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }
}

