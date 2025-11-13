package com.me1q.summerFestival.teleportbook.listener;

import com.me1q.summerFestival.teleportbook.TeleportBookGUI;
import com.me1q.summerFestival.teleportbook.TeleportBookItem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class TeleportBookListener implements Listener {

    private final TeleportBookGUI gui;

    public TeleportBookListener(TeleportBookGUI gui) {
        this.gui = gui;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item == null) {
            return;
        }

        if (!TeleportBookItem.isTeleportBook(item)) {
            return;
        }

        event.setCancelled(true);
        gui.openGUI(player);
    }
}

