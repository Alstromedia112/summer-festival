package com.me1q.summerFestival.game.boatrace.constants;

public enum Config {
    MIN_PLAYERS(2),
    MAX_PLAYERS(20),
    DEFAULT_MAX_PLAYERS(10),
    REQUIRED_MARKERS(2),
    COUNTDOWN_SECONDS(5),
    RESULT_DISPLAY_DELAY_TICKS(100),
    REWARD_FIRST_PLACE(1000),
    REWARD_SECOND_PLACE(700),
    REWARD_THIRD_PLACE(500),
    REWARD_OTHER_PLACE(200);

    private final int value;

    Config(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }
}
