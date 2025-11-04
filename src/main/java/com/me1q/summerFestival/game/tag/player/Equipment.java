package com.me1q.summerFestival.game.tag.player;

import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public final class Equipment {

    private Equipment() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static void equipPlayer(Player player, PlayerRole role) {
        player.getInventory().setHelmet(createHelmet(role));
    }

    private static ItemStack createHelmet(PlayerRole role) {
        ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
        LeatherArmorMeta meta = (LeatherArmorMeta) helmet.getItemMeta();

        if (role == PlayerRole.TAGGER) {
            meta.setColor(Color.RED);
        } else {
            meta.setColor(Color.BLUE);
        }

        meta.displayName(Component.text(role.getDisplayName()).color(role.getColor()));
        meta.addEnchant(Enchantment.BINDING_CURSE, 1, true);

        helmet.setItemMeta(meta);
        return helmet;
    }

    public static void clearInventory(Player player) {
        player.getInventory().setHelmet(ItemStack.empty());
    }
}

