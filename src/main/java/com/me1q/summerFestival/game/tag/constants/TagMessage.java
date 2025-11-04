package com.me1q.summerFestival.game.tag.constants;

public enum TagMessage {
    ALREADY_STARTED("すでに開始されています！"),
    ALREADY_RECRUITING("すでに募集中です！"),
    NO_RECRUITMENT("現在募集は行われていません。"),
    NOT_STARTED("増え鬼は開始されていません。"),
    RECRUITMENT_CANCELLED("募集をキャンセルしました。"),
    MIN_PLAYERS_ERROR("プレイヤーが足りません！最低2人必要です。"),
    MIN_DURATION_ERROR("時間を30秒以上で指定してください。"),
    INVALID_NUMBER("数値を正しく入力してください。"),
    START_RECRUITMENT_FIRST("先に /tag recruit で募集を開始してください。"),
    GAME_STOPPED("増え鬼を終了しました。"),
    PLAYER_JOINED("増え鬼に参加しました！"),
    ALREADY_JOINED("すでに参加しています！"),
    PLAYER_QUIT("がゲームから退出しました。"),
    PLAYER_CAUGHT("が鬼に捕まった！"),
    REMAINING_TIME("残り時間: "),
    CAPTURED_TITLE("捕まった"),
    BECAME_TAGGER("あなたは鬼になった"),
    GAME_START("GAME START"),
    GAME_END("GAME END"),
    TAGGER_WIN("✞ 鬼の勝利 ✞"),
    RUNNER_WIN("✞ 逃げ側の勝利 ✞"),
    RECRUITMENT_HEADER("増え鬼参加者募集中！"),
    JOIN_BUTTON("[参加する]"),
    JOIN_BUTTON_HOVER("クリックして参加"),
    GAME_DURATION("ゲーム時間: ");

    private final String text;

    TagMessage(String text) {
        this.text = text;
    }

    public String text() {
        return text;
    }
}

