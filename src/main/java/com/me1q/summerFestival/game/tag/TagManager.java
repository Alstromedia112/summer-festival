package com.me1q.summerFestival.game.tag;

import com.me1q.summerFestival.SummerFestival;
import com.me1q.summerFestival.core.message.MessageBuilder;
import com.me1q.summerFestival.game.boatrace.returnpoint.ReturnPointManager;
import com.me1q.summerFestival.game.boatrace.returnpoint.ReturnPointMarkerItem;
import com.me1q.summerFestival.game.boatrace.returnpoint.listener.ReturnPointMarkerListener;
import com.me1q.summerFestival.game.tag.constants.TagConfig;
import com.me1q.summerFestival.game.tag.constants.TagMessage;
import com.me1q.summerFestival.game.tag.itemstand.ItemStandManager;
import com.me1q.summerFestival.game.tag.itemstand.listener.ItemStandListener;
import com.me1q.summerFestival.game.tag.listener.EquipmentEffectListener;
import com.me1q.summerFestival.game.tag.session.TagRecruitSession;
import com.me1q.summerFestival.game.tag.session.TagSession;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class TagManager implements Listener {


    private final SummerFestival plugin;
    private final ItemStandManager itemStandManager;
    private final ReturnPointManager returnPointManager;
    private TagSession activeSession;
    private TagRecruitSession recruitSession;

    public TagManager(SummerFestival plugin) {
        this.plugin = plugin;
        this.itemStandManager = new ItemStandManager();
        this.returnPointManager = new ReturnPointManager();
        registerListeners();
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(
            new ItemStandListener(itemStandManager, this, plugin), plugin);
        Bukkit.getPluginManager().registerEvents(
            new EquipmentEffectListener(this), plugin);
        Bukkit.getPluginManager().registerEvents(
            new ReturnPointMarkerListener(returnPointManager), plugin);
    }

    public ItemStandManager getItemStandManager() {
        return itemStandManager;
    }

    public ReturnPointManager getReturnPointManager() {
        return returnPointManager;
    }

    public TagSession getActiveSession() {
        return activeSession;
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
        startGame(starter, null);
    }

    public void startGame(Player starter, List<Player> initialTaggers) {
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

        if (initialTaggers != null && !initialTaggers.isEmpty()) {
            for (Player tagger : initialTaggers) {
                if (!players.contains(tagger)) {
                    starter.sendMessage(MessageBuilder.error(
                        tagger.getName() + "は募集に参加していません。"));
                    return;
                }
            }

            if (initialTaggers.size() >= players.size()) {
                starter.sendMessage(MessageBuilder.error(
                    "鬼の人数が参加者数以上です。少なくとも1人は逃げ側にする必要があります。"));
                return;
            }
        }

        int duration = recruitSession.getGameDuration();
        recruitSession.stop();
        recruitSession = null;

        activeSession = new TagSession(plugin, players, duration, initialTaggers);
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

    public void giveReturnPointMarker(Player player) {
        player.getInventory().addItem(ReturnPointMarkerItem.create());
        player.sendMessage(MessageBuilder.success("リターンポイントマーカーを取得しました"));
    }

    public void teleportToReturnPoint(Player player) {
        Location returnPoint = returnPointManager.getReturnPoint();

        if (returnPoint == null) {
            player.sendMessage(MessageBuilder.error("リターンポイントが設定されていません"));
            player.sendMessage(
                MessageBuilder.warning("/tag returnpoint でマーカーを取得してください"));
            return;
        }

        List<Player> targetPlayers = null;

        if (activeSession != null) {
            targetPlayers = activeSession.getAllPlayers();
        } else if (recruitSession != null) {
            targetPlayers = recruitSession.getPlayers();
        }

        if (targetPlayers == null || targetPlayers.isEmpty()) {
            player.sendMessage(
                MessageBuilder.error("テレポート対象のプレイヤーがいません"));
            return;
        }

        int teleportCount = 0;
        for (Player p : targetPlayers) {
            if (p.isOnline()) {
                p.teleport(returnPoint);
                teleportCount++;
            }
        }

        player.sendMessage(MessageBuilder.success(
            teleportCount + "人のプレイヤーをリターンポイントにテレポートしました"));
    }

    public void removePlayerFromReturnPointTargets(Player executor) {
        List<Player> removedPlayers = null;

        if (activeSession != null) {
            removedPlayers = List.copyOf(activeSession.getAllPlayers());
            for (Player p : List.copyOf(removedPlayers)) {
                activeSession.removePlayer(p);
            }
        } else if (recruitSession != null) {
            removedPlayers = List.copyOf(recruitSession.getPlayers());
            for (Player p : List.copyOf(removedPlayers)) {
                recruitSession.removePlayer(p);
            }
        }

        if (removedPlayers != null && !removedPlayers.isEmpty()) {
            executor.sendMessage(MessageBuilder.success(
                removedPlayers.size() + "人のプレイヤーをリターンポイント対象から削除しました"));
        } else {
            executor.sendMessage(MessageBuilder.error(
                "アクティブなセッションまたは募集がありません"));
        }
    }
}

