package com.me1q.summerFestival.game.shooting.constants;

public final class ShootingAnnouncement {

    public static final int[] TIME_WARNING_SECONDS = {5, 4, 3, 2, 1};

    private ShootingAnnouncement() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static boolean shouldShowTimeWarning(int timeRemaining) {
        for (int warningTime : TIME_WARNING_SECONDS) {
            if (timeRemaining == warningTime) {
                return true;
            }
        }
        return false;
    }
}

