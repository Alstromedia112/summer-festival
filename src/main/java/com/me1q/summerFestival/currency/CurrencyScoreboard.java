package com.me1q.summerFestival.currency;

import java.util.HashMap;
import java.util.Map;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.RenderType;
import org.bukkit.scoreboard.Scoreboard;

public class CurrencyScoreboard {

    private static final String OBJECTIVE_NAME = "currency";
    private static final String DISPLAY_NAME = "所持金";

    private final Map<Player, Scoreboard> playerScoreboards;

    public CurrencyScoreboard() {
        this.playerScoreboards = new HashMap<>();
    }

    public void createScoreboard(Player player, int balance) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective(
            OBJECTIVE_NAME,
            Criteria.DUMMY,
            Component.text(DISPLAY_NAME).color(NamedTextColor.GOLD),
            RenderType.INTEGER
        );
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        updateScoreboardBalance(scoreboard, objective, balance);

        player.setScoreboard(scoreboard);
        playerScoreboards.put(player, scoreboard);
    }

    public void updateBalance(Player player, int balance) {
        Scoreboard scoreboard = playerScoreboards.get(player);
        if (scoreboard == null) {
            createScoreboard(player, balance);
            return;
        }

        Objective objective = scoreboard.getObjective(OBJECTIVE_NAME);
        if (objective == null) {
            return;
        }

        updateScoreboardBalance(scoreboard, objective, balance);
    }

    private void updateScoreboardBalance(Scoreboard scoreboard, Objective objective, int balance) {
        scoreboard.getEntries().forEach(scoreboard::resetScores);

        String formattedBalance = CurrencyFormatter.format(balance);
        objective.getScore(formattedBalance).setScore(1);
    }

    public void removeScoreboard(Player player) {
        Scoreboard scoreboard = playerScoreboards.remove(player);
        if (scoreboard != null) {
            player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        }
    }

    public void clearAll() {
        for (Player player : playerScoreboards.keySet()) {
            removeScoreboard(player);
        }
        playerScoreboards.clear();
    }
}


