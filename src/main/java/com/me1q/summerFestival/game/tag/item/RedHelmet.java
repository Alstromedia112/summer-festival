package com.me1q.summerFestival.game.tag.item;

import com.me1q.summerFestival.game.tag.item.listener.RedHelmetListener;
import java.util.stream.Stream;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class RedHelmet extends TagItemBase {

    public static ItemStack createItem() {
        ItemStack redHelmet = new ItemStack(Material.LEATHER_HELMET);
        LeatherArmorMeta meta = (LeatherArmorMeta) redHelmet.getItemMeta();

        if (meta != null) {
            meta.displayName(
                Component.text("赤帽子").decoration(TextDecoration.ITALIC, false));
            meta.lore(Stream.of(
                Component.text("右クリックで使用").color(NamedTextColor.GREEN),
                Component.text("10秒間鬼と同じ赤帽子をかぶる").color(NamedTextColor.GRAY)
            ).map(c -> c.decoration(TextDecoration.ITALIC, false)).toList());
            meta.setColor(Color.RED);
            redHelmet.setItemMeta(meta);
        }

        TagItemUtil.markAsTagItem(redHelmet);
        return redHelmet;
    }

    @Override
    public Listener getItemManager() {
        return new RedHelmetListener();
    }
}

