package com.me1q.summerFestival.game.boatrace.constants;

public enum RecruitmentMode {
    FIRST_COME("先着順"),
    LOTTERY("抽選");

    private final String displayName;

    RecruitmentMode(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static RecruitmentMode fromString(String value) {
        return switch (value.toLowerCase()) {
            case "first", "先着", "先着順" -> FIRST_COME;
            case "lottery", "抽選", "random" -> LOTTERY;
            default -> null;
        };
    }
}
