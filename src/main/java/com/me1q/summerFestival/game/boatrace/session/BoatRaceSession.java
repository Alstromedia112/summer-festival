package com.me1q.summerFestival.game.boatrace.session;

import com.me1q.summerFestival.SummerFestival;
import com.me1q.summerFestival.core.message.MessageBuilder;
import com.me1q.summerFestival.game.boatrace.constants.Config;
import com.me1q.summerFestival.game.boatrace.constants.Message;
import com.me1q.summerFestival.game.boatrace.constants.Messages;
import com.me1q.summerFestival.game.boatrace.goal.GoalLineManager.GoalLine;
import com.me1q.summerFestival.game.boatrace.listener.BoatRaceListener;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.Title.Times;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class BoatRaceSession {

    private final SummerFestival plugin;
    private final List<Player> participants;
    private final Player organizer;
    private final Runnable onComplete;
    private final GoalLine goalLine;

    private final Map<Player, Long> finishTimes;
    private final List<Player> rankings;
    private final Map<Player, Integer> playerLaps;
    private final Map<Player, Long> lastGoalCrossTime;

    private boolean isActive;
    private boolean raceStarted;
    private long startTime;
    private BukkitTask countdownTask;
    private BoatRaceListener raceListener;

    public BoatRaceSession(SummerFestival plugin, List<Player> participants,
        Player organizer, GoalLine goalLine, Runnable onComplete) {
        this.plugin = plugin;
        this.participants = new ArrayList<>(participants);
        this.organizer = organizer;
        this.goalLine = goalLine;
        this.onComplete = onComplete;
        this.finishTimes = new HashMap<>();
        this.rankings = new ArrayList<>();
        this.playerLaps = new HashMap<>();
        this.lastGoalCrossTime = new HashMap<>();
        this.isActive = false;
        this.raceStarted = false;
    }

    public void start() {
        if (isActive) {
            return;
        }

        isActive = true;
        raceStarted = false;

        raceListener = new BoatRaceListener(this);
        Bukkit.getPluginManager().registerEvents(raceListener, plugin);

        broadcastToParticipants(MessageBuilder.header(Message.RACE_STARTED.text()));
        broadcastToParticipants(
            MessageBuilder.warning(Message.COUNTDOWN_WARNING.text()));

        startCountdown();
    }

    public void stop() {
        if (!isActive) {
            return;
        }

        isActive = false;
        raceStarted = false;
        cancelCountdown();

        if (raceListener != null) {
            HandlerList.unregisterAll(raceListener);
            raceListener = null;
        }

        showFinalResults();
        onComplete.run();
    }

    private void startCountdown() {
        final int[] countdown = {Config.COUNTDOWN_SECONDS.value()};

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
                    Title title = Title.title(
                        Component.text(Message.START_TEXT.text()),
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

    private void showFinalResults() {
        broadcastToParticipants(MessageBuilder.separator());
        broadcastToParticipants(
            Component.text(Message.RACE_RESULTS.text()).color(NamedTextColor.GOLD)
                .decorate(net.kyori.adventure.text.format.TextDecoration.BOLD));

        for (int i = 0; i < rankings.size(); i++) {
            Player player = rankings.get(i);
            long finishTime = finishTimes.get(player);
            double timeSeconds = (finishTime - startTime) / 1000.0;

            Component result = Component.text("   " + getRankText(i + 1) + "位: ")
                .color(getRankColor(i + 1))
                .append(Component.text(player.getName()).color(NamedTextColor.AQUA))
                .append(Component.text(" - ").color(NamedTextColor.GRAY))
                .append(Component.text(Messages.finishTime(timeSeconds))
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
        return participants.contains(player);
    }

    public boolean isOrganizer(Player player) {
        return organizer.equals(player);
    }

    public GoalLine getGoalLine() {
        return goalLine;
    }

    public void recordGoalLineCross(Player player) {
        if (finishTimes.containsKey(player)) {
            return;
        }

        long currentTime = System.currentTimeMillis();
        long delayMillis = SummerFestival.getInstance().getConfigManager()
            .getBoatRaceGoalDetectionDelaySeconds() * 1000L;

        Long lastCrossTime = lastGoalCrossTime.get(player);
        if (lastCrossTime != null && (currentTime - lastCrossTime) < delayMillis) {
            return;
        }

        int requiredLaps = SummerFestival.getInstance().getConfigManager().getBoatRaceLaps();
        int currentLap = playerLaps.getOrDefault(player, 0) + 1;
        playerLaps.put(player, currentLap);
        lastGoalCrossTime.put(player, currentTime);

        if (currentLap < requiredLaps) {
            Component lapMessage = Component.text(player.getName()).color(NamedTextColor.AQUA)
                .append(Component.text(" が").color(NamedTextColor.GRAY))
                .append(Component.text(currentLap + "周目").color(NamedTextColor.YELLOW))
                .append(Component.text("を通過！").color(NamedTextColor.GRAY));
            broadcastToParticipants(lapMessage);

            player.showTitle(
                Title.title(Component.text(""), Component.text(currentLap + "/" + requiredLaps)));

            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.5f);
            return;
        }

        long finishTime = System.currentTimeMillis();
        finishTimes.put(player, finishTime);
        rankings.add(player);

        double timeSeconds = (finishTime - startTime) / 1000.0;
        int rank = rankings.size();

        Component finishMessage = Component.text(player.getName()).color(NamedTextColor.AQUA)
            .append(Component.text(" がゴール！ ").color(NamedTextColor.GRAY))
            .append(Component.text(getRankText(rank) + "位").color(getRankColor(rank)))
            .append(Component.text(" - ").color(NamedTextColor.GRAY))
            .append(Component.text(Messages.finishTime(timeSeconds))
                .color(NamedTextColor.YELLOW));

        broadcastToParticipants(finishMessage);

        player.showTitle(
            Title.title(Component.text(getRankText(rank) + "位").color(getRankColor(rank)),
                Component.text("タイム: " + Messages.finishTime(timeSeconds))
                    .color(NamedTextColor.AQUA)));
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);

        if (rankings.size() >= participants.size()) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    stop();
                }
            }.runTaskLater(plugin, Config.RESULT_DISPLAY_DELAY_TICKS.value());
        }
    }

    public boolean hasFinished(Player player) {
        return finishTimes.containsKey(player);
    }

    public boolean canDetectGoal() {
        if (!raceStarted) {
            return false;
        }
        long elapsedMillis = System.currentTimeMillis() - startTime;
        long delaySeconds = SummerFestival.getInstance().getConfigManager()
            .getBoatRaceGoalDetectionDelaySeconds();
        return elapsedMillis >= (delaySeconds * 1000L);
    }
}
