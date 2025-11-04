package com.me1q.summerFestival.game.shooting.constants;

public enum ShootingMessage {
    ALREADY_IN_GAME("You are already in the game."),
    NOT_IN_GAME("Not currently in the game."),
    GAME_STARTED("射的ゲームを開始します..."),
    GAME_START_TITLE("射的開始！"),
    GAME_DURATION_INFO("30秒間でできるだけ多くの的を射抜こう"),
    FORCE_STOP("Force stop the game..."),
    END_GAME_COMMAND("End the game with: /shooting stop"),
    INVALID_COORDINATES("座標の形式が正しくありません"),
    SPECIFY_COORDINATES("座標範囲を指定してください"),
    PLAYER_ONLY("This command can only be executed by the player."),
    UNKNOWN_SUBCOMMAND("Unknown subcommand: "),
    GAME_IN_PROGRESS("ゲーム進行中"),
    NOT_IN_GAME_JP("現在ゲームに参加していません"),
    FINAL_SCORE("最終スコア: ");

    private final String text;

    ShootingMessage(String text) {
        this.text = text;
    }

    public String text() {
        return text;
    }
}

