package com.me1q.summerFestival.game.boatrace.item.listener;

import org.bukkit.Material;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class SpeedBoostListener implements Listener {

    @EventHandler
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
        ItemStack offhandItem = event.getOffHandItem();
        if (offhandItem.getType() != Material.LIGHT_BLUE_WOOL) {
            return;
        }

        Player player = event.getPlayer();
        if (!(player.getVehicle() instanceof Boat boat)) {
            return;
        }

        Vector direction = boat.getLocation().getDirection();
        direction.setY(0);
        Vector newVelocity = direction.clone().multiply(1.5);

        boat.setVelocity(new Vector(0, 0, 0));
        boat.setVelocity(newVelocity);

        offhandItem.setAmount(offhandItem.getAmount() - 1);
    }
}
