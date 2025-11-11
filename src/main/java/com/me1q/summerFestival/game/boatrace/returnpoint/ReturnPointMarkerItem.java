package com.me1q.summerFestival.game.boatrace.returnpoint;

import java.util.List;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class ReturnPointMarkerItem {

    private static final String ITEM_NAME = "§e§lリターンポイントマーカー";
    private static final String ITEM_LORE = "§7右クリックでリターンポイントを設置";
    private static final String PDC_KEY = "boatrace_return_point_marker";

    public static ItemStack create() {
        ItemStack item = new ItemStack(Material.RECOVERY_COMPASS);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.displayName(Component.text(ITEM_NAME));
            meta.lore(List.of(Component.text(ITEM_LORE)));
            meta.getPersistentDataContainer().set(
                new org.bukkit.NamespacedKey("summerfestival", PDC_KEY),
                PersistentDataType.BYTE,
                (byte) 1
            );
            item.setItemMeta(meta);
        }

        return item;
    }

    public static boolean isReturnPointMarker(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();
        return meta.getPersistentDataContainer().has(
            new org.bukkit.NamespacedKey("summerfestival", PDC_KEY),
            PersistentDataType.BYTE
        );
    }
}

