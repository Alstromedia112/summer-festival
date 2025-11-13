package com.me1q.summerFestival.teleportbook;

import java.util.ArrayList;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class TeleportBookItem {

    public static ItemStack createBook() {
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        ItemMeta meta = book.getItemMeta();

        if (meta != null) {
            meta.displayName(Component.text("テレポートの本")
                .color(NamedTextColor.AQUA)
                .decoration(TextDecoration.ITALIC, false));

            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("右クリックで地点一覧を開く")
                .color(NamedTextColor.GRAY)
                .decoration(TextDecoration.ITALIC, false));
            meta.lore(lore);

            book.setItemMeta(meta);
        }

        return book;
    }

    public static boolean isTeleportBook(ItemStack item) {
        if (item == null || item.getType() != Material.WRITTEN_BOOK) {
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

        Component expectedName = Component.text("テレポートの本")
            .color(NamedTextColor.AQUA)
            .decoration(TextDecoration.ITALIC, false);

        return displayName.equals(expectedName);
    }
}

