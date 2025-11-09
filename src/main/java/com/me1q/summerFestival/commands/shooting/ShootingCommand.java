package com.me1q.summerFestival.commands.shooting;

import com.me1q.summerFestival.SummerFestival;
import com.me1q.summerFestival.core.message.MessageBuilder;
import com.me1q.summerFestival.game.shooting.ShootingManager;
import com.me1q.summerFestival.game.shooting.button.ButtonDataManager;
import com.me1q.summerFestival.game.shooting.constants.ShootingConfig;
import com.me1q.summerFestival.game.shooting.constants.ShootingMessage;
import com.me1q.summerFestival.game.shooting.spawner.SpawnArea;
import java.util.ArrayList;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
import org.jetbrains.annotations.NotNull;

public class ShootingCommand implements CommandExecutor, TabCompleter {

    private static final String[] SUB_COMMANDS = {"start", "stop", "status", "setbutton",
        "removebutton", "help"};

    private final ShootingManager gameManager;
    private final ButtonDataManager buttonDataManager;

    public ShootingCommand(SummerFestival plugin) {
        this.gameManager = new ShootingManager(plugin);
        this.buttonDataManager = new ButtonDataManager(plugin);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
        @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(
                MessageBuilder.error(ShootingMessage.PLAYER_ONLY.text()));
            return true;
        }

        if (args.length == 0) {
            showHelp(player);
            return true;
        }

        if (args[0].equalsIgnoreCase("start")) {
            handleStartCommand(player, args);
            return true;
        }

        if (!player.isOp()) {
            player.sendMessage(MessageBuilder.error("このコマンドを実行する権限がありません。"));
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "stop" -> handleStopCommand(player);
            case "status" -> handleStatusCommand(player);
            case "setbutton" -> handleSetButtonCommand(player, args);
            case "removebutton" -> handleRemoveButtonCommand(player);
            case "help" -> showHelp(player);
            default -> {
                player.sendMessage(
                    MessageBuilder.error(ShootingMessage.UNKNOWN_SUBCOMMAND.text() + args[0]));
                showHelp(player);
            }
        }

        return true;
    }

    private void handleStartCommand(Player player, String[] args) {
        if (gameManager.isPlayerInGame(player)) {
            player.sendMessage(MessageBuilder.error(ShootingMessage.ALREADY_IN_GAME.text()));
            player.sendMessage(MessageBuilder.warning(ShootingMessage.END_GAME_COMMAND.text()));
            return;
        }

        if (args.length != ShootingConfig.REQUIRED_COORDINATE_ARGS.value()) {
            showStartUsage(player);
            return;
        }

        try {
            double[] coords = parseCoordinates(args);
            player.sendMessage(MessageBuilder.success(ShootingMessage.GAME_STARTED.text()));
            gameManager.startGame(player, coords[0], coords[1], coords[2], coords[3], coords[4],
                coords[5]);
        } catch (NumberFormatException e) {
            player.sendMessage(MessageBuilder.error(ShootingMessage.INVALID_COORDINATES.text()));
            showStartUsage(player);
        }
    }

    private double[] parseCoordinates(String[] args) throws NumberFormatException {
        double[] coords = new double[6];
        for (int i = 0; i < 6; i++) {
            coords[i] = Double.parseDouble(args[i + 1]);
        }
        return coords;
    }

    private void showStartUsage(Player player) {
        player.sendMessage(MessageBuilder.error(ShootingMessage.SPECIFY_COORDINATES.text()));
        player.sendMessage(
            MessageBuilder.warning("Usage: /shooting start <x> <y> <z> <dx> <dy> <dz>"));
        player.sendMessage(Component.text("Example: /shooting start 100 64 100 200 80 200")
            .color(NamedTextColor.GRAY));
    }

    private void handleStopCommand(Player player) {
        if (!gameManager.isPlayerInGame(player)) {
            player.sendMessage(MessageBuilder.error(ShootingMessage.NOT_IN_GAME.text()));
            return;
        }

        player.sendMessage(MessageBuilder.warning(ShootingMessage.FORCE_STOP.text()));
        gameManager.stopGame(player);
    }

    private void handleStatusCommand(Player player) {
        if (gameManager.isPlayerInGame(player)) {
            player.sendMessage(MessageBuilder.success(ShootingMessage.GAME_IN_PROGRESS.text()));
        } else {
            player.sendMessage(MessageBuilder.warning(ShootingMessage.NOT_IN_GAME_JP.text()));
        }
    }

    private void handleSetButtonCommand(Player player, String[] args) {
        if (args.length != 7) {
            player.sendMessage(MessageBuilder.error("座標を正しく指定してください"));
            player.sendMessage(
                MessageBuilder.warning("Usage: /shooting setbutton <x1> <y1> <z1> <x2> <y2> <z2>"));
            player.sendMessage(Component.text("Example: /shooting setbutton 100 64 100 200 80 200")
                .color(NamedTextColor.GRAY));
            return;
        }

        Block targetBlock = player.getTargetBlockExact(10);
        if (targetBlock == null || !isButton(targetBlock.getType())) {
            player.sendMessage(MessageBuilder.error("ボタンを見てこのコマンドを実行してください"));
            return;
        }

        try {
            double x1 = Double.parseDouble(args[1]);
            double y1 = Double.parseDouble(args[2]);
            double z1 = Double.parseDouble(args[3]);
            double x2 = Double.parseDouble(args[4]);
            double y2 = Double.parseDouble(args[5]);
            double z2 = Double.parseDouble(args[6]);

            SpawnArea spawnArea = new SpawnArea(x1, y1, z1, x2, y2, z2);
            buttonDataManager.addButton(targetBlock.getLocation(), spawnArea);

            player.sendMessage(MessageBuilder.success("ボタンに射的範囲を設定しました"));
            player.sendMessage(MessageBuilder.info(
                String.format("範囲: (%.1f, %.1f, %.1f) - (%.1f, %.1f, %.1f)",
                    x1, y1, z1, x2, y2, z2)));
        } catch (NumberFormatException e) {
            player.sendMessage(MessageBuilder.error("座標の値が不正です"));
        }
    }

    private void handleRemoveButtonCommand(Player player) {
        Block targetBlock = player.getTargetBlockExact(10);
        if (targetBlock == null || !isButton(targetBlock.getType())) {
            player.sendMessage(MessageBuilder.error("ボタンを見てこのコマンドを実行してください"));
            return;
        }

        buttonDataManager.removeButton(targetBlock.getLocation());
        player.sendMessage(MessageBuilder.success("ボタンの設定を削除しました"));
    }

    private boolean isButton(Material material) {
        return material == Material.OAK_BUTTON
            || material == Material.SPRUCE_BUTTON
            || material == Material.BIRCH_BUTTON
            || material == Material.JUNGLE_BUTTON
            || material == Material.ACACIA_BUTTON
            || material == Material.DARK_OAK_BUTTON
            || material == Material.MANGROVE_BUTTON
            || material == Material.CHERRY_BUTTON
            || material == Material.BAMBOO_BUTTON
            || material == Material.CRIMSON_BUTTON
            || material == Material.WARPED_BUTTON
            || material == Material.STONE_BUTTON
            || material == Material.POLISHED_BLACKSTONE_BUTTON;
    }

    private void showHelp(Player player) {
        player.sendMessage(MessageBuilder.header("射的"));
        player.sendMessage(
            MessageBuilder.info("/shooting start <x> <y> <z> <dx> <dy> <dz> - ゲームを開始"));
        player.sendMessage(MessageBuilder.info("/shooting stop - ゲームを終了"));
        player.sendMessage(MessageBuilder.info("/shooting status - 現在の状況を確認"));
        player.sendMessage(
            MessageBuilder.info(
                "/shooting setbutton <x1> <y1> <z1> <x2> <y2> <z2> - ボタンに射的範囲を設定"));
        player.sendMessage(MessageBuilder.info("/shooting removebutton - ボタンの設定を削除"));
        player.sendMessage(Component.text(""));

        player.sendMessage(Component.text("座標範囲の指定:").color(NamedTextColor.YELLOW));
        player.sendMessage(Component.text("• 的が出現する範囲を2つの座標で指定してください")
            .color(NamedTextColor.WHITE));
        player.sendMessage(Component.text("• 例: /shooting start 100 64 100 200 80 200")
            .color(NamedTextColor.GRAY));
        player.sendMessage(Component.text(""));

        player.sendMessage(Component.text("ボタン設定:").color(NamedTextColor.YELLOW));
        player.sendMessage(Component.text("• ボタンを見ながらsetbuttonコマンドで範囲を設定")
            .color(NamedTextColor.WHITE));
        player.sendMessage(Component.text("• 設定後、プレイヤーがボタンを押すと射的が開始されます")
            .color(NamedTextColor.WHITE));
        player.sendMessage(Component.text(""));

        player.sendMessage(Component.text("ルール:").color(NamedTextColor.YELLOW));
        player.sendMessage(
            Component.text("• 30秒間でできるだけ多くの的を射抜こう").color(NamedTextColor.WHITE));
        player.sendMessage(Component.text("• 白: 1ポイント").color(NamedTextColor.WHITE));
        player.sendMessage(Component.text("• 黄色: 3ポイント").color(NamedTextColor.YELLOW));
        player.sendMessage(Component.text("• 赤: 5ポイント").color(NamedTextColor.RED));
        player.sendMessage(Component.text("• 金: 10ポイント").color(NamedTextColor.GOLD));
        player.sendMessage(
            Component.text("的は1秒ごとに出現し、5秒で消えます").color(NamedTextColor.GREEN));
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
        @NotNull String label,
        String[] args) {
        if (!(sender instanceof Player player)) {
            return null;
        }

        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            String input = args[0].toLowerCase();
            for (String subCommand : SUB_COMMANDS) {
                if (subCommand.startsWith(input)) {
                    completions.add(subCommand);
                }
            }
        } else if (args.length >= 2 && args[0].equalsIgnoreCase("start")) {
            String targetCoords = getTargetBlockCoordinates(player);
            if (targetCoords != null) {
                completions.add(targetCoords);
            }
        }

        return completions;
    }

    private String getTargetBlockCoordinates(Player player) {
        try {
            RayTraceResult result = player.getWorld().rayTraceBlocks(
                player.getEyeLocation(),
                player.getEyeLocation().getDirection(),
                100.0,
                org.bukkit.FluidCollisionMode.NEVER,
                true
            );

            if (result != null && result.getHitBlock() != null) {
                Block hitBlock = result.getHitBlock();
                Location blockLoc = hitBlock.getLocation();
                return blockLoc.getBlockX() + " " + blockLoc.getBlockY() + " "
                    + blockLoc.getBlockZ();
            }
        } catch (Exception ignored) {
        }

        return null;
    }

    public ShootingManager getGameManager() {
        return gameManager;
    }

    public ButtonDataManager getButtonDataManager() {
        return buttonDataManager;
    }
}

