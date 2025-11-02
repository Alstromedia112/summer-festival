package com.me1q.summerFestival.game.boatrace.constants;

public enum Message {
    ALREADY_RECRUITING("すでに募集中です"),
    RACE_IN_PROGRESS("レースが進行中です"),
    SET_GOAL_FIRST("ゴール地点を設定してください"),
    MIN_PLAYERS_ERROR("定員は2人以上にしてください"),
    MAX_PLAYERS_ERROR("定員は20人以下にしてください"),
    NO_RECRUITMENT("現在募集していません"),
    NOT_RECRUITING("募集していません"),
    ONLY_ORGANIZER_CAN_CANCEL("募集を開始したプレイヤーのみキャンセルできます"),
    START_RECRUITMENT_FIRST("募集を開始してください"),
    ONLY_ORGANIZER_CAN_START("募集を開始したプレイヤーのみレースを開始できます"),
    MIN_PARTICIPANTS_REQUIRED("参加者が2人以上必要です"),
    GOAL_NOT_SET("ゴール地点が設定されていません"),
    NO_RACE_IN_PROGRESS("レースが進行していません"),
    NOT_PARTICIPANT("レースに参加していないため、停止できません"),
    GOAL_MARKER_OBTAINED("ゴールマーカーを入手しました"),
    MARKERS_FULL("既に2つのゴールマーカーが設置されています"),
    USE_CLEARGOAL("/boatrace cleargoal で削除してください"),
    MARKER_1_PLACED("開始地点を設置しました 続けて終了地点を設置してください"),
    MARKER_2_PLACED("終了地点を設置しました"),
    GOAL_LINE_COMPLETE("ゴールラインが完成しました！"),
    ALREADY_JOINED("すでに参加しています！"),
    RECRUITMENT_CANCELLED("募集がキャンセルされました。"),
    RACE_STARTED("ボートレース開始！"),
    COUNTDOWN_WARNING("カウントダウン後にスタートします"),
    START_TEXT("スタート！"),
    RACE_RESULTS("   レース結果"),
    ALL_MARKERS_REMOVED("すべてのゴールマーカーを削除しました");

    private final String text;

    Message(String text) {
        this.text = text;
    }

    public String text() {
        return text;
    }
}
