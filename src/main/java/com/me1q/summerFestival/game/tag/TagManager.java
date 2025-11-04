package com.me1q.summerFestival.game.tag;

import com.me1q.summerFestival.SummerFestival;
import com.me1q.summerFestival.core.message.MessageBuilder;
import com.me1q.summerFestival.game.tag.constants.TagConfig;
import com.me1q.summerFestival.game.tag.constants.TagMessage;
import com.me1q.summerFestival.game.tag.session.TagRecruitSession;
import com.me1q.summerFestival.game.tag.session.TagSession;
import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class TagManager implements Listener {


    private final SummerFestival plugin;
    private TagSession activeSession;
    private TagRecruitSession recruitSession;

    public TagManager(SummerFestival plugin) {
        this.plugin = plugin;
    }

    public void startRecruit(Player starter, int duration) {
        if (isGameActive()) {
            starter.sendMessage(MessageBuilder.error(TagMessage.ALREADY_STARTED.text()));
            return;
        }

        if (isRecruitActive()) {
            starter.sendMessage(MessageBuilder.error(TagMessage.ALREADY_RECRUITING.text()));
            return;
        }

        recruitSession = new TagRecruitSession(starter, duration);
        recruitSession.start();
    }

    public void joinRecruit(Player player) {
        if (!isRecruitActive()) {
            player.sendMessage(MessageBuilder.error(TagMessage.NO_RECRUITMENT.text()));
            return;
        }

        recruitSession.addPlayer(player);
    }

    public void cancelRecruit(Player player) {
        if (!isRecruitActive()) {
            player.sendMessage(MessageBuilder.error(TagMessage.NO_RECRUITMENT.text()));
            return;
        }

        recruitSession.cancel();
        recruitSession = null;
    }

    public void startGame(Player starter) {
        if (isGameActive()) {
            starter.sendMessage(MessageBuilder.error(TagMessage.ALREADY_STARTED.text()));
            return;
        }

        if (!isRecruitActive()) {
            starter.sendMessage(
                MessageBuilder.error(TagMessage.START_RECRUITMENT_FIRST.text()));
            return;
        }

        List<Player> players = recruitSession.getPlayers();
        if (players.size() < TagConfig.MIN_PLAYERS.value()) {
            starter.sendMessage(MessageBuilder.error(TagMessage.MIN_PLAYERS_ERROR.text()));
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
            player.sendMessage(MessageBuilder.error(TagMessage.NOT_STARTED.text()));
            return;
        }

        activeSession.stop();
        player.sendMessage(MessageBuilder.warning(TagMessage.GAME_STOPPED.text()));
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

