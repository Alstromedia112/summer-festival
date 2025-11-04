package com.me1q.summerFestival.game.boatrace.listener;

import com.me1q.summerFestival.core.message.MessageBuilder;
import com.me1q.summerFestival.game.boatrace.constants.Config;
import com.me1q.summerFestival.game.boatrace.constants.Message;
import com.me1q.summerFestival.game.boatrace.goal.GoalLineManager;
import com.me1q.summerFestival.game.boatrace.goal.GoalMarkerItem;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class GoalMarkerListener implements Listener {

    private final GoalLineManager goalLineManager;

    public GoalMarkerListener(GoalLineManager goalLineManager) {
        this.goalLineManager = goalLineManager;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        var player = event.getPlayer();
        ItemStack item = event.getItem();

        if (!GoalMarkerItem.isGoalMarker(item)) {
            return;
        }

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK || event.getClickedBlock() == null) {
            return;
        }

        event.setCancelled(true);

        int currentMarkerCount = goalLineManager.getMarkerCount(player);

        if (currentMarkerCount >= Config.REQUIRED_MARKERS.value()) {
            player.sendMessage(MessageBuilder.warning(Message.MARKERS_FULL.text()));
            player.sendMessage(MessageBuilder.warning(Message.USE_CLEARGOAL.text()));
            return;
        }

        Location location = event.getClickedBlock().getLocation().add(0.5, 1, 0.5);
        ArmorStand marker = goalLineManager.createMarker(location, player);
        int markerCount = goalLineManager.addMarker(player, marker);

        goalLineManager.sendMarkerPlacedMessage(player, markerCount);
    }
}

