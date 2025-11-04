package com.me1q.summerFestival.game.shooting.spawner;

import com.me1q.summerFestival.SummerFestival;
import com.me1q.summerFestival.game.shooting.target.ShootingTarget;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class TargetSpawner {

    private static final long SPAWN_INTERVAL_TICKS = 20L;
    private static final long TARGET_LIFETIME_TICKS = 100L;

    private final SummerFestival plugin;
    private final Set<ShootingTarget> activeTargets;
    private BukkitTask spawnTask;

    public TargetSpawner(SummerFestival plugin) {
        this.plugin = plugin;
        this.activeTargets = new HashSet<>();
    }

    public void startSpawning(Player player, SpawnArea spawnArea, Supplier<Boolean> isActiveCheck) {
        spawnTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (!isActiveCheck.get()) {
                    cancel();
                    return;
                }
                spawnTarget(player, spawnArea);
            }
        }.runTaskTimer(plugin, SPAWN_INTERVAL_TICKS, SPAWN_INTERVAL_TICKS);
    }

    private void spawnTarget(Player player, SpawnArea spawnArea) {
        Location targetLocation = spawnArea.getRandomLocation(player.getWorld());
        ShootingTarget target = new ShootingTarget(targetLocation, player);
        activeTargets.add(target);
        target.spawn();

        scheduleTargetRemoval(target);
    }

    private void scheduleTargetRemoval(ShootingTarget target) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (activeTargets.contains(target)) {
                    target.remove();
                    activeTargets.remove(target);
                }
            }
        }.runTaskLater(plugin, TARGET_LIFETIME_TICKS);
    }

    public void stopSpawning() {
        if (spawnTask != null) {
            spawnTask.cancel();
        }
    }

    public void cleanupTargetsForPlayer(Player player) {
        Set<ShootingTarget> toRemove = new HashSet<>();
        for (ShootingTarget target : activeTargets) {
            if (target.getOwner().equals(player)) {
                target.remove();
                toRemove.add(target);
            }
        }
        activeTargets.removeAll(toRemove);
    }

    public Set<ShootingTarget> getActiveTargets() {
        return new HashSet<>(activeTargets);
    }

    public void removeTarget(ShootingTarget target) {
        target.remove();
        activeTargets.remove(target);
    }
}

