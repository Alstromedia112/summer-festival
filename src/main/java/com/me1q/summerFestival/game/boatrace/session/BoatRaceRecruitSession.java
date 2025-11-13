package com.me1q.summerFestival.game.boatrace.session;

import com.me1q.summerFestival.core.message.MessageBuilder;
import com.me1q.summerFestival.game.boatrace.constants.Message;
import com.me1q.summerFestival.game.boatrace.constants.Messages;
import com.me1q.summerFestival.game.boatrace.constants.RecruitmentMode;
import java.util.ArrayList;
import java.util.Collections;
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
    private final Set<Player> spectators;
    private final int maxPlayers;
    private final RecruitmentMode mode;
    private boolean active;
    private List<Player> selectedParticipants;
    private boolean lotteryDrawn;

    public BoatRaceRecruitSession(Player organizer, int maxPlayers, boolean organizerParticipates,
        RecruitmentMode mode) {
        this.organizer = organizer;
        this.participants = new HashSet<>();
        this.spectators = new HashSet<>();
        this.maxPlayers = maxPlayers;
        this.mode = mode;
        this.active = false;
        this.selectedParticipants = null;
        this.lotteryDrawn = false;

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
            .append(Component.text("   ボートレース参加者募集中! (Tキーを押して参加をクリック)")
                .color(NamedTextColor.YELLOW)
                .decorate(net.kyori.adventure.text.format.TextDecoration.BOLD))
            .append(Component.newline())
            .append(Component.text("   "))
            .append(MessageBuilder.clickable("[参加]", NamedTextColor.GREEN, "/boatrace join",
                "クリックで参加"))
            .append(Component.newline())
            .append(Component.text("   定員: " + maxPlayers + "人").color(NamedTextColor.AQUA))
            .append(Component.newline())
            .append(Component.text("   方式: " + mode.getDisplayName()).color(NamedTextColor.GOLD))
            .append(Component.newline())
            .append(MessageBuilder.separator());

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.sendMessage(message);
            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.5f);
        }
    }

    public void addPlayer(Player player) {
        if (participants.contains(player)) {
            player.sendMessage(MessageBuilder.warning(Message.ALREADY_JOINED.text()));
            return;
        }

        if (spectators.contains(player)) {
            player.sendMessage(MessageBuilder.warning("既に観戦者として参加しています"));
            return;
        }

        if (mode == RecruitmentMode.FIRST_COME && participants.size() >= maxPlayers) {
            player.sendMessage(
                MessageBuilder.error(Messages.maxPlayersReached(maxPlayers)));
            return;
        }

        participants.add(player);
        player.sendMessage(MessageBuilder.success("ボートレースに参加しました!"));
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);

        broadcastParticipantJoined(player);
    }

    public void addSpectator(Player player) {
        if (participants.contains(player)) {
            player.sendMessage(MessageBuilder.warning("既に参加者として参加しています"));
            return;
        }

        if (spectators.contains(player)) {
            player.sendMessage(MessageBuilder.warning("既に観戦者として参加しています"));
            return;
        }

        spectators.add(player);
        player.sendMessage(MessageBuilder.success("観戦者としてボートレースに参加しました!"));
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);

        broadcastSpectatorJoined(player);
    }

    private void broadcastParticipantJoined(Player player) {
        Component broadcast = Component.text(player.getName()).color(NamedTextColor.AQUA)
            .append(Component.text(" が参加しました (").color(NamedTextColor.GRAY))
            .append(Component.text(participants.size()).color(NamedTextColor.YELLOW));

        if (mode == RecruitmentMode.FIRST_COME) {
            broadcast = broadcast.append(
                Component.text("/" + maxPlayers + "人)").color(NamedTextColor.GRAY));
        } else {
            broadcast = broadcast.append(Component.text("人)").color(NamedTextColor.GRAY));
        }

        Component finalBroadcast = broadcast;
        participants.forEach(p -> p.sendMessage(finalBroadcast));
    }

    private void broadcastSpectatorJoined(Player player) {
        Component finalBroadcast = Component.text(player.getName()).color(NamedTextColor.AQUA)
            .append(Component.text(" が観戦者として参加しました").color(NamedTextColor.GRAY));
        participants.forEach(p -> p.sendMessage(finalBroadcast));
        spectators.forEach(p -> p.sendMessage(finalBroadcast));
    }

    public void cancel() {
        active = false;
        broadcastToAll(MessageBuilder.warning(Message.RECRUITMENT_CANCELLED.text()));
    }

    public void stop() {
        active = false;
        broadcastToAll(
            MessageBuilder.warning(
                Messages.recruitmentStopped(participants.size())));
    }

    private void broadcastToAll(Component message) {
        Bukkit.getOnlinePlayers().forEach(p -> p.sendMessage(message));
    }

    public List<Player> getParticipants() {
        if (mode == RecruitmentMode.LOTTERY && lotteryDrawn) {
            return new ArrayList<>(selectedParticipants);
        }
        return new ArrayList<>(participants);
    }

    public List<Player> getSpectators() {
        return new ArrayList<>(spectators);
    }

    public void performLottery() {
        if (mode != RecruitmentMode.LOTTERY) {
            return;
        }

        if (lotteryDrawn) {
            return;
        }

        List<Player> allParticipants = new ArrayList<>(participants);
        Collections.shuffle(allParticipants);

        if (allParticipants.size() <= maxPlayers) {
            selectedParticipants = allParticipants;
            lotteryDrawn = true;
            broadcastNoLotteryNeeded();
            return;
        }

        List<Player> selected = allParticipants.subList(0, maxPlayers);
        List<Player> notSelected = allParticipants.subList(maxPlayers, allParticipants.size());

        selectedParticipants = new ArrayList<>(selected);
        lotteryDrawn = true;

        broadcastLotteryResults(selected, notSelected);
    }

    private void broadcastNoLotteryNeeded() {

        Component message = MessageBuilder.separator()
            .append(Component.newline())
            .append(
                Component.text("   参加者が定員以下のため、抽選不要です").color(NamedTextColor.GREEN)
                    .decorate(net.kyori.adventure.text.format.TextDecoration.BOLD))
            .append(Component.newline())
            .append(Component.text("   全員参加: " + selectedParticipants.size() + "人")
                .color(NamedTextColor.AQUA))
            .append(Component.newline())
            .append(MessageBuilder.separator());
        Bukkit.getOnlinePlayers().forEach(p -> p.sendMessage(message));

        for (Player p : selectedParticipants) {
            p.sendMessage(MessageBuilder.separator());
            p.sendMessage(Component.text("参加確定！").color(NamedTextColor.GREEN)
                .decorate(net.kyori.adventure.text.format.TextDecoration.BOLD));
            p.sendMessage(Component.text("参加者が定員以下のため、全員参加となります")
                .color(NamedTextColor.AQUA));
            p.sendMessage(MessageBuilder.separator());
            p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        }
    }

    private void broadcastLotteryResults(List<Player> selected, List<Player> notSelected) {
        Component message = Component.newline()
            .append(Component.text("   当選者: ").color(NamedTextColor.GREEN));

        for (int i = 0; i < selected.size(); i++) {
            if (i > 0) {
                message = message.append(Component.text(", ").color(NamedTextColor.GRAY));
            }
            message = message.append(
                Component.text(selected.get(i).getName()).color(NamedTextColor.AQUA));
        }

        message = message.append(Component.newline())
            .append(MessageBuilder.separator());

        for (Player p : selected) {
            p.sendMessage(MessageBuilder.separator()
                .append(Component.newline())
                .append(Component.text("   抽選結果 - ").color(NamedTextColor.GOLD))
                .append(Component.text("当選").color(NamedTextColor.YELLOW)));
            p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        }

        for (Player p : notSelected) {
            p.sendMessage(MessageBuilder.separator()
                .append(Component.newline())
                .append(Component.text("   抽選結果 - ").color(NamedTextColor.GOLD))
                .append(Component.text("落選").color(NamedTextColor.GRAY)));
            p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
        }

        Component finalMessage = message;
        Bukkit.getOnlinePlayers().forEach(p -> p.sendMessage(finalMessage));
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

    public RecruitmentMode getMode() {
        return mode;
    }

    public boolean isLotteryDrawn() {
        return lotteryDrawn;
    }
}
