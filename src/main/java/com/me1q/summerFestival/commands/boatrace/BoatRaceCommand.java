package com.me1q.summerFestival.commands.boatrace;

import com.me1q.summerFestival.SummerFestival;
import com.me1q.summerFestival.core.message.MessageBuilder;
import com.me1q.summerFestival.game.boatrace.BoatRaceManager;
import com.me1q.summerFestival.game.boatrace.constants.Config;
import com.me1q.summerFestival.game.boatrace.constants.Message;
import com.me1q.summerFestival.game.boatrace.constants.RecruitmentMode;
import java.util.ArrayList;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BoatRaceCommand implements CommandExecutor, TabCompleter {

    private static final String[] SUB_COMMANDS = {"recruit", "join", "draw", "start", "stop",
        "getgoal",
        "cleargoal", "getitemstand", "getboatstand", "clearboatstand", "help"};

    private final BoatRaceManager gameManager;

    public BoatRaceCommand(SummerFestival plugin) {
        this.gameManager = new BoatRaceManager(plugin);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
        @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(MessageBuilder.error("このコマンドはプレイヤーのみ実行できます。"));
            return true;
        }

        if (args.length == 0) {
            showHelp(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "recruit" -> handleRecruitCommand(player, args);
            case "join" -> handleJoinCommand(player);
            case "draw" -> handleDrawCommand(player);
            case "start" -> handleStartCommand(player);
            case "stop" -> handleStopCommand(player);
            case "getgoal" -> handleGetGoalCommand(player);
            case "cleargoal" -> handleClearGoalCommand(player);
            case "getitemstand" -> handleGetItemStandCommand(player);
            case "getboatstand" -> handleGetBoatStandCommand(player);
            case "clearboatstand" -> handleClearBoatStandCommand(player);
            case "help" -> showHelp(player);
            default -> {
                player.sendMessage(MessageBuilder.error("不明なサブコマンド: " + args[0]));
                showHelp(player);
            }
        }

        return true;
    }

    private void handleRecruitCommand(Player player, String[] args) {
        if (args.length > 1 && args[1].equalsIgnoreCase("cancel")) {
            gameManager.cancelRecruit(player);
            return;
        }

        if (!gameManager.hasGoalLine(player)) {
            player.sendMessage(MessageBuilder.error("ゴール地点を設定してください"));
            player.sendMessage(MessageBuilder.warning("/boatrace getgoal - ゴールマーカーを取得"));
            return;
        }

        int maxPlayers = Config.DEFAULT_MAX_PLAYERS.value();
        if (args.length > 1) {
            try {
                maxPlayers = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                player.sendMessage(MessageBuilder.error("人数は数字で指定してください"));
                return;
            }
        }

        boolean organizerParticipates = true;
        if (args.length > 2) {
            String participateArg = args[2].toLowerCase();
            if (participateArg.equals("false") || participateArg.equals("f")) {
                organizerParticipates = false;
            } else if (!participateArg.equals("true") && !participateArg.equals("t")) {
                player.sendMessage(
                    MessageBuilder.error("参加フラグは true または false で指定してください"));
                return;
            }
        }

        RecruitmentMode mode = RecruitmentMode.FIRST_COME;
        if (args.length > 3) {
            RecruitmentMode parsedMode = RecruitmentMode.fromString(args[3]);
            if (parsedMode == null) {
                player.sendMessage(
                    MessageBuilder.error("募集方式は first または lottery で指定してください"));
                return;
            }
            mode = parsedMode;
        }

        gameManager.startRecruit(player, maxPlayers, organizerParticipates, mode);
    }

    private void handleJoinCommand(Player player) {
        gameManager.joinRecruit(player);
    }

    private void handleDrawCommand(Player player) {
        gameManager.drawLottery(player);
    }

    private void handleStartCommand(Player player) {
        gameManager.startRace(player);
    }

    private void handleGetGoalCommand(Player player) {
        gameManager.giveGoalMarkerEgg(player);
    }

    private void handleClearGoalCommand(Player player) {
        gameManager.clearGoalLines(player);
        player.sendMessage(MessageBuilder.success(Message.ALL_MARKERS_REMOVED.text()));
    }

    private void handleGetItemStandCommand(Player player) {
        gameManager.giveItemStandMarker(player);
    }

    private void handleGetBoatStandCommand(Player player) {
        gameManager.giveBoatStandMarker(player);
    }

    private void handleClearBoatStandCommand(Player player) {
        gameManager.clearBoatStands(player);
    }

    private void handleStopCommand(Player player) {
        gameManager.stopRace(player);
    }

    private void showHelp(Player player) {
        player.sendMessage(MessageBuilder.header("ボートレース"));
        player.sendMessage(Component.text("/boatrace getgoal - ゴールマーカーを取得")
            .color(NamedTextColor.YELLOW));
        player.sendMessage(Component.text("/boatrace cleargoal - すべてのゴールマーカーを削除")
            .color(NamedTextColor.YELLOW));
        player.sendMessage(Component.text("/boatrace getitemstand - アイテムスタンドマーカーを取得")
            .color(NamedTextColor.YELLOW));
        player.sendMessage(Component.text("/boatrace getboatstand - ボートスタンドマーカーを取得")
            .color(NamedTextColor.YELLOW));
        player.sendMessage(Component.text("/boatrace clearboatstand - すべてのボートスタンドを削除")
            .color(NamedTextColor.YELLOW));
        player.sendMessage(
            Component.text(
                    "/boatrace recruit <people> <true/false> <first/lottery> - レース参加者を募集")
                .color(NamedTextColor.YELLOW));
        player.sendMessage(Component.text("/boatrace recruit cancel - 募集をキャンセル")
            .color(NamedTextColor.YELLOW));
        player.sendMessage(
            Component.text("/boatrace join - 募集中のレースに参加").color(NamedTextColor.YELLOW));
        player.sendMessage(
            Component.text("/boatrace draw - 抽選を実施 (抽選モードのみ)")
                .color(NamedTextColor.YELLOW));
        player.sendMessage(
            Component.text("/boatrace start - レースを開始").color(NamedTextColor.YELLOW));
        player.sendMessage(
            Component.text("/boatrace stop - レースを終了").color(NamedTextColor.YELLOW));
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
        @NotNull String alias,
        String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            for (String subCommand : SUB_COMMANDS) {
                if (subCommand.startsWith(args[0].toLowerCase())) {
                    completions.add(subCommand);
                }
            }
        } else if (args[0].equalsIgnoreCase("recruit")) {
            if (args.length == 2) {
                if ("cancel".startsWith(args[1].toLowerCase())) {
                    completions.add("cancel");
                }
                for (int i = 2; i <= 10; i++) {
                    if (String.valueOf(i).startsWith(args[1])) {
                        completions.add(String.valueOf(i));
                    }
                }
            } else if (args.length == 3) {
                if ("true".startsWith(args[2].toLowerCase())) {
                    completions.add("true");
                }
                if ("false".startsWith(args[2].toLowerCase())) {
                    completions.add("false");
                }
            } else if (args.length == 4) {
                if ("first".startsWith(args[3].toLowerCase())) {
                    completions.add("first");
                }
                if ("lottery".startsWith(args[3].toLowerCase())) {
                    completions.add("lottery");
                }
            }
        }

        return completions;
    }

    public BoatRaceManager getGameManager() {
        return gameManager;
    }
}
