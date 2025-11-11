package com.me1q.summerFestival.game.boatrace.returnpoint.listener;

import com.me1q.summerFestival.core.message.MessageBuilder;
import com.me1q.summerFestival.game.boatrace.returnpoint.ReturnPointManager;
import com.me1q.summerFestival.game.boatrace.returnpoint.ReturnPointMarkerItem;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class ReturnPointMarkerListener implements Listener {

    private final ReturnPointManager returnPointManager;

    public ReturnPointMarkerListener(ReturnPointManager returnPointManager) {
        this.returnPointManager = returnPointManager;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        var player = event.getPlayer();
        ItemStack item = event.getItem();

        if (!ReturnPointMarkerItem.isReturnPointMarker(item)) {
            return;
        }

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK || event.getClickedBlock() == null) {
            return;
        }

        event.setCancelled(true);

        Location location = event.getClickedBlock().getLocation().add(0.5, 1, 0.5);
        returnPointManager.setReturnPoint(player, location);

        player.sendMessage(MessageBuilder.success("リターンポイントを設置しました"));
        player.sendMessage(MessageBuilder.info("座標: " +
            String.format("X: %.1f, Y: %.1f, Z: %.1f",
                location.getX(), location.getY(), location.getZ())));
    }
}

