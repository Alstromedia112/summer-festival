package com.me1q.summerFestival.game.tag.item;

import com.me1q.summerFestival.game.tag.item.listener.DecoyListener;
import java.util.stream.Stream;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Decoy extends TagItemBase {

    public static ItemStack createItem() {
        ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD);
        ItemMeta meta = playerHead.getItemMeta();

        if (meta != null) {
            meta.displayName(Component.text("デコイ").decoration(TextDecoration.ITALIC, false));
            meta.lore(Stream.of(
                Component.text("右クリックで使用").color(NamedTextColor.GREEN),
                Component.text("一定時間その場に自分の分身を設置する").color(NamedTextColor.GRAY),
                Component.text("分身は設置してから30秒後に消える").color(NamedTextColor.GRAY)
            ).map(c -> c.decoration(TextDecoration.ITALIC, false)).toList());
            playerHead.setItemMeta(meta);
        }

        return playerHead;

    }

    @Override
    public Listener getItemManager() {
        return new DecoyListener();
    }
}
