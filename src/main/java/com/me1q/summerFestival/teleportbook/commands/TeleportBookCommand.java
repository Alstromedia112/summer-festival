package com.me1q.summerFestival.teleportbook.commands;

import com.me1q.summerFestival.core.message.MessageBuilder;
import com.me1q.summerFestival.teleportbook.TeleportBookGUI;
import com.me1q.summerFestival.teleportbook.TeleportBookItem;
import com.me1q.summerFestival.teleportbook.TeleportLocation;
import com.me1q.summerFestival.teleportbook.TeleportLocationManager;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TeleportBookCommand implements TabExecutor {

    private final TeleportLocationManager locationManager;
    private final TeleportBookGUI gui;

    public TeleportBookCommand(TeleportLocationManager locationManager, TeleportBookGUI gui) {
        this.locationManager = locationManager;
        this.gui = gui;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
        @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(
                    MessageBuilder.error("このコマンドはプレイヤーのみ実行できます。"));
                return true;
            }
            player.getInventory().addItem(TeleportBookItem.createBook());
            player.sendMessage(MessageBuilder.success("テレポートの本を入手しました。"));
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "give" -> handleGive(sender, args);
            case "location" -> handleLocation(sender, args);
            case "reload" -> handleReload(sender);
            case "help" -> sendUsage(sender);
            default -> sendUsage(sender);
        }

        return true;
    }

    private void handleGive(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(MessageBuilder.error("使用方法: /teleportbook give <player>"));
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(MessageBuilder.error("プレイヤーが見つかりません: " + args[1]));
            return;
        }

        target.getInventory().addItem(TeleportBookItem.createBook());
        sender.sendMessage(
            MessageBuilder.success(target.getName() + " にテレポートの本を付与しました。"));
        target.sendMessage(MessageBuilder.success("テレポートの本を入手しました。"));
    }

    private void handleLocation(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(
                MessageBuilder.error("使用方法: /teleportbook location <add|remove|list>"));
            return;
        }

        String action = args[1].toLowerCase();

        switch (action) {
            case "add" -> handleLocationAdd(sender, args);
            case "remove" -> handleLocationRemove(sender, args);
            case "list" -> handleLocationList(sender);
            default -> sender.sendMessage(
                MessageBuilder.error("使用方法: /teleportbook location <add|remove|list>"));
        }
    }

    private void handleLocationAdd(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(MessageBuilder.error("このコマンドはプレイヤーのみ実行できます。"));
            return;
        }

        if (args.length < 3) {
            sender.sendMessage(MessageBuilder.error("使用方法: /teleportbook location add <name>"));
            return;
        }

        String locationName = args[2];

        if (locationManager.addLocation(locationName, player.getLocation())) {
            player.sendMessage(MessageBuilder.success("地点「" + locationName + "」を追加しました。"));
        } else {
            player.sendMessage(MessageBuilder.error("地点「" + locationName + "」は既に存在します。"));
        }
    }

    private void handleLocationRemove(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(
                MessageBuilder.error("使用方法: /teleportbook location remove <name>"));
            return;
        }

        String locationName = args[2];

        if (locationManager.removeLocation(locationName)) {
            sender.sendMessage(MessageBuilder.success("地点「" + locationName + "」を削除しました。"));
        } else {
            sender.sendMessage(MessageBuilder.error("地点「" + locationName + "」は存在しません。"));
        }
    }

    private void handleLocationList(CommandSender sender) {
        List<TeleportLocation> locations = locationManager.getAllLocations();

        if (locations.isEmpty()) {
            sender.sendMessage(
                Component.text("登録されている地点はありません。").color(NamedTextColor.YELLOW));
            return;
        }

        sender.sendMessage(Component.text("=== テレポート地点一覧 ===").color(NamedTextColor.GOLD));
        for (TeleportLocation location : locations) {
            var loc = location.getLocation();
            sender.sendMessage(Component.text(String.format("- %s: (%.1f, %.1f, %.1f) %s",
                    location.getName(),
                    loc.getX(), loc.getY(), loc.getZ(),
                    loc.getWorld().getName()))
                .color(NamedTextColor.GREEN));
        }
    }

    private void handleReload(CommandSender sender) {
        locationManager.reload();
        sender.sendMessage(MessageBuilder.success("テレポート地点を再読み込みしました。"));
    }

    private void sendUsage(CommandSender sender) {
        sender.sendMessage(
            Component.text("=== テレポートの本 コマンド ===").color(NamedTextColor.GOLD));
        sender.sendMessage(
            Component.text("/teleportbook - テレポートの本を入手").color(NamedTextColor.GREEN));
        sender.sendMessage(Component.text("/teleportbook give <player> - プレイヤーに本を付与")
            .color(NamedTextColor.GREEN));
        sender.sendMessage(Component.text("/teleportbook location add <name> - 地点追加")
            .color(NamedTextColor.GREEN));
        sender.sendMessage(Component.text("/teleportbook location remove <name> - 地点削除")
            .color(NamedTextColor.GREEN));
        sender.sendMessage(
            Component.text("/teleportbook location list - 地点一覧").color(NamedTextColor.GREEN));
        sender.sendMessage(
            Component.text("/teleportbook reload - 地点再読み込み").color(NamedTextColor.GREEN));
        sender.sendMessage(
            Component.text("/teleportbook help - ヘルプ表示").color(NamedTextColor.GREEN));
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender,
        @NotNull Command command,
        @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            return java.util.stream.Stream.of("give", "location", "reload", "help")
                .filter(s -> s.startsWith(args[0].toLowerCase()))
                .collect(Collectors.toList());
        }

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("give")) {
                return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(s -> s.toLowerCase().startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
            }

            if (args[0].equalsIgnoreCase("location")) {
                return java.util.stream.Stream.of("add", "remove", "list")
                    .filter(s -> s.startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
            }
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("location")
            && args[1].equalsIgnoreCase("remove")) {
            return locationManager.getAllLocations().stream()
                .map(TeleportLocation::getName)
                .filter(s -> s.toLowerCase().startsWith(args[2].toLowerCase()))
                .collect(Collectors.toList());
        }

        return new ArrayList<>();
    }
}

