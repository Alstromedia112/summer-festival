package com.me1q.summerFestival.game.tag.item.listener;

import com.me1q.summerFestival.SummerFestival;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class DecoyListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR
            && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        ItemStack item = event.getItem();
        if (item == null || item.getType() != Material.PLAYER_HEAD) {
            return;
        }

        Player player = event.getPlayer();
        event.setCancelled(true);

        spawnDecoy(player);
    }

    private void spawnDecoy(Player player) {
        Location location = player.getLocation();
        World world = player.getWorld();

        EntityType entityType = EntityType.ARMOR_STAND;
        ArmorStand decoy = (ArmorStand) world.spawnEntity(location, entityType);

        decoy.customName(Component.text(player.getName()));
        decoy.setCustomNameVisible(true);
        decoy.setSilent(true);
        decoy.setInvulnerable(true);

        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) head.getItemMeta();
        if (skullMeta != null) {
            skullMeta.setOwningPlayer(player);
            head.setItemMeta(skullMeta);
        }
        decoy.getEquipment().setHelmet(head);

        Bukkit.getScheduler().runTaskLater(SummerFestival.getInstance(), () -> {
            if (!decoy.isDead()) {
                decoy.remove();
            }
        }, 30 * 20);
    }
}
