package com.me1q.summerFestival.games.boatrace.session;

import com.me1q.summerFestival.core.message.MessageBuilder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class BoatRaceRecruitSession {

    private final Player organizer;
    private final Set<Player> participants;
    private final int maxPlayers;
    private boolean active;

    public BoatRaceRecruitSession(Player organizer, int maxPlayers, boolean organizerParticipates) {
        this.organizer = organizer;
        this.participants = new HashSet<>();
        this.maxPlayers = maxPlayers;
        this.active = false;

        if (organizerParticipates) {
            participants.add(organizer);
        }
    }

    public void start() {
        active = true;
        broadcastRecruitMessage();
    }

    private void broadcastRecruitMessage() {
        Component message = MessageBuilder.separator()
            .append(Component.newline())
            .append(Component.text("   ボートレース参加者募集中！").color(NamedTextColor.YELLOW)
                .decorate(net.kyori.adventure.text.format.TextDecoration.BOLD))
            .append(Component.newline())
            .append(Component.text("   "))
            .append(MessageBuilder.clickable("[参加する]", NamedTextColor.GREEN, "/boatrace join",
                "クリックして参加"))
            .append(Component.newline())
            .append(Component.text("   定員: " + maxPlayers + "人").color(NamedTextColor.AQUA))
            .append(Component.newline())
            .append(MessageBuilder.separator());

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.sendMessage(message);
            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.5f);
        }
    }

    public void addPlayer(Player player) {
        if (participants.contains(player)) {
            player.sendMessage(MessageBuilder.warning("すでに参加しています！"));
            return;
        }

        if (participants.size() >= maxPlayers) {
            player.sendMessage(
                MessageBuilder.error("定員に達しています（最大" + maxPlayers + "人）"));
            return;
        }

        participants.add(player);
        player.sendMessage(MessageBuilder.success("ボートレースに参加しました！"));
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);

        broadcastParticipantJoined(player);
    }

    private void broadcastParticipantJoined(Player player) {
        Component broadcast = Component.text(player.getName()).color(NamedTextColor.AQUA)
            .append(Component.text(" が参加しました (").color(NamedTextColor.GRAY))
            .append(Component.text(participants.size()).color(NamedTextColor.YELLOW))
            .append(Component.text("/" + maxPlayers + "人)").color(NamedTextColor.GRAY));

        participants.forEach(p -> p.sendMessage(broadcast));
    }

    public void cancel() {
        active = false;
        broadcastToAll(MessageBuilder.warning("募集がキャンセルされました。"));
    }

    public void stop() {
        active = false;
        broadcastToAll(
            MessageBuilder.warning("募集を終了しました。参加者: " + participants.size() + "人"));
    }

    private void broadcastToAll(Component message) {
        Bukkit.getOnlinePlayers().forEach(p -> p.sendMessage(message));
    }

    public List<Player> getParticipants() {
        return new ArrayList<>(participants);
    }

    public boolean isActive() {
        return active;
    }

    public Player getOrganizer() {
        return organizer;
    }

    public int getParticipantCount() {
        return participants.size();
    }
}
