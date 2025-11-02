package com.me1q.summerFestival.game.boatrace.constants;

public enum Hitbox {
    HALF_WIDTH(0.5),
    MIN_Y_OFFSET(-1.0),
    MAX_Y_OFFSET(1.0);

    private final double value;

    Hitbox(double value) {
        this.value = value;
    }

    public double value() {
        return value;
    }
}
