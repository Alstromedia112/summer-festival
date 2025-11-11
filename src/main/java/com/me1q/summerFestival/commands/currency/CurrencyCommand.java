package com.me1q.summerFestival.commands.currency;

import com.me1q.summerFestival.SummerFestival;
import com.me1q.summerFestival.core.message.MessageBuilder;
import com.me1q.summerFestival.currency.CurrencyFormatter;
import com.me1q.summerFestival.currency.CurrencyManager;
import java.util.ArrayList;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CurrencyCommand implements TabExecutor {

    private final CurrencyManager currencyManager;

    public CurrencyCommand(SummerFestival plugin) {
        this.currencyManager = plugin.getCurrencyManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
        @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            sendUsage(sender);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "balance" -> handleBalance(sender, args);
            case "add" -> handleAdd(sender, args);
            case "remove" -> handleRemove(sender, args);
            case "set" -> handleSet(sender, args);
            case "setreward" -> handleSetReward(sender, args);
            default -> sendUsage(sender);
        }

        return true;
    }

    private void handleBalance(CommandSender sender, String[] args) {
        Player target;

        if (args.length == 1) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(MessageBuilder.error("プレイヤーを指定してください。"));
                return;
            }
            target = (Player) sender;
        } else {
            target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage(MessageBuilder.error("プレイヤーが見つかりません: " + args[1]));
                return;
            }
        }

        int balance = currencyManager.getBalance(target);
        String formattedBalance = CurrencyFormatter.format(balance);

        if (target.equals(sender)) {
            sender.sendMessage(MessageBuilder.success("あなたの所持金: " + formattedBalance));
        } else {
            sender.sendMessage(
                MessageBuilder.success(target.getName() + "の所持金: " + formattedBalance));
        }
    }

    private void handleAdd(CommandSender sender, String[] args) {
        if (!sender.hasPermission("summerfestival.currency.add")) {
            sender.sendMessage(MessageBuilder.error("このコマンドを実行する権限がありません。"));
            return;
        }

        if (args.length < 3) {
            sender.sendMessage(MessageBuilder.error("使用方法: /currency add <player> <amount>"));
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(MessageBuilder.error("プレイヤーが見つかりません: " + args[1]));
            return;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[2]);
            if (amount <= 0) {
                sender.sendMessage(MessageBuilder.error("金額は1以上の整数を指定してください。"));
                return;
            }
        } catch (NumberFormatException e) {
            sender.sendMessage(MessageBuilder.error("無効な金額です: " + args[2]));
            return;
        }

        currencyManager.addBalance(target, amount);
        String formattedAmount = CurrencyFormatter.format(amount);
        sender.sendMessage(
            MessageBuilder.success(target.getName() + "に" + formattedAmount + "を付与しました。"));
        target.sendMessage(MessageBuilder.success(formattedAmount + "を受け取りました。"));
    }

    private void handleRemove(CommandSender sender, String[] args) {
        if (!sender.hasPermission("summerfestival.currency.remove")) {
            sender.sendMessage(MessageBuilder.error("このコマンドを実行する権限がありません。"));
            return;
        }

        if (args.length < 3) {
            sender.sendMessage(
                MessageBuilder.error("使用方法: /currency remove <player> <amount>"));
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(MessageBuilder.error("プレイヤーが見つかりません: " + args[1]));
            return;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[2]);
            if (amount <= 0) {
                sender.sendMessage(MessageBuilder.error("金額は1以上の整数を指定してください。"));
                return;
            }
        } catch (NumberFormatException e) {
            sender.sendMessage(MessageBuilder.error("無効な金額です: " + args[2]));
            return;
        }

        boolean success = currencyManager.removeBalance(target, amount);
        if (!success) {
            sender.sendMessage(MessageBuilder.error(target.getName() + "の残高が不足しています。"));
            return;
        }

        String formattedAmount = CurrencyFormatter.format(amount);
        sender.sendMessage(MessageBuilder.success(
            target.getName() + "から" + formattedAmount + "を減算しました。"));
        target.sendMessage(MessageBuilder.error(formattedAmount + "が減算されました。"));
    }

    private void handleSet(CommandSender sender, String[] args) {
        if (!sender.hasPermission("summerfestival.currency.set")) {
            sender.sendMessage(MessageBuilder.error("このコマンドを実行する権限がありません。"));
            return;
        }

        if (args.length < 3) {
            sender.sendMessage(MessageBuilder.error("使用方法: /currency set <player> <amount>"));
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(MessageBuilder.error("プレイヤーが見つかりません: " + args[1]));
            return;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[2]);
            if (amount < 0) {
                sender.sendMessage(MessageBuilder.error("金額は0以上の整数を指定してください。"));
                return;
            }
        } catch (NumberFormatException e) {
            sender.sendMessage(MessageBuilder.error("無効な金額です: " + args[2]));
            return;
        }

        currencyManager.setBalance(target, amount);
        String formattedAmount = CurrencyFormatter.format(amount);
        sender.sendMessage(MessageBuilder.success(
            target.getName() + "の所持金を" + formattedAmount + "に設定しました。"));
    }

    private void handleSetReward(CommandSender sender, String[] args) {
        if (!sender.hasPermission("summerfestival.currency.setreward")) {
            sender.sendMessage(MessageBuilder.error("このコマンドを実行する権限がありません。"));
            return;
        }

        if (args.length < 2) {
            int currentReward = currencyManager.getMovementListener().getRewardAmount();
            sender.sendMessage(MessageBuilder.success(
                "現在の移動報酬: " + CurrencyFormatter.format(currentReward) + "/10m"));
            sender.sendMessage(MessageBuilder.error("使用方法: /currency setreward <amount>"));
            return;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[1]);
            if (amount < 0) {
                sender.sendMessage(MessageBuilder.error("報酬額は0以上の整数を指定してください。"));
                return;
            }
        } catch (NumberFormatException e) {
            sender.sendMessage(MessageBuilder.error("無効な報酬額です: " + args[1]));
            return;
        }

        currencyManager.getMovementListener().setRewardAmount(amount);
        String formattedAmount = CurrencyFormatter.format(amount);
        sender.sendMessage(
            MessageBuilder.success("移動報酬を" + formattedAmount + "/10mに設定しました。"));
    }

    private void sendUsage(CommandSender sender) {
        sender.sendMessage(Component.text("=== 通貨コマンド ===").color(NamedTextColor.GOLD));
        sender.sendMessage(
            Component.text("/currency balance [player] - 残高を確認").color(NamedTextColor.YELLOW));
        sender.sendMessage(Component.text("/currency add <player> <amount> - 残高を追加")
            .color(NamedTextColor.YELLOW));
        sender.sendMessage(Component.text("/currency remove <player> <amount> - 残高を減算")
            .color(NamedTextColor.YELLOW));
        sender.sendMessage(Component.text("/currency set <player> <amount> - 残高を設定")
            .color(NamedTextColor.YELLOW));
        sender.sendMessage(
            Component.text("/currency setreward <amount> - 移動報酬を設定（10m移動あたり）")
                .color(NamedTextColor.YELLOW));
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender,
        @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.add("balance");
            if (sender.hasPermission("summerfestival.currency.add")) {
                completions.add("add");
            }
            if (sender.hasPermission("summerfestival.currency.remove")) {
                completions.add("remove");
            }
            if (sender.hasPermission("summerfestival.currency.set")) {
                completions.add("set");
            }
            if (sender.hasPermission("summerfestival.currency.setreward")) {
                completions.add("setreward");
            }
            return completions.stream()
                .filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
                .toList();
        }

        if (args.length == 2) {
            String subCommand = args[0].toLowerCase();
            if (subCommand.equals("balance") || subCommand.equals("add") ||
                subCommand.equals("remove") || subCommand.equals("set")) {
                return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
                    .toList();
            }
            if (subCommand.equals("setreward")) {
                completions.add("1");
                completions.add("5");
                completions.add("10");
                completions.add("50");
                completions.add("100");
                return completions;
            }
        }

        if (args.length == 3) {
            String subCommand = args[0].toLowerCase();
            if (subCommand.equals("add") || subCommand.equals("remove") || subCommand.equals(
                "set")) {
                completions.add("100");
                completions.add("500");
                completions.add("1000");
                completions.add("5000");
                completions.add("10000");
                return completions;
            }
        }

        return completions;
    }
}

