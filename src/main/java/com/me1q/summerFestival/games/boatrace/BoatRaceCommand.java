package com.me1q.summerFestival.games.boatrace;

import com.me1q.summerFestival.SummerFestival;
import com.me1q.summerFestival.core.message.MessageBuilder;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class BoatRaceCommand implements CommandExecutor, TabCompleter {

    private static final String[] SUB_COMMANDS = {"recruit", "join", "start", "stop", "getgoal",
        "cleargoal", "help"};

    private final BoatRaceGame gameManager;

    public BoatRaceCommand(SummerFestival plugin) {
        this.gameManager = new BoatRaceGame(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
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
            case "start" -> handleStartCommand(player);
            case "stop" -> handleStopCommand(player);
            case "getgoal" -> handleGetGoalCommand(player);
            case "cleargoal" -> handleClearGoalCommand(player);
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

        if (gameManager.hasGoalLine(player)) {
            player.sendMessage(MessageBuilder.error("ゴール地点を設定してください"));
            player.sendMessage(MessageBuilder.warning("/boatrace getgoal - ゴールマーカーを取得"));
            return;
        }

        // Parse maxPlayers (default: 10)
        int maxPlayers = 10;
        if (args.length > 1) {
            try {
                maxPlayers = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                player.sendMessage(MessageBuilder.error("人数は数字で指定してください"));
                return;
            }
        }

        // Parse organizerParticipates (default: true)
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

        gameManager.startRecruit(player, maxPlayers, organizerParticipates);
    }

    private void handleJoinCommand(Player player) {
        gameManager.joinRecruit(player);
    }

    private void handleStartCommand(Player player) {
        gameManager.startRace(player);
    }

    private void handleGetGoalCommand(Player player) {
        gameManager.giveGoalMarkerEgg(player);
    }

    private void handleClearGoalCommand(Player player) {
        gameManager.clearGoalLines(player);
        player.sendMessage(MessageBuilder.success("すべてのゴール地点を削除しました"));
    }

    private void handleStopCommand(Player player) {
        gameManager.stopRace(player);
    }

    private void showHelp(Player player) {
        player.sendMessage(MessageBuilder.header("ボートレース コマンド"));
        player.sendMessage("§e/boatrace getgoal");
        player.sendMessage("§7  - ゴールマーカー（スポーンエッグ）を取得");
        player.sendMessage("§7    右クリックでゴール地点を設置");
        player.sendMessage("§e/boatrace cleargoal");
        player.sendMessage("§7  - すべてのゴール地点を削除");
        player.sendMessage("§e/boatrace recruit [人数] [true/false]");
        player.sendMessage("§7  - レース参加者を募集");
        player.sendMessage("§7    [人数]: 定員（省略時: 10人）");
        player.sendMessage("§7    [true/false]: 自分も参加するか（省略時: true）");
        player.sendMessage("§e/boatrace recruit cancel");
        player.sendMessage("§7  - 募集をキャンセル");
        player.sendMessage("§e/boatrace join");
        player.sendMessage("§7  - 募集中のレースに参加");
        player.sendMessage("§e/boatrace start");
        player.sendMessage("§7  - レースを開始（募集した人のみ）");
        player.sendMessage("§e/boatrace stop");
        player.sendMessage("§7  - レースを終了");
        player.sendMessage("§e/boatrace help");
        player.sendMessage("§7  - ヘルプを表示");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias,
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
                // Suggest "cancel" or numbers
                if ("cancel".startsWith(args[1].toLowerCase())) {
                    completions.add("cancel");
                }
                // Suggest some common numbers
                for (int i = 2; i <= 10; i++) {
                    if (String.valueOf(i).startsWith(args[1])) {
                        completions.add(String.valueOf(i));
                    }
                }
            } else if (args.length == 3) {
                // Suggest true/false for participation
                if ("true".startsWith(args[2].toLowerCase())) {
                    completions.add("true");
                }
                if ("false".startsWith(args[2].toLowerCase())) {
                    completions.add("false");
                }
            }
        }

        return completions;
    }

    public BoatRaceGame getGameManager() {
        return gameManager;
    }
}
