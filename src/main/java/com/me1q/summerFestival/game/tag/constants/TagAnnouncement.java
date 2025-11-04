package com.me1q.summerFestival.game.tag.constants;

public final class TagAnnouncement {

    public static final int[] TIME_ANNOUNCEMENTS = {30, 10, 5, 4, 3, 2, 1};

    private TagAnnouncement() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static boolean shouldAnnounceTime(int timeRemaining) {
        for (int time : TIME_ANNOUNCEMENTS) {
            if (timeRemaining == time) {
                return true;
            }
        }
        return false;
    }
}

