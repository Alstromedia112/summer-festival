package com.me1q.summerFestival.teleportbook.listener;

import com.me1q.summerFestival.teleportbook.TeleportBookGUI;
import com.me1q.summerFestival.teleportbook.TeleportLocation;
import com.me1q.summerFestival.teleportbook.TeleportLocationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class TeleportGUIListener implements Listener {

    private final TeleportLocationManager locationManager;
    private final TeleportBookGUI gui;

    public TeleportGUIListener(TeleportLocationManager locationManager, TeleportBookGUI gui) {
        this.locationManager = locationManager;
        this.gui = gui;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getClickedInventory();
        if (inventory == null) {
            return;
        }

        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        if (!TeleportBookGUI.isTeleportGUI(event.getView().getTopInventory())) {
            return;
        }

        event.setCancelled(true);

        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) {
            return;
        }

        if (clickedItem.getType() == Material.BARRIER) {
            player.closeInventory();
            return;
        }

        if (clickedItem.getType() == Material.ENDER_PEARL) {
            String locationName = gui.getLocationNameFromSlot(event.getSlot());
            if (locationName != null) {
                TeleportLocation teleportLocation = locationManager.getLocation(locationName);
                if (teleportLocation != null) {
                    Location loc = teleportLocation.getLocation();
                    player.teleport(loc);
                    player.closeInventory();
                    player.sendMessage(Component.text("「" + locationName + "」にテレポートしました")
                        .color(NamedTextColor.GREEN));
                } else {
                    player.sendMessage(Component.text("地点が見つかりませんでした")
                        .color(NamedTextColor.RED));
                }
            }
        }
    }
}

