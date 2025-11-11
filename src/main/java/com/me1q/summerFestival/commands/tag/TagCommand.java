package com.me1q.summerFestival.commands.tag;

import com.me1q.summerFestival.SummerFestival;
import com.me1q.summerFestival.core.message.MessageBuilder;
import com.me1q.summerFestival.game.tag.TagManager;
import com.me1q.summerFestival.game.tag.constants.TagConfig;
import com.me1q.summerFestival.game.tag.constants.TagMessage;
import com.me1q.summerFestival.game.tag.item.Decoy;
import com.me1q.summerFestival.game.tag.item.InvisiblePotion;
import com.me1q.summerFestival.game.tag.item.RedHelmet;
import com.me1q.summerFestival.game.tag.item.SlownessPotion;
import com.me1q.summerFestival.game.tag.item.SmokeLauncher;
import com.me1q.summerFestival.game.tag.item.SpeedPotion;
import com.me1q.summerFestival.game.tag.item.TaggerDetector;
import com.me1q.summerFestival.game.tag.item.TaggerTeleporter;
import com.me1q.summerFestival.game.tag.itemstand.ItemStandMarkerItem;
import java.util.ArrayList;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class TagCommand implements CommandExecutor, TabCompleter {

    private static final String[] SUB_COMMANDS = {"recruit", "join", "start", "stop", "give",
        "returnpoint", "tpreturn", "help"};

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

        if (args[0].equalsIgnoreCase("join")) {
            gameManager.joinRecruit(player);
            return true;
        }

        if (!player.isOp()) {
            player.sendMessage(MessageBuilder.error("このコマンドを実行する権限がありません。"));
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "recruit" -> handleRecruitCommand(player, args);
            case "start" -> handleStartCommand(player, args);
            case "stop" -> gameManager.stopGame(player);
            case "give" -> handleGiveCommand(player, args);
            case "returnpoint" -> gameManager.giveReturnPointMarker(player);
            case "tpreturn" -> gameManager.teleportToReturnPoint(player);
            default -> sendUsage(player);
        }

        return true;
    }

    private void handleRecruitCommand(Player player, String[] args) {
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

            if (duration < TagConfig.MIN_DURATION_SECONDS.value()) {
                player.sendMessage(MessageBuilder.error(TagMessage.MIN_DURATION_ERROR.text()));
                return;
            }

            gameManager.startRecruit(player, duration);
        } catch (NumberFormatException e) {
            player.sendMessage(MessageBuilder.error(TagMessage.INVALID_NUMBER.text()));
        }
    }

    private void handleStartCommand(Player player, String[] args) {
        if (args.length == 1) {
            gameManager.startGame(player);
            return;
        }

        List<Player> initialTaggers = new ArrayList<>();
        for (int i = 1; i < args.length; i++) {
            Player tagger = player.getServer().getPlayer(args[i]);
            if (tagger == null) {
                player.sendMessage(MessageBuilder.error(
                    "プレイヤー '" + args[i] + "' が見つかりません。"));
                return;
            }
            initialTaggers.add(tagger);
        }

        gameManager.startGame(player, initialTaggers);
    }

    private void handleGiveCommand(Player player, String[] args) {
        if (args.length < 2) {
            MessageBuilder.error("使用方法: /tag give <item>");
            return;
        }

        ItemStack item;

        switch (args[1].toLowerCase()) {
            case "potion_of_invisibility" -> item = InvisiblePotion.createItem();
            case "potion_of_speed" -> item = SpeedPotion.createItem();
            case "potion_of_slowness" -> item = SlownessPotion.createItem();
            case "decoy" -> item = Decoy.createItem();
            case "smoke_launcher" -> item = SmokeLauncher.createItem();
            case "tagger_detector" -> item = TaggerDetector.createItem();
            case "tagger_teleporter" -> item = TaggerTeleporter.createItem();
            case "red_helmet" -> item = RedHelmet.createItem();
            case "item_stand" -> item = ItemStandMarkerItem.create();
            default -> item = ItemStack.of(Material.AIR);
        }

        player.getInventory().addItem(item);
    }

    private void sendUsage(Player player) {
        player.sendMessage(MessageBuilder.header("増え鬼"));
        player.sendMessage(Component.text(""));
        player.sendMessage(Component.text("ルール:").color(NamedTextColor.AQUA));
        player.sendMessage(
            Component.text("- 鬼が逃げ側を殴ると捕まえられる").color(NamedTextColor.WHITE));
        player.sendMessage(
            Component.text("- 捕まった人は鬼になります").color(NamedTextColor.WHITE));
        player.sendMessage(Component.text("- 時間内に全員鬼にならなければ逃げ側の勝ち")
            .color(NamedTextColor.WHITE));
        player.sendMessage(Component.text(""));
        player.sendMessage(Component.text("コマンド:").color(NamedTextColor.AQUA));
        player.sendMessage(Component.text("/tag recruit <時間> - 参加者を募集")
            .color(NamedTextColor.YELLOW));
        player.sendMessage(Component.text("/tag join - 募集に参加")
            .color(NamedTextColor.YELLOW));
        player.sendMessage(Component.text("/tag start [鬼...] - ゲームを開始")
            .color(NamedTextColor.YELLOW));
        player.sendMessage(Component.text("/tag stop - ゲームを停止")
            .color(NamedTextColor.YELLOW));
        player.sendMessage(Component.text("/tag returnpoint - リターンポイントマーカーを取得")
            .color(NamedTextColor.YELLOW));
        player.sendMessage(Component.text("/tag tpreturn - 全プレイヤーをリターンポイントにTP")
            .color(NamedTextColor.YELLOW));
        player.sendMessage(Component.text("/tag give <item> - アイテムを取得")
            .color(NamedTextColor.YELLOW));
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender,
        @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            return null;
        }

        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            for (String subCommand : SUB_COMMANDS) {
                if (subCommand.startsWith(args[0].toLowerCase())) {
                    completions.add(subCommand);
                }
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("recruit")) {
            if ("cancel".startsWith(args[1].toLowerCase())) {
                completions.add("cancel");

                String[] commonDurations = {"300", "600"};
                for (String duration : commonDurations) {
                    if (duration.startsWith(args[1])) {
                        completions.add(duration);
                    }
                }
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("give")) {
            String[] items = {
                "potion_of_invisibility",
                "potion_of_speed",
                "potion_of_slowness",
                "decoy",
                "smoke_launcher",
                "tagger_detector",
                "tagger_teleporter",
                "red_helmet",
                "item_stand"
            };
            for (String itemName : items) {
                if (itemName.startsWith(args[1].toLowerCase())) {
                    completions.add(itemName);
                }
            }
        } else if (args[0].equalsIgnoreCase("start")) {
            for (Player p : player.getServer().getOnlinePlayers()) {
                if (p.getName().toLowerCase().startsWith(args[args.length - 1].toLowerCase())) {
                    completions.add(p.getName());
                }
            }
        }

        return completions;
    }

    public TagManager getGameManager() {
        return gameManager;
    }
}

