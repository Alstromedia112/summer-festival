package com.me1q.summerFestival.game.tag.session;

import com.me1q.summerFestival.SummerFestival;
import com.me1q.summerFestival.core.message.MessageBuilder;
import com.me1q.summerFestival.game.tag.constants.TagAnnouncement;
import com.me1q.summerFestival.game.tag.constants.TagConfig;
import com.me1q.summerFestival.game.tag.constants.TagMessage;
import com.me1q.summerFestival.game.tag.player.Equipment;
import com.me1q.summerFestival.game.tag.player.PlayerRole;
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


    private final SummerFestival plugin;
    private final Set<Player> runners;
    private final Set<Player> taggers;
    private final Map<Player, PlayerRole> playerRoles;
    private boolean active;
    private BukkitTask gameTask;
    private int timeRemaining;

    public TagSession(SummerFestival plugin, List<Player> players, int duration) {
        this(plugin, players, duration, null);
    }

    public TagSession(SummerFestival plugin, List<Player> players, int duration,
        List<Player> initialTaggers) {
        this.plugin = plugin;
        this.runners = new HashSet<>();
        this.taggers = new HashSet<>();
        this.playerRoles = new HashMap<>();
        this.timeRemaining = duration;
        this.active = false;

        if (initialTaggers == null || initialTaggers.isEmpty()) {
            initializeRolesRandomly(players);
        } else {
            initializeRolesWithTaggers(players, initialTaggers);
        }
    }

    private void initializeRolesRandomly(List<Player> players) {
        Collections.shuffle(players);
        Player firstTagger = players.getFirst();

        taggers.add(firstTagger);
        playerRoles.put(firstTagger, PlayerRole.TAGGER);

        for (int i = 1; i < players.size(); i++) {
            Player runner = players.get(i);
            runners.add(runner);
            playerRoles.put(runner, PlayerRole.RUNNER);
        }
    }

    private void initializeRolesWithTaggers(List<Player> players, List<Player> initialTaggers) {
        Set<Player> taggerSet = new HashSet<>(initialTaggers);

        for (Player player : players) {
            if (taggerSet.contains(player)) {
                taggers.add(player);
                playerRoles.put(player, PlayerRole.TAGGER);
            } else {
                runners.add(player);
                playerRoles.put(player, PlayerRole.RUNNER);
            }
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
            Component.text(TagMessage.GAME_START.text()).color(NamedTextColor.GOLD)
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
        if (TagAnnouncement.shouldAnnounceTime(timeRemaining)) {
            broadcastMessage(MessageBuilder.warning(
                TagMessage.REMAINING_TIME.text() + timeRemaining + "秒"));
        }

        Component actionBar = Component.text(
            TagMessage.REMAINING_TIME.text() + timeRemaining + "秒 | 鬼: " + taggers.size()
                + " 逃げ: " + runners.size()
        ).color(NamedTextColor.AQUA);

        getAllPlayers().forEach(p -> {
            p.sendActionBar(actionBar);
            p.addPotionEffect(
                new PotionEffect(PotionEffectType.SATURATION, 1, 0, false, false, false));
        });
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

        player.showTitle(Title.title(
            Component.text(TagMessage.CAPTURED_TITLE.text()).color(NamedTextColor.RED),
            Component.text(TagMessage.BECAME_TAGGER.text()).color(NamedTextColor.GOLD)
                .decorate(TextDecoration.UNDERLINED)));

        player.addPotionEffects(List.of(
            new PotionEffect(PotionEffectType.BLINDNESS, TagConfig.BLINDNESS_DURATION_TICKS.value(),
                1, false, false, false),
            new PotionEffect(PotionEffectType.SLOWNESS, TagConfig.SLOWNESS_DURATION_TICKS.value(),
                TagConfig.SLOWNESS_AMPLIFIER.value(), false, false, false)
        ));

        broadcastMessage(Component.text(player.getName()).color(NamedTextColor.RED)
            .append(Component.text(TagMessage.PLAYER_CAUGHT.text()).color(NamedTextColor.YELLOW)));
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
            ? Component.text(TagMessage.TAGGER_WIN.text()).color(NamedTextColor.RED)
            : Component.text(TagMessage.RUNNER_WIN.text()).color(NamedTextColor.BLUE);

        Component subtitle = Component.text(TagMessage.GAME_END.text()).color(NamedTextColor.YELLOW)
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
        }.runTaskLater(plugin, TagConfig.VICTORY_DISPLAY_DELAY_TICKS.value());
    }

    private void cleanup() {
        getAllPlayers().forEach(Equipment::clearInventory);
    }

    private void broadcastMessage(Component message) {
        getAllPlayers().forEach(p -> p.sendMessage(message));
    }

    public List<Player> getAllPlayers() {
        List<Player> all = new java.util.ArrayList<>();
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
                MessageBuilder.warning(player.getName() + TagMessage.PLAYER_QUIT.text()));

            if (taggers.isEmpty() || runners.isEmpty()) {
                stop();
            }
        }
    }

    public boolean isActive() {
        return active;
    }

    public boolean isTagger(Player player) {
        return taggers.contains(player);
    }
}

