package com.me1q.summerFestival.game.boatrace.boatstand;

import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class BoatStandMarkerItem {

    public static ItemStack create() {
        ItemStack item = new ItemStack(Material.ARMOR_STAND);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.displayName(Component.text("ボートスタンドマーカー")
                .color(NamedTextColor.AQUA)
                .decoration(TextDecoration.ITALIC, false));

            meta.lore(List.of(
                Component.text("右クリックでボートの出現地点を設定")
                    .color(NamedTextColor.GRAY)
                    .decoration(TextDecoration.ITALIC, false),
                Component.text("レース開始時にここにボートが出現します")
                    .color(NamedTextColor.GRAY)
                    .decoration(TextDecoration.ITALIC, false)
            ));

            item.setItemMeta(meta);
        }

        return item;
    }

    public static boolean isMarkerItem(ItemStack item) {
        if (item == null || item.getType() != Material.ARMOR_STAND) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) {
            return false;
        }

        Component displayName = meta.displayName();
        if (displayName == null) {
            return false;
        }

        Component expectedName = Component.text("ボートスタンドマーカー")
            .color(NamedTextColor.AQUA)
            .decoration(TextDecoration.ITALIC, false);

        return displayName.equals(expectedName);
    }
}

