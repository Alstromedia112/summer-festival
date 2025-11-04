package com.me1q.summerFestival.game.boatrace.itemstand.listener;

import com.me1q.summerFestival.core.message.MessageBuilder;
import com.me1q.summerFestival.game.boatrace.itemstand.ItemStandManager;
import com.me1q.summerFestival.game.boatrace.itemstand.ItemStandMarkerItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class ItemStandListener implements Listener {

    private final ItemStandManager itemStandManager;
    private static final double PICKUP_DISTANCE = 1.5;

    public ItemStandListener(ItemStandManager itemStandManager) {
        this.itemStandManager = itemStandManager;
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
        if (!ItemStandMarkerItem.isItemStandMarker(item)) {
            return;
        }

        event.setCancelled(true);

        if (event.getClickedBlock() == null) {
            return;
        }

        Player player = event.getPlayer();
        Location clickedBlock = event.getClickedBlock().getLocation();
        Location spawnLocation = clickedBlock.add(0.5, 1, 0.5);

        itemStandManager.spawnItemStand(spawnLocation);

        player.sendMessage(MessageBuilder.success("アイテムスタンドを設置しました"));
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location playerLocation = player.getLocation();

        if (itemStandManager.isOnCooldown(player.getUniqueId())) {
            return;
        }

        for (Entity entity : player.getWorld()
            .getNearbyEntities(playerLocation, PICKUP_DISTANCE, PICKUP_DISTANCE, PICKUP_DISTANCE)) {
            if (!(entity instanceof ArmorStand stand)) {
                continue;
            }

            if (!itemStandManager.isItemStand(stand.getUniqueId())) {
                continue;
            }

            ItemStack helmet = stand.getEquipment().getHelmet();

            if (helmet == null || helmet.getType() == Material.AIR) {
                continue;
            }

            player.getInventory().addItem(helmet.clone());
            player.sendMessage(MessageBuilder.success("アイテムを取得しました"));

            itemStandManager.setCooldown(player.getUniqueId());

            break;
        }
    }
}
