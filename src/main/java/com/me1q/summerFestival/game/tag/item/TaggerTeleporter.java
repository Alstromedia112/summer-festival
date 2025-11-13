package com.me1q.summerFestival.game.tag.item;

import com.me1q.summerFestival.game.tag.item.listener.TaggerTeleporterListener;
import java.util.stream.Stream;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class TaggerTeleporter extends TagItemBase {

    public static ItemStack createItem() {
        ItemStack teleporter = new ItemStack(Material.COCOA_BEANS);
        ItemMeta meta = teleporter.getItemMeta();

        if (meta != null) {
            meta.displayName(
                Component.text("鬼テレポーター").decoration(TextDecoration.ITALIC, false));
            meta.lore(Stream.of(
                Component.text("右クリックで使用").color(NamedTextColor.GREEN),
                Component.text("7m以内にいる鬼1体をリターンポイントにテレポートさせる")
                    .color(NamedTextColor.GRAY)
            ).map(c -> c.decoration(TextDecoration.ITALIC, false)).toList());
            teleporter.setItemMeta(meta);
        }

        TagItemUtil.markAsTagItem(teleporter);
        return teleporter;
    }

    @Override
    public Listener getItemManager() {
        return new TaggerTeleporterListener();
    }
}

