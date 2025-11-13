package com.me1q.summerFestival.game.tag.item;

import com.me1q.summerFestival.game.tag.item.listener.TaggerDetectorListener;
import java.util.stream.Stream;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class TaggerDetector extends TagItemBase {

    public static ItemStack createItem() {
        ItemStack detector = new ItemStack(Material.ENDER_EYE);
        ItemMeta meta = detector.getItemMeta();

        if (meta != null) {
            meta.displayName(
                Component.text("探知機").decoration(TextDecoration.ITALIC, false));
            meta.lore(Stream.of(
                Component.text("右クリックで使用").color(NamedTextColor.GREEN),
                Component.text("近くの鬼3体を10秒間発光させる").color(NamedTextColor.GRAY)
            ).map(c -> c.decoration(TextDecoration.ITALIC, false)).toList());
            detector.setItemMeta(meta);
        }

        TagItemUtil.markAsTagItem(detector);
        return detector;
    }

    @Override
    public Listener getItemManager() {
        return new TaggerDetectorListener();
    }
}

