package com.me1q.summerFestival.commands.shop;

import com.me1q.summerFestival.core.message.MessageBuilder;
import com.me1q.summerFestival.shop.ShopAdminGUI;
import com.me1q.summerFestival.shop.ShopGUI;
import com.me1q.summerFestival.shop.ShopItemManager;
import com.me1q.summerFestival.shop.ShopVillager;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ShopCommand implements TabExecutor {

    private final ShopVillager shopVillager;
    private final ShopGUI shopGUI;
    private final ShopAdminGUI adminGUI;
    private final ShopItemManager itemManager;

    public ShopCommand(ShopVillager shopVillager, ShopGUI shopGUI, ShopAdminGUI adminGUI,
        ShopItemManager itemManager) {
        this.shopVillager = shopVillager;
        this.shopGUI = shopGUI;
        this.adminGUI = adminGUI;
        this.itemManager = itemManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
        @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(MessageBuilder.error("このコマンドはプレイヤーのみ実行できます。"));
            return true;
        }

        if (args.length == 0) {
            sendUsage(sender);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "spawn" -> handleSpawn(player, args);
            case "remove" -> handleRemove(player);
            case "open" -> handleOpen(player, args);
            case "admin" -> handleAdmin(player, args);
            default -> sendUsage(sender);
        }

        return true;
    }

    private void handleSpawn(Player player, String[] args) {
        if (!player.hasPermission("summerfestival.shop.spawn")) {
            player.sendMessage(MessageBuilder.error("このコマンドを実行する権限がありません。"));
            return;
        }

        String shopId = args.length > 1 ? args[1] : "default";
        shopVillager.spawnShopVillager(player.getLocation(), shopId);
        player.sendMessage(MessageBuilder.success("ショップ [" + shopId + "] を生成しました。"));
    }

    private void handleRemove(Player player) {
        if (!player.hasPermission("summerfestival.shop.remove")) {
            player.sendMessage(MessageBuilder.error("このコマンドを実行する権限がありません。"));
            return;
        }

        List<Entity> nearbyEntities = player.getNearbyEntities(5, 5, 5);
        int removed = 0;
        Set<String> deletedShopIds = new HashSet<>();

        for (Entity entity : nearbyEntities) {
            if (entity instanceof Villager villager && ShopVillager.isShopVillager(villager)) {
                String shopId = ShopVillager.getShopId(villager);
                villager.remove();
                removed++;
                if (!shopId.equals("default")) {
                    deletedShopIds.add(shopId);
                }
            }
        }

        for (String shopId : deletedShopIds) {
            itemManager.deleteShop(shopId);
        }

        if (removed > 0) {
            player.sendMessage(MessageBuilder.success(removed + "体のショップNPCを削除しました。"));
            if (!deletedShopIds.isEmpty()) {
                player.sendMessage(MessageBuilder.success(
                    "ショップデータ: " + String.join(", ", deletedShopIds) + " を削除しました。"));
            }
        } else {
            player.sendMessage(MessageBuilder.error("近くにショップNPCが見つかりません。"));
        }
    }

    private void handleOpen(Player player, String[] args) {
        String shopId = args.length > 1 ? args[1] : "default";
        shopGUI.openShop(player, shopId);
    }

    private void handleAdmin(Player player, String[] args) {
        if (!player.hasPermission("summerfestival.shop.admin")) {
            player.sendMessage(MessageBuilder.error("このコマンドを実行する権限がありません。"));
            return;
        }
        String shopId = args.length > 1 ? args[1] : "default";
        adminGUI.openAdminGUI(player, shopId);
    }

    private void sendUsage(CommandSender sender) {
        sender.sendMessage(Component.text("=== ショップコマンド ===").color(NamedTextColor.GOLD));
        sender.sendMessage(
            Component.text("/shop spawn [shopId] - ショップNPCを生成")
                .color(NamedTextColor.YELLOW));
        sender.sendMessage(
            Component.text("/shop remove - 近くのショップNPCを削除").color(NamedTextColor.YELLOW));
        sender.sendMessage(
            Component.text("/shop open [shopId] - ショップを開く").color(NamedTextColor.YELLOW));
        sender.sendMessage(
            Component.text("/shop admin [shopId] - ショップ管理画面を開く")
                .color(NamedTextColor.YELLOW));
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender,
        @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.add("open");
            if (sender.hasPermission("summerfestival.shop.spawn")) {
                completions.add("spawn");
            }
            if (sender.hasPermission("summerfestival.shop.remove")) {
                completions.add("remove");
            }
            if (sender.hasPermission("summerfestival.shop.admin")) {
                completions.add("admin");
            }
            return completions.stream()
                .filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
                .toList();
        }

        return completions;
    }
}

