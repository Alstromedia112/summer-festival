package com.me1q.summerFestival.game.boatrace.constants;

public final class Messages {

    private Messages() {
    }

    public static String recruitmentStarted(int maxPlayers, boolean participating) {
        return "[BoatRace] 定員: " + maxPlayers + "人 " + (participating ? "（参加）"
            : "（不参加）");
    }

    public static String maxPlayersReached(int maxPlayers) {
        return "定員に達しています（最大" + maxPlayers + "人）";
    }

    public static String recruitmentStopped(int participants) {
        return "募集を終了しました。参加者: " + participants + "人";
    }

    public static String markerDistance(double distance) {
        return String.format("マーカー間の距離: %.2fブロック", distance);
    }

    public static String playerJoined(String playerName, int current, int max) {
        return playerName + " が参加しました (" + current + "/" + max + "人)";
    }

    public static String finishTime(double seconds) {
        return String.format("%.2f秒", seconds);
    }
}
