package com.me1q.summerFestival.game.tag.item;

import com.me1q.summerFestival.SummerFestival;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class TagItemUtil {

    private static final NamespacedKey TAG_ITEM_KEY = new NamespacedKey(
        SummerFestival.getInstance(), "tag_game_item");

    public static void markAsTagItem(ItemStack item) {
        if (item == null) {
            return;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            PersistentDataContainer container = meta.getPersistentDataContainer();
            container.set(TAG_ITEM_KEY, PersistentDataType.BYTE, (byte) 1);
            item.setItemMeta(meta);
        }
    }

    public static boolean isTagItem(ItemStack item) {
        if (item == null) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return false;
        }

        PersistentDataContainer container = meta.getPersistentDataContainer();
        return container.has(TAG_ITEM_KEY, PersistentDataType.BYTE);
    }
}

