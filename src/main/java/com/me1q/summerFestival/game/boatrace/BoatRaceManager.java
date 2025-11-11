package com.me1q.summerFestival.game.boatrace;

import com.me1q.summerFestival.SummerFestival;
import com.me1q.summerFestival.core.message.MessageBuilder;
import com.me1q.summerFestival.currency.CurrencyManager;
import com.me1q.summerFestival.game.boatrace.boatstand.BoatStandManager;
import com.me1q.summerFestival.game.boatrace.boatstand.BoatStandMarkerItem;
import com.me1q.summerFestival.game.boatrace.boatstand.listener.BoatStandListener;
import com.me1q.summerFestival.game.boatrace.constants.Message;
import com.me1q.summerFestival.game.boatrace.constants.Messages;
import com.me1q.summerFestival.game.boatrace.constants.RecruitmentMode;
import com.me1q.summerFestival.game.boatrace.goal.GoalLineManager;
import com.me1q.summerFestival.game.boatrace.goal.GoalLineManager.GoalLine;
import com.me1q.summerFestival.game.boatrace.goal.GoalMarkerItem;
import com.me1q.summerFestival.game.boatrace.itemstand.ItemStandManager;
import com.me1q.summerFestival.game.boatrace.itemstand.ItemStandMarkerItem;
import com.me1q.summerFestival.game.boatrace.itemstand.listener.ItemStandListener;
import com.me1q.summerFestival.game.boatrace.listener.GoalMarkerListener;
import com.me1q.summerFestival.game.boatrace.session.BoatRaceRecruitSession;
import com.me1q.summerFestival.game.boatrace.session.BoatRaceSession;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class BoatRaceManager {

    private final SummerFestival plugin;
    private final CurrencyManager currencyManager;
    private final GoalLineManager goalLineManager;
    private final ItemStandManager itemStandManager;
    private final BoatStandManager boatStandManager;

    private BoatRaceRecruitSession activeRecruitSession;
    private BoatRaceSession activeRaceSession;

    public BoatRaceManager(SummerFestival plugin, CurrencyManager currencyManager) {
        this.plugin = plugin;
        this.currencyManager = currencyManager;
        this.goalLineManager = new GoalLineManager(plugin);
        this.itemStandManager = new ItemStandManager();
        this.boatStandManager = new BoatStandManager();
        this.activeRecruitSession = null;
        this.activeRaceSession = null;

        registerListeners();
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(
            new GoalMarkerListener(goalLineManager), plugin);
        Bukkit.getPluginManager().registerEvents(
            new ItemStandListener(itemStandManager), plugin);
        Bukkit.getPluginManager().registerEvents(
            new BoatStandListener(boatStandManager), plugin);
    }

    public void startRecruit(Player organizer, int maxPlayers, boolean organizerParticipates,
        RecruitmentMode mode) {
        if (isRecruitmentActive()) {
            organizer.sendMessage(
                MessageBuilder.error(Message.ALREADY_RECRUITING.text()));
            return;
        }

        if (isRaceActive()) {
            organizer.sendMessage(
                MessageBuilder.error(Message.RACE_IN_PROGRESS.text()));
            return;
        }

        if (!RecruitmentValidator.validateGoalLine(organizer, goalLineManager)) {
            return;
        }

        if (!RecruitmentValidator.validateMaxPlayers(organizer, maxPlayers)) {
            return;
        }

        organizer.sendMessage(MessageBuilder.success(
            Messages.recruitmentStarted(maxPlayers, organizerParticipates)));

        activeRecruitSession = new BoatRaceRecruitSession(organizer, maxPlayers,
            organizerParticipates, mode);
        activeRecruitSession.start();
    }

    public void joinRecruit(Player player) {
        if (!isRecruitmentActive()) {
            player.sendMessage(
                MessageBuilder.error(Message.NO_RECRUITMENT.text()));
            return;
        }

        activeRecruitSession.addPlayer(player);
    }

    public void joinRecruitAsSpectator(Player player) {
        if (!isRecruitmentActive()) {
            player.sendMessage(
                MessageBuilder.error(Message.NO_RECRUITMENT.text()));
            return;
        }

        activeRecruitSession.addSpectator(player);
    }

    public void cancelRecruit(Player player) {
        if (!isRecruitmentActive()) {
            player.sendMessage(
                MessageBuilder.error(Message.NOT_RECRUITING.text()));
            return;
        }

        if (!isOrganizer(player, activeRecruitSession.getOrganizer())) {
            player.sendMessage(MessageBuilder.error(
                Message.ONLY_ORGANIZER_CAN_CANCEL.text()));
            return;
        }

        activeRecruitSession.cancel();
        activeRecruitSession = null;
    }

    public void drawLottery(Player player) {
        if (!isRecruitmentActive()) {
            player.sendMessage(
                MessageBuilder.error(Message.NO_RECRUITMENT.text()));
            return;
        }

        if (!isOrganizer(player, activeRecruitSession.getOrganizer())) {
            player.sendMessage(MessageBuilder.error(
                Message.ONLY_ORGANIZER_CAN_CANCEL.text()));
            return;
        }

        if (activeRecruitSession.getMode() != RecruitmentMode.LOTTERY) {
            player.sendMessage(
                MessageBuilder.error("抽選モードではありません"));
            return;
        }

        if (activeRecruitSession.isLotteryDrawn()) {
            player.sendMessage(
                MessageBuilder.warning("すでに抽選が実施されています"));
            return;
        }

        activeRecruitSession.performLottery();
    }

    public void startRace(Player organizer) {
        if (!isRecruitmentActive()) {
            organizer.sendMessage(
                MessageBuilder.error(Message.START_RECRUITMENT_FIRST.text()));
            return;
        }

        if (!isOrganizer(organizer, activeRecruitSession.getOrganizer())) {
            organizer.sendMessage(MessageBuilder.error(
                Message.ONLY_ORGANIZER_CAN_START.text()));
            return;
        }

        if (!RecruitmentValidator.validateMinParticipants(organizer,
            activeRecruitSession.getParticipantCount())) {
            return;
        }

        if (activeRecruitSession.getMode() == RecruitmentMode.LOTTERY
            && !activeRecruitSession.isLotteryDrawn()) {
            organizer.sendMessage(MessageBuilder.error(
                "抽選を実施してください (/boatrace draw)"));
            return;
        }

        GoalLine goalLine = goalLineManager.getGoalLine(organizer);
        if (!isGoalLineValid(organizer, goalLine)) {
            return;
        }

        activeRecruitSession.stop();

        activeRaceSession = new BoatRaceSession(
            plugin,
            currencyManager,
            activeRecruitSession.getParticipants(),
            activeRecruitSession.getSpectators(),
            organizer,
            goalLine,
            boatStandManager.getBoatStandLocations(organizer),
            this::cleanupRaceSession
        );

        activeRecruitSession = null;
        activeRaceSession.start();
    }

    public void stopRace(Player player) {
        if (!isRaceActive()) {
            player.sendMessage(
                MessageBuilder.error(Message.NO_RACE_IN_PROGRESS.text()));
            return;
        }

        if (!canStopRace(player)) {
            player.sendMessage(
                MessageBuilder.error(Message.NOT_PARTICIPANT.text()));
            return;
        }

        activeRaceSession.stop();
    }

    private void cleanupRaceSession() {
        activeRaceSession = null;
    }

    public void giveGoalMarkerEgg(Player player) {
        player.getInventory().addItem(GoalMarkerItem.create());
        player.sendMessage(
            MessageBuilder.success(Message.GOAL_MARKER_OBTAINED.text()));
    }

    public void clearGoalLines(Player player) {
        goalLineManager.clearGoalLines(player);
    }

    public void giveItemStandMarker(Player player) {
        player.getInventory().addItem(ItemStandMarkerItem.create());
        player.sendMessage(
            MessageBuilder.success("アイテムスタンドマーカーを取得しました"));
    }

    public void giveBoatStandMarker(Player player) {
        player.getInventory().addItem(BoatStandMarkerItem.create());
        player.sendMessage(
            MessageBuilder.success("ボートスタンドマーカーを取得しました"));
    }

    public void clearBoatStands(Player player) {
        boatStandManager.clearBoatStands(player);
        player.sendMessage(MessageBuilder.success("すべてのボートスタンドを削除しました"));
    }

    public boolean hasGoalLine(Player player) {
        return goalLineManager.hasGoalLine(player);
    }

    private boolean isRecruitmentActive() {
        return activeRecruitSession != null && activeRecruitSession.isActive();
    }

    private boolean isRaceActive() {
        return activeRaceSession != null && activeRaceSession.isActive();
    }

    private boolean isOrganizer(Player player, Player organizer) {
        return organizer.equals(player);
    }

    private boolean isGoalLineValid(Player player, GoalLine goalLine) {
        if (goalLine == null || !goalLine.isValid()) {
            player.sendMessage(
                MessageBuilder.error(Message.GOAL_NOT_SET.text()));
            return false;
        }
        return true;
    }

    private boolean canStopRace(Player player) {
        return activeRaceSession.isParticipant(player) ||
            activeRaceSession.isOrganizer(player);
    }

    public SummerFestival getPlugin() {
        return plugin;
    }
}
