package com.me1q.summerFestival.game.tag.itemstand;

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
                Component.text("鬼ごっこアイテムスタンド")
                    .decoration(TextDecoration.ITALIC, false)
                    .color(NamedTextColor.GOLD));
            meta.lore(Stream.of(
                Component.text("右クリックで設置").color(NamedTextColor.GREEN),
                Component.text("鬼ごっこ中にアイテムを取得できる").color(NamedTextColor.GRAY),
                Component.text("鬼側は取得不可").color(NamedTextColor.RED),
                Component.text("再取得まで30秒").color(NamedTextColor.YELLOW)
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
        Component expectedName = Component.text("鬼ごっこアイテムスタンド")
            .decoration(TextDecoration.ITALIC, false)
            .color(NamedTextColor.GOLD);

        return displayName != null && displayName.equals(expectedName);
    }
}

