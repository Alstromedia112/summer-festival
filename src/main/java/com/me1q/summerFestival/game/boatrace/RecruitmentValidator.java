package com.me1q.summerFestival.game.boatrace;

import com.me1q.summerFestival.core.message.MessageBuilder;
import com.me1q.summerFestival.game.boatrace.constants.Config;
import com.me1q.summerFestival.game.boatrace.constants.Message;
import com.me1q.summerFestival.game.boatrace.goal.GoalLineManager;
import org.bukkit.entity.Player;

public final class RecruitmentValidator {

    private RecruitmentValidator() {
    }

    public static boolean validateMaxPlayers(Player player, int maxPlayers) {
        if (maxPlayers < Config.MIN_PLAYERS.value()) {
            player.sendMessage(MessageBuilder.error(Message.MIN_PLAYERS_ERROR.text()));
            return false;
        }

        if (maxPlayers > Config.MAX_PLAYERS.value()) {
            player.sendMessage(MessageBuilder.error(Message.MAX_PLAYERS_ERROR.text()));
            return false;
        }

        return true;
    }

    public static boolean validateGoalLine(Player player, GoalLineManager goalLineManager) {
        if (!goalLineManager.hasGoalLine(player)) {
            player.sendMessage(MessageBuilder.error(Message.SET_GOAL_FIRST.text()));
            return false;
        }
        return true;
    }

    public static boolean validateMinParticipants(Player player, int participantCount) {
        if (participantCount < Config.MIN_PLAYERS.value()) {
            player.sendMessage(
                MessageBuilder.error(Message.MIN_PARTICIPANTS_REQUIRED.text()));
            return false;
        }
        return true;
    }
}

