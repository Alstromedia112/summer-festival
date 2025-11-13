package com.me1q.summerFestival.teleportbook.listener;

import com.me1q.summerFestival.teleportbook.TeleportBookItem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (hasTeleportBook(player)) {
            return;
        }

        player.getInventory().addItem(TeleportBookItem.createBook());
    }

    private boolean hasTeleportBook(Player player) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (TeleportBookItem.isTeleportBook(item)) {
                return true;
            }
        }
        return false;
    }
}

