package com.me1q.summerFestival.game.boatrace.session;

import com.me1q.summerFestival.SummerFestival;
import com.me1q.summerFestival.core.message.MessageBuilder;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.Title.Times;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class BoatRaceSession {

    private static final int COUNTDOWN_SECONDS = 5;

    private final SummerFestival plugin;
    private final List<Player> participants;
    private final Player organizer;
    private final Runnable onComplete;

    private final Map<Player, Long> finishTimes;
    private final List<Player> rankings;

    private boolean isActive;
    private boolean raceStarted;
    private long startTime;
    private BukkitTask countdownTask;

    public BoatRaceSession(SummerFestival plugin, List<Player> participants,
        Player organizer, Runnable onComplete) {
        this.plugin = plugin;
        this.participants = new ArrayList<>(participants);
        this.organizer = organizer;
        this.onComplete = onComplete;
        this.finishTimes = new HashMap<>();
        this.rankings = new ArrayList<>();
        this.isActive = false;
        this.raceStarted = false;
    }

    public void start() {
        if (isActive) {
            return;
        }

        isActive = true;
        raceStarted = false;

        broadcastToParticipants(MessageBuilder.header("ボートレース開始！"));
        broadcastToParticipants(MessageBuilder.warning("カウントダウン後にスタートします"));

        startCountdown();
    }

    public void stop() {
        if (!isActive) {
            return;
        }

        isActive = false;
        raceStarted = false;
        cancelCountdown();

        showFinalResults();
        onComplete.run();
    }

    private void startCountdown() {
        final int[] countdown = {COUNTDOWN_SECONDS};

        countdownTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (!isActive) {
                    cancel();
                    return;
                }

                if (countdown[0] > 0) {
                    Title title = Title.title(
                        Component.text(countdown[0]),
                        Component.empty(),
                        Times.times(Duration.ofMillis(250), Duration.ofMillis(750),
                            Duration.ofMillis(250))
                    );

                    for (Player player : participants) {
                        if (player.isOnline()) {
                            player.showTitle(title);
                            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 1.0f,
                                1.0f);
                        }
                    }
                    countdown[0]--;
                } else {
                    // Start the race
                    Title title = Title.title(
                        Component.text("スタート！"),
                        Component.empty(),
                        Times.times(Duration.ofMillis(250), Duration.ofSeconds(1),
                            Duration.ofMillis(250))
                    );

                    for (Player player : participants) {
                        if (player.isOnline()) {
                            player.showTitle(title);
                            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL,
                                1.0f, 2.0f);
                        }
                    }

                    raceStarted = true;
                    startTime = System.currentTimeMillis();

                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    private void cancelCountdown() {
        if (countdownTask != null) {
            countdownTask.cancel();
        }
    }

    public void playerReachGoal(Player player) {
        if (!raceStarted || finishTimes.containsKey(player)) {
            return;
        }

        long finishTime = System.currentTimeMillis();
        double timeSeconds = (finishTime - startTime) / 1000.0;

        finishTimes.put(player, finishTime);
        rankings.add(player);

        int rank = rankings.size();

        // Show rank to the player
        Title title = Title.title(
            Component.text(getRankText(rank) + "位!").color(getRankColor(rank)),
            Component.text(String.format("タイム: %.2f秒", timeSeconds)),
            Times.times(Duration.ofMillis(500), Duration.ofSeconds(2), Duration.ofMillis(500))
        );
        player.showTitle(title);
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);

        // Broadcast to all participants
        Component announcement = Component.text(player.getName()).color(NamedTextColor.AQUA)
            .append(Component.text(" が").color(NamedTextColor.GRAY))
            .append(Component.text(getRankText(rank) + "位").color(getRankColor(rank)))
            .append(Component.text("でゴール！ (").color(NamedTextColor.GRAY))
            .append(
                Component.text(String.format("%.2f秒", timeSeconds)).color(NamedTextColor.YELLOW))
            .append(Component.text(")").color(NamedTextColor.GRAY));

        broadcastToParticipants(announcement);

        // Check if all players finished
        if (rankings.size() == participants.size()) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    stop();
                }
            }.runTaskLater(plugin, 60L); // Wait 3 seconds before showing final results
        }
    }

    private void showFinalResults() {
        broadcastToParticipants(MessageBuilder.separator());
        broadcastToParticipants(Component.text("   レース結果").color(NamedTextColor.GOLD)
            .decorate(net.kyori.adventure.text.format.TextDecoration.BOLD));

        for (int i = 0; i < rankings.size(); i++) {
            Player player = rankings.get(i);
            long finishTime = finishTimes.get(player);
            double timeSeconds = (finishTime - startTime) / 1000.0;

            Component result = Component.text("   " + getRankText(i + 1) + "位: ")
                .color(getRankColor(i + 1))
                .append(Component.text(player.getName()).color(NamedTextColor.AQUA))
                .append(Component.text(" - ").color(NamedTextColor.GRAY))
                .append(Component.text(String.format("%.2f秒", timeSeconds))
                    .color(NamedTextColor.YELLOW));

            broadcastToParticipants(result);
        }

        broadcastToParticipants(MessageBuilder.separator());
    }

    private String getRankText(int rank) {
        return switch (rank) {
            case 1 -> "1";
            case 2 -> "2";
            case 3 -> "3";
            default -> String.valueOf(rank);
        };
    }

    private NamedTextColor getRankColor(int rank) {
        return switch (rank) {
            case 1 -> NamedTextColor.GOLD;
            case 2 -> NamedTextColor.GRAY;
            case 3 -> NamedTextColor.DARK_RED;
            default -> NamedTextColor.WHITE;
        };
    }

    private void broadcastToParticipants(Component message) {
        participants.forEach(p -> {
            if (p.isOnline()) {
                p.sendMessage(message);
            }
        });
    }

    public boolean isActive() {
        return isActive;
    }

    public boolean isRaceStarted() {
        return raceStarted;
    }

    public boolean isParticipant(Player player) {
        return !participants.contains(player);
    }

    public boolean isOrganizer(Player player) {
        return organizer.equals(player);
    }
}
