package com.me1q.summerFestival.game.tag.item;

import com.me1q.summerFestival.game.tag.item.listener.SmokeLauncherListener;
import java.util.stream.Stream;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SmokeLauncher extends TagItemBase {

    public ItemStack createItem() {
        ItemStack windCharge = new ItemStack(Material.WIND_CHARGE);
        ItemMeta meta = windCharge.getItemMeta();

        if (meta != null) {
            meta.displayName(
                Component.text("スモークランチャー").decoration(TextDecoration.ITALIC, false));
            meta.lore(Stream.of(
                Component.text("右クリックで使用").color(NamedTextColor.GREEN),
                Component.text("周囲に煙幕を発生させ、視界を遮る").color(NamedTextColor.GRAY)
            ).map(c -> c.decoration(TextDecoration.ITALIC, false)).toList());
            windCharge.setItemMeta(meta);
        }

        return windCharge;
    }

    @Override
    public Listener getItemManager() {
        return new SmokeLauncherListener();
    }
}
