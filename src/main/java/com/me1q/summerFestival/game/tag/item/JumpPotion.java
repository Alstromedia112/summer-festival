package com.me1q.summerFestival.game.tag.item;

import java.util.stream.Stream;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class JumpPotion {

    public static ItemStack createItem() {
        ItemStack potion = new ItemStack(Material.SPLASH_POTION);
        PotionMeta meta = (PotionMeta) potion.getItemMeta();

        if (meta != null) {
            meta.displayName(
                Component.text("ジャンプポーション").decoration(TextDecoration.ITALIC, false));
            meta.lore(Stream.of(
                Component.text("右クリックで使用").color(NamedTextColor.GREEN),
                Component.text("10秒間ジャンプ力が上昇する").color(NamedTextColor.GRAY)
            ).map(c -> c.decoration(TextDecoration.ITALIC, false)).toList());
            meta.clearCustomEffects();
            meta.addCustomEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, 20 * 10, 3), true);
            meta.setColor(Color.GREEN);
            potion.setItemMeta(meta);
        }

        TagItemUtil.markAsTagItem(potion);
        return potion;
    }
}
