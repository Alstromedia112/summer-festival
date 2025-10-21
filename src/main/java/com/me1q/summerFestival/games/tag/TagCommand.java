package com.me1q.summerFestival.games.tag;

import com.me1q.summerFestival.SummerFestival;
import com.me1q.summerFestival.core.message.MessageBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TagCommand implements CommandExecutor, TabCompleter {

    private static final int MIN_DURATION = 30;

    private final TagManager gameManager;

    public TagCommand(SummerFestival plugin) {
        this.gameManager = new TagManager(plugin);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
        @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(MessageBuilder.error("このコマンドはプレイヤーのみ実行できます。"));
            return true;
        }

        if (args.length == 0) {
            sendUsage(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "recruit" -> handleCommand(player, args);
            case "join" -> gameManager.joinRecruit(player);
            case "start" -> gameManager.startGame(player);
            case "stop" -> gameManager.stopGame(player);
            default -> sendUsage(player);
        }

        return true;
    }

    private void handleCommand(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(
                Component.text("使用方法: /tag recruit <時間(秒)>").color(NamedTextColor.RED));
            player.sendMessage(Component.text("例: /tag recruit 300").color(NamedTextColor.GRAY));
            player.sendMessage(
                Component.text("キャンセル: /tag recruit cancel").color(NamedTextColor.GRAY));
            return;
        }

        if (args[1].equalsIgnoreCase("cancel")) {
            gameManager.cancelRecruit(player);
            return;
        }

        try {
            int duration = Integer.parseInt(args[1]);

            if (duration < MIN_DURATION) {
                player.sendMessage(MessageBuilder.error("時間を30秒以上で指定してください。"));
                return;
            }

            gameManager.startRecruit(player, duration);
        } catch (NumberFormatException e) {
            player.sendMessage(MessageBuilder.error("数値を正しく入力してください。"));
        }
    }

    private void sendUsage(Player player) {
        player.sendMessage(MessageBuilder.header("増え鬼"));
        player.sendMessage(
            Component.text("/tag recruit <時間> - 参加者を募集").color(NamedTextColor.YELLOW));
        player.sendMessage(
            Component.text("/tag recruit cancel - 募集をキャンセル").color(NamedTextColor.YELLOW));
        player.sendMessage(Component.text("/tag join - 募集に参加").color(NamedTextColor.YELLOW));
        player.sendMessage(
            Component.text("/tag start - 増え鬼を開始").color(NamedTextColor.YELLOW));
        player.sendMessage(
            Component.text("/tag stop - 増え鬼を終了").color(NamedTextColor.YELLOW));
        player.sendMessage(
            Component.text("/tag status - ゲームの状態を確認").color(NamedTextColor.YELLOW));
        player.sendMessage(Component.text(""));
        player.sendMessage(Component.text("ルール:").color(NamedTextColor.AQUA));
        player.sendMessage(
            Component.text("- 鬼が逃げる側を殴るとタッチ成功").color(NamedTextColor.WHITE));
        player.sendMessage(
            Component.text("- タッチされた人は鬼になります（増え鬼）").color(NamedTextColor.WHITE));
        player.sendMessage(Component.text("- 時間内に全員鬼にならなければ逃げ側の勝ち")
            .color(NamedTextColor.WHITE));
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender,
        @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.addAll(Arrays.asList("recruit", "join", "start", "stop", "status"));
        } else if (args.length == 2 && args[0].equalsIgnoreCase("recruit")) {
            completions.addAll(Arrays.asList("cancel", "60", "120", "300", "600"));
        }

        return completions;
    }

    public TagManager getGameManager() {
        return gameManager;
    }
}
