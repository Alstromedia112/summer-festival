package com.me1q.summerFestival.game.boatrace.boatstand.listener;

import com.me1q.summerFestival.core.message.MessageBuilder;
import com.me1q.summerFestival.game.boatrace.boatstand.BoatStandManager;
import com.me1q.summerFestival.game.boatrace.boatstand.BoatStandMarkerItem;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class BoatStandListener implements Listener {

    private final BoatStandManager boatStandManager;

    public BoatStandListener(BoatStandManager boatStandManager) {
        this.boatStandManager = boatStandManager;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        ItemStack item = event.getItem();
        if (!BoatStandMarkerItem.isMarkerItem(item)) {
            return;
        }

        event.setCancelled(true);

        if (event.getClickedBlock() == null) {
            return;
        }

        Player player = event.getPlayer();
        Location clickedBlock = event.getClickedBlock().getLocation();
        Location spawnLocation = clickedBlock.add(0.5, 1, 0.5);

        ArmorStand armorStand = (ArmorStand) spawnLocation.getWorld()
            .spawnEntity(spawnLocation, EntityType.ARMOR_STAND);
        armorStand.setGravity(false);
        armorStand.setVisible(false);
        armorStand.customName(MessageBuilder.info("ボート出現地点"));

        boatStandManager.addBoatStand(player, armorStand);
    }
}

