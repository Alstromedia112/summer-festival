package com.me1q.summerFestival.currency;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class CurrencyManager implements Listener {

    private static final int DEFAULT_BALANCE = 0;

    private final Map<UUID, Integer> balances;
    private final CurrencyScoreboard scoreboard;

    public CurrencyManager() {
        this.balances = new HashMap<>();
        this.scoreboard = new CurrencyScoreboard();
    }

    public int getBalance(Player player) {
        return balances.getOrDefault(player.getUniqueId(), DEFAULT_BALANCE);
    }

    public void setBalance(Player player, int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Balance cannot be negative");
        }
        balances.put(player.getUniqueId(), amount);
        scoreboard.updateBalance(player, amount);
    }

    public void addBalance(Player player, int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }
        int currentBalance = getBalance(player);
        int newBalance = currentBalance + amount;
        setBalance(player, newBalance);
    }

    public boolean removeBalance(Player player, int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }
        int currentBalance = getBalance(player);
        if (currentBalance < amount) {
            return false;
        }
        setBalance(player, currentBalance - amount);
        return true;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        int balance = getBalance(player);
        scoreboard.createScoreboard(player, balance);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        scoreboard.removeScoreboard(player);
    }

    public void shutdown() {
        scoreboard.clearAll();
        balances.clear();
    }
}

