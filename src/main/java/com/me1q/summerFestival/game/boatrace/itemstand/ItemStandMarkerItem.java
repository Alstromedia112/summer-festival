package com.me1q.summerFestival.game.boatrace.itemstand;

import java.util.stream.Stream;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class ItemStandMarkerItem {

    private ItemStandMarkerItem() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static ItemStack create() {
        ItemStack item = new ItemStack(Material.ARMOR_STAND);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.displayName(
                Component.text("アイテムスタンド").decoration(TextDecoration.ITALIC, false)
                    .color(NamedTextColor.GOLD));
            meta.lore(Stream.of(
                Component.text("右クリックで設置").color(NamedTextColor.GREEN),
                Component.text("ボートレース中にアイテムを取得できる").color(NamedTextColor.GRAY)
            ).map(c -> c.decoration(TextDecoration.ITALIC, false)).toList());
            item.setItemMeta(meta);
        }

        return item;
    }

    public static boolean isItemStandMarker(ItemStack item) {
        if (item == null || item.getType() != Material.ARMOR_STAND) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) {
            return false;
        }

        Component displayName = meta.displayName();
        Component expectedName = Component.text("アイテムスタンド")
            .decoration(TextDecoration.ITALIC, false)
            .color(NamedTextColor.GOLD);

        return displayName != null && displayName.equals(expectedName);
    }
}

