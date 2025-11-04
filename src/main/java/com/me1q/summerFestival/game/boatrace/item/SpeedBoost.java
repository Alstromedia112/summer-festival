package com.me1q.summerFestival.game.boatrace.item;

import com.me1q.summerFestival.game.boatrace.item.listener.SpeedBoostListener;
import java.util.stream.Stream;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SpeedBoost extends BoatRaceItemBase {

    public static ItemStack createItem() {
        ItemStack item = new ItemStack(Material.LIGHT_BLUE_WOOL);
        ItemMeta meta = (ItemMeta) item.getItemMeta();

        if (meta != null) {
            meta.displayName(
                Component.text("スピードブースト").decoration(TextDecoration.ITALIC, false));
            meta.lore(Stream.of(
                Component.text("右クリックで使用").color(NamedTextColor.GREEN),
                Component.text("ボートの移動速度が一時的に上昇する").color(NamedTextColor.GRAY)
            ).map(c -> c.decoration(TextDecoration.ITALIC, false)).toList());
            item.setItemMeta(meta);
        }

        return item;
    }

    @Override
    public Listener getItemManager() {
        return new SpeedBoostListener();
    }
}
