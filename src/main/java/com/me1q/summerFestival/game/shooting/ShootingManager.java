package com.me1q.summerFestival.game.shooting;

import com.me1q.summerFestival.SummerFestival;
import com.me1q.summerFestival.core.message.MessageBuilder;
import com.me1q.summerFestival.currency.CurrencyManager;
import com.me1q.summerFestival.game.shooting.constants.ShootingConfig;
import com.me1q.summerFestival.game.shooting.constants.ShootingMessage;
import com.me1q.summerFestival.game.shooting.session.ShootingSession;
import com.me1q.summerFestival.game.shooting.spawner.SpawnArea;
import com.me1q.summerFestival.game.shooting.spawner.TargetSpawner;
import com.me1q.summerFestival.game.shooting.target.ShootingTarget;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class ShootingManager implements Listener {


    private final SummerFestival plugin;
    private final CurrencyManager currencyManager;
    private final Map<Player, ShootingSession> activeSessions;
    private final Map<Player, TargetSpawner> playerSpawners;
    private final Map<SpawnArea, Player> activeSpawnAreas;
    private final Map<Player, SpawnArea> playerSpawnAreas;

    public ShootingManager(SummerFestival plugin, CurrencyManager currencyManager) {
        this.plugin = plugin;
        this.currencyManager = currencyManager;
        this.activeSessions = new HashMap<>();
        this.playerSpawners = new HashMap<>();
        this.activeSpawnAreas = new HashMap<>();
        this.playerSpawnAreas = new HashMap<>();
    }

    public void startGame(Player player, double x, double y, double z, double dx, double dy,
        double dz) {
        if (isPlayerInGame(player)) {
            player.sendMessage(MessageBuilder.error(ShootingMessage.ALREADY_IN_GAME.text()));
            return;
        }

        SpawnArea spawnArea = new SpawnArea(x, y, z, dx, dy, dz);

        if (isSpawnAreaInUse(spawnArea)) {
            Player usingPlayer = activeSpawnAreas.get(spawnArea);
            player.sendMessage(MessageBuilder.error(
                "この射的場は現在 " + usingPlayer.getName() + " が使用中です。"));
            return;
        }

        TargetSpawner spawner = new TargetSpawner(plugin);
        playerSpawners.put(player, spawner);

        activeSpawnAreas.put(spawnArea, player);
        playerSpawnAreas.put(player, spawnArea);

        ShootingSession session = new ShootingSession(player, plugin, currencyManager,
            () -> cleanupPlayerSession(player));
        activeSessions.put(player, session);
        session.start();

        spawner.startSpawning(player, spawnArea, () -> isSessionActive(player));
    }

    public void stopGame(Player player) {
        ShootingSession session = activeSessions.get(player);
        if (session == null) {
            player.sendMessage(MessageBuilder.error(ShootingMessage.NOT_IN_GAME.text()));
            return;
        }

        session.stop();
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof Arrow arrow)) {
            return;
        }

        if (!(arrow.getShooter() instanceof Player shooter)) {
            return;
        }

        ShootingSession session = activeSessions.get(shooter);
        if (session == null || !session.isActive()) {
            return;
        }

        handleTargetHit(shooter, arrow.getLocation());
        scheduleArrowCleanup(arrow);
    }

    private void handleTargetHit(Player shooter, Location arrowLocation) {
        TargetSpawner spawner = playerSpawners.get(shooter);
        if (spawner == null) {
            return;
        }

        for (ShootingTarget target : new HashSet<>(spawner.getActiveTargets())) {
            if (target.getOwner().equals(shooter) && target.isHit(arrowLocation)) {
                ShootingSession session = activeSessions.get(shooter);
                session.addScore(target.getPoints());
                spawner.removeTarget(target);
                break;
            }
        }
    }

    private void scheduleArrowCleanup(Arrow arrow) {
        new BukkitRunnable() {
            @Override
            public void run() {
                arrow.remove();
            }
        }.runTaskLater(plugin, ShootingConfig.ARROW_CLEANUP_DELAY_TICKS.value());
    }

    private void cleanupPlayerSession(Player player) {
        activeSessions.remove(player);

        SpawnArea spawnArea = playerSpawnAreas.remove(player);
        if (spawnArea != null) {
            activeSpawnAreas.remove(spawnArea);
        }

        TargetSpawner spawner = playerSpawners.remove(player);
        if (spawner != null) {
            spawner.stopSpawning();
            spawner.cleanupTargetsForPlayer(player);
        }
    }

    private boolean isSessionActive(Player player) {
        ShootingSession session = activeSessions.get(player);
        return session != null && session.isActive();
    }

    public boolean isPlayerInGame(Player player) {
        return activeSessions.containsKey(player);
    }

    public boolean isSpawnAreaInUse(SpawnArea spawnArea) {
        return activeSpawnAreas.containsKey(spawnArea);
    }
}

