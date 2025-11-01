package com.me1q.summerFestival.game.shooting;

import com.me1q.summerFestival.SummerFestival;
import com.me1q.summerFestival.core.message.MessageBuilder;
import java.util.ArrayList;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
import org.jetbrains.annotations.NotNull;

public class ShootingCommand implements CommandExecutor, TabCompleter {

    private static final int REQUIRED_COORDINATE_ARGS = 7;
    private static final String[] SUB_COMMANDS = {"start", "stop", "status", "help"};

    private final ShootingManager gameManager;

    public ShootingCommand(SummerFestival plugin) {
        this.gameManager = new ShootingManager(plugin);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
        @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(
                MessageBuilder.error("This command can only be executed by the player."));
            return true;
        }

        if (args.length == 0) {
            showHelp(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "start" -> handleStartCommand(player, args);
            case "stop" -> handleStopCommand(player);
            case "status" -> handleStatusCommand(player);
            case "help" -> showHelp(player);
            default -> {
                player.sendMessage(MessageBuilder.error("Unknown subcommand: " + args[0]));
                showHelp(player);
            }
        }

        return true;
    }

    private void handleStartCommand(Player player, String[] args) {
        if (gameManager.isPlayerInGame(player)) {
            player.sendMessage(MessageBuilder.error("You are already in the game."));
            player.sendMessage(MessageBuilder.warning("End the game with: /shooting stop"));
            return;
        }

        if (args.length != REQUIRED_COORDINATE_ARGS) {
            showStartUsage(player);
            return;
        }

        try {
            double[] coords = parseCoordinates(args);
            player.sendMessage(MessageBuilder.success("射的ゲームを開始します..."));
            gameManager.startGame(player, coords[0], coords[1], coords[2], coords[3], coords[4],
                coords[5]);
        } catch (NumberFormatException e) {
            player.sendMessage(MessageBuilder.error("座標の形式が正しくありません"));
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
        player.sendMessage(MessageBuilder.error("座標範囲を指定してください"));
        player.sendMessage(
            MessageBuilder.warning("Usage: /shooting start <x> <y> <z> <dx> <dy> <dz>"));
        player.sendMessage(Component.text("Example: /shooting start 100 64 100 200 80 200")
            .color(NamedTextColor.GRAY));
    }

    private void handleStopCommand(Player player) {
        if (!gameManager.isPlayerInGame(player)) {
            player.sendMessage(MessageBuilder.error("Not currently in the game."));
            return;
        }

        player.sendMessage(MessageBuilder.warning("Force stop the game..."));
        gameManager.stopGame(player);
    }

    private void handleStatusCommand(Player player) {
        if (gameManager.isPlayerInGame(player)) {
            player.sendMessage(MessageBuilder.success("ゲーム進行中"));
        } else {
            player.sendMessage(MessageBuilder.warning("現在ゲームに参加していません"));
        }
    }

    private void showHelp(Player player) {
        player.sendMessage(MessageBuilder.header("射的"));
        player.sendMessage(
            MessageBuilder.info("/shooting start <x> <y> <z> <dx> <dy> <dz> - ゲームを開始"));
        player.sendMessage(MessageBuilder.info("/shooting stop - ゲームを終了"));
        player.sendMessage(MessageBuilder.info("/shooting status - 現在の状況を確認"));
        player.sendMessage(Component.text(""));

        player.sendMessage(Component.text("座標範囲の指定:").color(NamedTextColor.YELLOW));
        player.sendMessage(Component.text("• 的が出現する範囲を2つの座標で指定してください")
            .color(NamedTextColor.WHITE));
        player.sendMessage(Component.text("• 例: /shooting start 100 64 100 200 80 200")
            .color(NamedTextColor.GRAY));
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
}
