package com.me1q.summerFestival.teleportbook;

import java.util.ArrayList;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class TeleportBookGUI {

    private static final int GUI_SIZE = 54;
    private static final String GUI_TITLE = "テレポート地点選択";

    private final TeleportLocationManager locationManager;

    public TeleportBookGUI(TeleportLocationManager locationManager) {
        this.locationManager = locationManager;
    }

    public void openGUI(Player player) {
        Inventory inventory = Bukkit.createInventory(null, GUI_SIZE,
            Component.text(GUI_TITLE).color(NamedTextColor.DARK_PURPLE));

        List<TeleportLocation> locations = locationManager.getAllLocations();

        int slot = 0;
        for (TeleportLocation location : locations) {
            if (slot >= GUI_SIZE - 9) {
                break;
            }
            inventory.setItem(slot, createLocationItem(location));
            slot++;
        }

        addCloseButton(inventory);

        player.openInventory(inventory);
    }

    private ItemStack createLocationItem(TeleportLocation location) {
        ItemStack item = new ItemStack(Material.ENDER_PEARL);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.displayName(Component.text(location.getName())
                .color(NamedTextColor.GREEN)
                .decoration(TextDecoration.ITALIC, false));

            Location loc = location.getLocation();
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("ワールド: " + loc.getWorld().getName())
                .color(NamedTextColor.GRAY)
                .decoration(TextDecoration.ITALIC, false));
            lore.add(Component.text(String.format("座標: %.1f, %.1f, %.1f",
                    loc.getX(), loc.getY(), loc.getZ()))
                .color(NamedTextColor.GRAY)
                .decoration(TextDecoration.ITALIC, false));
            lore.add(Component.empty());
            lore.add(Component.text("クリックでテレポート")
                .color(NamedTextColor.YELLOW)
                .decoration(TextDecoration.ITALIC, false));

            meta.lore(lore);
            item.setItemMeta(meta);
        }

        return item;
    }

    private void addCloseButton(Inventory inventory) {
        ItemStack closeItem = new ItemStack(Material.BARRIER);
        ItemMeta meta = closeItem.getItemMeta();

        if (meta != null) {
            meta.displayName(Component.text("閉じる")
                .color(NamedTextColor.RED)
                .decoration(TextDecoration.ITALIC, false));
            closeItem.setItemMeta(meta);
        }

        inventory.setItem(GUI_SIZE - 1, closeItem);
    }

    public static boolean isTeleportGUI(Inventory inventory) {
        if (inventory.getViewers().isEmpty()) {
            return false;
        }

        Component title = inventory.getViewers().getFirst().getOpenInventory().title();
        if (title == null) {
            return false;
        }

        Component expectedTitle = Component.text(GUI_TITLE).color(NamedTextColor.DARK_PURPLE);
        return title.equals(expectedTitle);
    }

    public String getLocationNameFromSlot(int slot) {
        List<TeleportLocation> locations = locationManager.getAllLocations();
        if (slot >= 0 && slot < locations.size()) {
            return locations.get(slot).getName();
        }
        return null;
    }
}

