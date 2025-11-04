package com.me1q.summerFestival.game.shooting.session;

import com.me1q.summerFestival.SummerFestival;
import com.me1q.summerFestival.core.message.MessageBuilder;
import com.me1q.summerFestival.game.shooting.constants.ShootingAnnouncement;
import com.me1q.summerFestival.game.shooting.constants.ShootingConfig;
import com.me1q.summerFestival.game.shooting.constants.ShootingMessage;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class ShootingSession {

    private final Player player;
    private final SummerFestival plugin;
    private final Runnable onComplete;
    private int score;
    private int timeRemaining;
    private boolean isActive;
    private BukkitTask countdownTask;

    public ShootingSession(Player player, SummerFestival plugin, Runnable onComplete) {
        this.player = player;
        this.plugin = plugin;
        this.onComplete = onComplete;
        this.score = 0;
        this.timeRemaining = ShootingConfig.GAME_DURATION_SECONDS.value();
        this.isActive = false;
    }

    public void start() {
        if (isActive) {
            player.sendMessage(MessageBuilder.error(ShootingMessage.ALREADY_IN_GAME.text()));
            return;
        }

        isActive = true;
        resetGameState();
        showGameStartMessage();
        giveGameItems();
        startCountdown();
    }

    private void resetGameState() {
        score = 0;
        timeRemaining = ShootingConfig.GAME_DURATION_SECONDS.value();
    }

    private void showGameStartMessage() {
        player.sendMessage(MessageBuilder.header(ShootingMessage.GAME_START_TITLE.text()));
        player.sendMessage(MessageBuilder.warning(ShootingMessage.GAME_DURATION_INFO.text()));
    }

    public void stop() {
        if (!isActive) {
            return;
        }

        isActive = false;
        cancelCountdown();
        showGameResults();
        clearPlayerInventory();
        onComplete.run();
    }

    private void cancelCountdown() {
        if (countdownTask != null) {
            countdownTask.cancel();
        }
    }

    private void startCountdown() {
        countdownTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (!isActive || !player.isOnline()) {
                    cancel();
                    return;
                }

                timeRemaining--;
                updatePlayerActionBar();
                showTimeWarning();

                if (timeRemaining <= 0) {
                    stop();
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    private void updatePlayerActionBar() {
        Component actionBar = Component.text("残り時間: " + timeRemaining + "秒 | ",
                NamedTextColor.YELLOW)
            .append(Component.text("スコア: " + score + " | ", NamedTextColor.GOLD))
            .append(createTimeBar());
        player.sendActionBar(actionBar);
    }

    private void showTimeWarning() {
        if (ShootingAnnouncement.shouldShowTimeWarning(timeRemaining)) {
            player.sendMessage(Component.text(timeRemaining).color(NamedTextColor.RED));
        }
    }

    private Component createTimeBar() {
        int filledBars = (timeRemaining * ShootingConfig.TIME_BAR_LENGTH.value())
            / ShootingConfig.GAME_DURATION_SECONDS.value();
        TextComponent.Builder bar = Component.text();

        for (int i = 0; i < ShootingConfig.TIME_BAR_LENGTH.value(); i++) {
            NamedTextColor color = i < filledBars ? NamedTextColor.GREEN : NamedTextColor.RED;
            bar.append(Component.text("█", color));
        }

        return bar.build();
    }

    private void giveGameItems() {
        player.getInventory().addItem(new ItemStack(Material.BOW, 1));
        player.getInventory().addItem(new ItemStack(Material.ARROW, 64));
    }

    private void clearPlayerInventory() {
        player.getInventory().remove(Material.BOW);
        player.getInventory().remove(Material.ARROW);
    }

    private void showGameResults() {
        player.sendMessage(MessageBuilder.header("ゲーム終了"));
        player.sendMessage(Component.text("あなたのスコア: ", NamedTextColor.YELLOW)
            .append(Component.text(score + " ポイント", NamedTextColor.GOLD)));

        showRankMessage();
        broadcastScore();
    }

    private void showRankMessage() {
        ScoreRank rank = ScoreRank.fromScore(score);
        player.sendMessage(Component.text(rank.getMessage()).color(rank.getColor()));
    }

    private void broadcastScore() {
        Component broadcast = Component.text(player.getName() + " が射的ゲームで ",
                NamedTextColor.AQUA)
            .append(Component.text(score + " ポイント", NamedTextColor.GOLD))
            .append(Component.text(" を獲得しました！", NamedTextColor.AQUA));

        Bukkit.getOnlinePlayers().forEach(p -> p.sendMessage(broadcast));
    }

    public void addScore(int points) {
        if (!isActive) {
            return;
        }

        score += points;
        player.sendMessage(MessageBuilder.success("HIT! +" + points + " pt"));
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
    }

    public boolean isActive() {
        return isActive;
    }
}

