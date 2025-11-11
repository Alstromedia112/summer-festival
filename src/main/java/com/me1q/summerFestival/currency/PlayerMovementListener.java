package com.me1q.summerFestival.currency;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerMovementListener implements Listener {

    private static final double DISTANCE_PER_REWARD = 10.0;
    private static final int DEFAULT_REWARD_AMOUNT = 1;

    private final CurrencyManager currencyManager;
    private final Map<UUID, Location> lastLocations;
    private final Map<UUID, Double> accumulatedDistances;
    private int rewardAmount;

    public PlayerMovementListener(CurrencyManager currencyManager) {
        this.currencyManager = currencyManager;
        this.lastLocations = new HashMap<>();
        this.accumulatedDistances = new HashMap<>();
        this.rewardAmount = DEFAULT_REWARD_AMOUNT;
    }

    public int getRewardAmount() {
        return rewardAmount;
    }

    public void setRewardAmount(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Reward amount cannot be negative");
        }
        this.rewardAmount = amount;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        lastLocations.put(player.getUniqueId(), player.getLocation());
        accumulatedDistances.put(player.getUniqueId(), 0.0);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location to = event.getTo();
        Location from = event.getFrom();

        if (from.getBlockX() == to.getBlockX() &&
            from.getBlockY() == to.getBlockY() &&
            from.getBlockZ() == to.getBlockZ()) {
            return;
        }

        UUID playerId = player.getUniqueId();
        Location lastLocation = lastLocations.get(playerId);

        if (lastLocation == null) {
            lastLocations.put(playerId, to);
            accumulatedDistances.put(playerId, 0.0);
            return;
        }

        if (from.getWorld() != to.getWorld()) {
            lastLocations.put(playerId, to);
            return;
        }

        double distance = lastLocation.distance(to);
        double accumulated = accumulatedDistances.getOrDefault(playerId, 0.0) + distance;

        if (accumulated >= DISTANCE_PER_REWARD) {
            int rewardCount = (int) (accumulated / DISTANCE_PER_REWARD);
            currencyManager.addBalance(player, rewardCount * rewardAmount);
            accumulated = accumulated % DISTANCE_PER_REWARD;
        }

        accumulatedDistances.put(playerId, accumulated);
        lastLocations.put(playerId, to);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID playerId = event.getPlayer().getUniqueId();
        lastLocations.remove(playerId);
        accumulatedDistances.remove(playerId);
    }

    public void shutdown() {
        lastLocations.clear();
        accumulatedDistances.clear();
    }
}

