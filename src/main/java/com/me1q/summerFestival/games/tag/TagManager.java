package com.me1q.summerFestival.games.tag;

import com.me1q.summerFestival.SummerFestival;
import com.me1q.summerFestival.core.message.MessageBuilder;
import com.me1q.summerFestival.games.tag.session.TagRecruitSession;
import com.me1q.summerFestival.games.tag.session.TagSession;
import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class TagManager implements Listener {

    private static final int MIN_PLAYERS = 2;

    private final SummerFestival plugin;
    private TagSession activeSession;
    private TagRecruitSession recruitSession;

    public TagManager(SummerFestival plugin) {
        this.plugin = plugin;
    }

    public void startRecruit(Player starter, int duration) {
        if (isGameActive()) {
            starter.sendMessage(MessageBuilder.error("すでに開始されています！"));
            return;
        }

        if (isRecruitActive()) {
            starter.sendMessage(MessageBuilder.error("すでに募集中です！"));
            return;
        }

        recruitSession = new TagRecruitSession(starter, duration);
        recruitSession.start();
    }

    public void joinRecruit(Player player) {
        if (!isRecruitActive()) {
            player.sendMessage(MessageBuilder.error("現在募集は行われていません。"));
            return;
        }

        recruitSession.addPlayer(player);
    }

    public void cancelRecruit(Player player) {
        if (!isRecruitActive()) {
            player.sendMessage(MessageBuilder.error("現在募集は行われていません。"));
            return;
        }

        recruitSession.cancel();
        recruitSession = null;
    }

    public void startGame(Player starter) {
        if (isGameActive()) {
            starter.sendMessage(MessageBuilder.error("すでに開始されています！"));
            return;
        }

        if (!isRecruitActive()) {
            starter.sendMessage(
                MessageBuilder.error("先に /tag recruit で募集を開始してください。"));
            return;
        }

        List<Player> players = recruitSession.getPlayers();
        if (players.size() < MIN_PLAYERS) {
            starter.sendMessage(MessageBuilder.error("プレイヤーが足りません！最低2人必要です。"));
            return;
        }

        int duration = recruitSession.getGameDuration();
        recruitSession.stop();
        recruitSession = null;

        activeSession = new TagSession(plugin, players, duration);
        activeSession.start();
    }

    public void stopGame(Player player) {
        if (!isGameActive()) {
            player.sendMessage(MessageBuilder.error("増え鬼は開始されていません。"));
            return;
        }

        activeSession.stop();
        player.sendMessage(MessageBuilder.warning("増え鬼を終了しました。"));
    }

    @EventHandler
    public void onPlayerHit(EntityDamageByEntityEvent event) {
        if (!isGameActive()) {
            return;
        }

        if (event.getDamager() instanceof Player damager
            && event.getEntity() instanceof Player victim) {
            if (activeSession.handleTouch(damager, victim)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (isGameActive()) {
            activeSession.removePlayer(event.getPlayer());
        }
        if (isRecruitActive()) {
            recruitSession.removePlayer(event.getPlayer());
        }
    }

    private boolean isGameActive() {
        return activeSession != null && activeSession.isActive();
    }

    private boolean isRecruitActive() {
        return recruitSession != null && recruitSession.isActive();
    }
}

