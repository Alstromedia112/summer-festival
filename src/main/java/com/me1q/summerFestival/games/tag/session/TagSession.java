package com.me1q.summerFestival.games.tag.session;

import com.me1q.summerFestival.SummerFestival;
import com.me1q.summerFestival.core.message.MessageBuilder;
import com.me1q.summerFestival.games.tag.player.Equipment;
import com.me1q.summerFestival.games.tag.player.PlayerRole;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.Title.Times;
import org.bukkit.GameMode;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class TagSession {

    private static final int[] TIME_ANNOUNCEMENTS = {30, 10, 5, 4, 3, 2, 1};

    private final SummerFestival plugin;
    private final Set<Player> runners;
    private final Set<Player> taggers;
    private final Map<Player, PlayerRole> playerRoles;
    private boolean active;
    private BukkitTask gameTask;
    private int timeRemaining;

    public TagSession(SummerFestival plugin, List<Player> players, int duration) {
        this.plugin = plugin;
        this.runners = new HashSet<>();
        this.taggers = new HashSet<>();
        this.playerRoles = new HashMap<>();
        this.timeRemaining = duration;
        this.active = false;

        initializeRoles(players);
    }

    private void initializeRoles(List<Player> players) {
        Collections.shuffle(players);
        Player firstTagger = players.get(0);

        taggers.add(firstTagger);
        playerRoles.put(firstTagger, PlayerRole.TAGGER);

        for (int i = 1; i < players.size(); i++) {
            Player runner = players.get(i);
            runners.add(runner);
            playerRoles.put(runner, PlayerRole.RUNNER);
        }
    }

    public void start() {
        active = true;
        prepareAllPlayers();
        startGameTimer();
    }

    private void prepareAllPlayers() {
        for (Player player : getAllPlayers()) {
            preparePlayer(player);
        }
    }

    private void preparePlayer(Player player) {
        player.setGameMode(GameMode.ADVENTURE);
        PlayerRole role = playerRoles.get(player);
        Equipment.equipPlayer(player, role);

        player.showTitle(Title.title(
            Component.text(role.getDisplayName()).color(role.getColor()),
            Component.text("GAME START").color(NamedTextColor.GOLD)
                .decorate(TextDecoration.UNDERLINED)
        ));

        player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_AMBIENT, 0.2f, 1.0f);
    }

    private void startGameTimer() {
        gameTask = new BukkitRunnable() {
            @Override
            public void run() {
                timeRemaining--;
                updateAllPlayers();

                if (timeRemaining <= 0) {
                    endGame(false);
                    cancel();
                } else if (runners.isEmpty()) {
                    endGame(true);
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 20L, 20L);
    }

    private void updateAllPlayers() {
        if (shouldAnnounceTime()) {
            broadcastMessage(MessageBuilder.warning("残り時間: " + timeRemaining + "秒"));
        }

        Component actionBar = Component.text(
            "残り時間: " + timeRemaining + "秒 | 鬼: " + taggers.size() + " 逃げ: " + runners.size()
        ).color(NamedTextColor.AQUA);

        getAllPlayers().forEach(p -> p.sendActionBar(actionBar));
    }

    private boolean shouldAnnounceTime() {
        for (int time : TIME_ANNOUNCEMENTS) {
            if (timeRemaining == time) {
                return true;
            }
        }
        return false;
    }

    public void stop() {
        if (gameTask != null) {
            gameTask.cancel();
        }
        cleanup();
        active = false;
    }

    public boolean handleTouch(Player damager, Player victim) {
        if (!active || !taggers.contains(damager) || !runners.contains(victim)) {
            return false;
        }

        convertToTagger(victim);
        playTouchSounds(damager, victim);

        if (runners.isEmpty()) {
            endGame(true);
        }

        return true;
    }

    private void convertToTagger(Player player) {
        runners.remove(player);
        taggers.add(player);
        playerRoles.put(player, PlayerRole.TAGGER);

        Equipment.equipPlayer(player, PlayerRole.TAGGER);

        player.showTitle(Title.title(Component.text("捕まった").color(NamedTextColor.RED),
            Component.text("あなたは鬼になった").color(NamedTextColor.GOLD)
                .decorate(TextDecoration.UNDERLINED)));

        player.addPotionEffects(List.of(
            new PotionEffect(PotionEffectType.BLINDNESS, 60, 1, false, false, false),
            new PotionEffect(PotionEffectType.SLOWNESS, 60, 10, false, false, false)
        ));

        broadcastMessage(Component.text(player.getName()).color(NamedTextColor.RED)
            .append(Component.text(" が鬼に捕まった！").color(NamedTextColor.YELLOW)));
    }

    private void playTouchSounds(Player tagger, Player victim) {
        victim.playSound(victim.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1.0f, 1.0f);
        tagger.playSound(tagger.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
    }

    private void endGame(boolean taggersWin) {
        active = false;
        if (gameTask != null) {
            gameTask.cancel();
        }

        showVictoryTitle(taggersWin);
        playEffects();
        scheduleCleanup();
    }

    private void showVictoryTitle(boolean taggersWin) {
        Component title = taggersWin
            ? Component.text("✞ 鬼の勝利 ✞").color(NamedTextColor.RED)
            : Component.text("✞ 逃げ側の勝利 ✞").color(NamedTextColor.BLUE);

        Component subtitle = Component.text("GAME END").color(NamedTextColor.YELLOW)
            .decorate(TextDecoration.UNDERLINED);

        Title victoryTitle = Title.title(title, subtitle,
            Times.times(Duration.ofMillis(500), Duration.ofMillis(3000),
                Duration.ofMillis(1000)));

        getAllPlayers().forEach(p -> p.showTitle(victoryTitle));
    }

    private void playEffects() {
        for (Player player : getAllPlayers()) {
            player.getWorld()
                .spawnParticle(Particle.FIREWORK, player.getLocation(), 50, 1, 1, 1, 0.1);
            player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1.0f, 1.0f);
        }
    }

    private void scheduleCleanup() {
        new BukkitRunnable() {
            @Override
            public void run() {
                cleanup();
            }
        }.runTaskLater(plugin, 100L);
    }

    private void cleanup() {
        getAllPlayers().forEach(Equipment::clearInventory);
    }

    private void broadcastMessage(Component message) {
        getAllPlayers().forEach(p -> p.sendMessage(message));
    }

    private Set<Player> getAllPlayers() {
        Set<Player> all = new HashSet<>();
        all.addAll(taggers);
        all.addAll(runners);
        return all;
    }

    public void removePlayer(Player player) {
        taggers.remove(player);
        runners.remove(player);
        playerRoles.remove(player);

        if (active) {
            broadcastMessage(
                MessageBuilder.warning(player.getName() + " がゲームから退出しました。"));

            if (taggers.isEmpty() || runners.isEmpty()) {
                stop();
            }
        }
    }

    public boolean isActive() {
        return active;
    }
}

