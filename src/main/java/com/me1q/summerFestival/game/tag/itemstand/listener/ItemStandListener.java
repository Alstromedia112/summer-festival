package com.me1q.summerFestival.game.tag.itemstand.listener;

import com.me1q.summerFestival.SummerFestival;
import com.me1q.summerFestival.core.message.MessageBuilder;
import com.me1q.summerFestival.game.tag.TagManager;
import com.me1q.summerFestival.game.tag.itemstand.ItemStandManager;
import com.me1q.summerFestival.game.tag.itemstand.ItemStandMarkerItem;
import com.me1q.summerFestival.game.tag.session.TagSession;
import org.bukkit.Location;
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
    private final TagManager tagManager;
    private final SummerFestival plugin;
    private static final double PICKUP_DISTANCE = 1.5;

    public ItemStandListener(ItemStandManager itemStandManager, TagManager tagManager,
        SummerFestival plugin) {
        this.itemStandManager = itemStandManager;
        this.tagManager = tagManager;
        this.plugin = plugin;
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

        Location clickedBlock = event.getClickedBlock().getLocation();
        Location spawnLocation = clickedBlock.add(0.5, -0.3, 0.5);

        itemStandManager.spawnItemStand(spawnLocation);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        TagSession tagSession = tagManager.getActiveSession();

        if (tagSession == null || !tagSession.isActive()) {
            return;
        }

        Player player = event.getPlayer();
        Location playerLocation = player.getLocation();

        if (tagSession.isTagger(player)) {
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

            if (itemStandManager.isStandOnCooldown(stand.getUniqueId())) {
                continue;
            }

            ItemStack standItem = itemStandManager.getItemFromStand(stand.getUniqueId());
            if (standItem != null) {
                player.getInventory().addItem(standItem);
                player.sendMessage(MessageBuilder.success("アイテムを取得しました"));

                itemStandManager.setStandCooldown(stand.getUniqueId(), plugin);
            }

            break;
        }
    }
}

