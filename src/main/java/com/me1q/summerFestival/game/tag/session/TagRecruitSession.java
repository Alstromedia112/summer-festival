package com.me1q.summerFestival.game.tag.session;

import com.me1q.summerFestival.core.message.MessageBuilder;
import com.me1q.summerFestival.game.boatrace.returnpoint.ReturnPointManager;
import com.me1q.summerFestival.game.tag.constants.TagMessage;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class TagRecruitSession {

    private final Set<Player> players;
    private final int gameDuration;
    private final ReturnPointManager returnPointManager;
    private boolean active;

    public TagRecruitSession(Player starter, int gameDuration,
        ReturnPointManager returnPointManager) {
        this.gameDuration = gameDuration;
        this.returnPointManager = returnPointManager;
        this.players = new HashSet<>();
        this.active = false;
        players.add(starter);
    }

    public void start() {
        active = true;
        broadcastRecruitMessage();
    }

    private void broadcastRecruitMessage() {
        Component message = MessageBuilder.separator()
            .append(Component.newline())
            .append(Component.text("   " + TagMessage.RECRUITMENT_HEADER.text())
                .color(NamedTextColor.YELLOW)
                .decorate(net.kyori.adventure.text.format.TextDecoration.BOLD))
            .append(Component.newline())
            .append(Component.text("   "))
            .append(MessageBuilder.clickable(
                TagMessage.JOIN_BUTTON.text(),
                NamedTextColor.GREEN,
                "/tag join",
                TagMessage.JOIN_BUTTON_HOVER.text()))
            .append(Component.newline())
            .append(Component.text("   " + TagMessage.GAME_DURATION.text() + gameDuration + "秒")
                .color(NamedTextColor.AQUA))
            .append(Component.newline())
            .append(MessageBuilder.separator());

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.sendMessage(message);
            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.5f);
        }
    }

    public void addPlayer(Player player) {
        if (players.contains(player)) {
            player.sendMessage(MessageBuilder.warning(TagMessage.ALREADY_JOINED.text()));
            return;
        }

        players.add(player);
        player.sendMessage(MessageBuilder.success(TagMessage.PLAYER_JOINED.text()));
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);

        teleportToReturnPoint(player);
        broadcastParticipantJoined(player);
    }

    private void teleportToReturnPoint(Player player) {
        Location returnPoint = returnPointManager.getReturnPoint();

        if (returnPoint == null) {
            player.sendMessage(MessageBuilder.warning("リターンポイントが設定されていません"));
            return;
        }

        player.teleport(returnPoint);
        player.sendMessage(MessageBuilder.success("リターンポイントにテレポートしました"));
    }

    private void broadcastParticipantJoined(Player player) {
        Component broadcast = Component.text(player.getName()).color(NamedTextColor.AQUA)
            .append(Component.text(" が参加しました (").color(NamedTextColor.GRAY))
            .append(Component.text(players.size()).color(NamedTextColor.YELLOW))
            .append(Component.text("人)").color(NamedTextColor.GRAY));

        players.forEach(p -> p.sendMessage(broadcast));
    }

    public void removePlayer(Player player) {
        players.remove(player);
    }

    public void cancel() {
        active = false;
        broadcastToAll(MessageBuilder.warning(TagMessage.RECRUITMENT_CANCELLED.text()));
    }

    public void stop() {
        active = false;
        broadcastToAll(
            MessageBuilder.warning("募集を終了しました。参加者: " + players.size() + "人"));
    }

    private void broadcastToAll(Component message) {
        Bukkit.getOnlinePlayers().forEach(p -> p.sendMessage(message));
    }

    public List<Player> getPlayers() {
        return new ArrayList<>(players);
    }

    public boolean isActive() {
        return active;
    }

    public int getGameDuration() {
        return gameDuration;
    }
}

