package com.me1q.summerFestival.shop;

import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ShopItemData {

    private final String id;
    private String material;
    private String displayName;
    private int price;
    private int amount;

    public ShopItemData(String id, String material, String displayName, int price, int amount) {
        this.id = id;
        this.material = material;
        this.displayName = displayName;
        this.price = price;
        this.amount = amount;
    }

    public String getId() {
        return id;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public Material getMaterial() {
        try {
            return Material.valueOf(material.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Material.STONE;
        }
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = Math.max(0, price);
    }

    public void addPrice(int amount) {
        setPrice(this.price + amount);
    }

    public ItemStack createDisplayItem() {
        ItemStack item = new ItemStack(getMaterial());
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.text(displayName).color(NamedTextColor.YELLOW));
            meta.lore(List.of(
                Component.text("価格: " + price + "円").color(NamedTextColor.GOLD),
                Component.text("数量: " + amount).color(NamedTextColor.GRAY),
                Component.empty(),
                Component.text("クリックで購入").color(NamedTextColor.GREEN)
            ));
            item.setItemMeta(meta);
        }
        return item;
    }

    public ItemStack createAdminDisplayItem() {
        ItemStack item = new ItemStack(getMaterial());
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.text(displayName).color(NamedTextColor.AQUA));
            meta.lore(List.of(
                Component.text("ID: " + id).color(NamedTextColor.GRAY),
                Component.text("価格: " + price + "円").color(NamedTextColor.GOLD),
                Component.text("数量: " + amount).color(NamedTextColor.WHITE),
                Component.empty(),
                Component.text("左クリック: +10円").color(NamedTextColor.GREEN),
                Component.text("右クリック: -10円").color(NamedTextColor.RED),
                Component.text("Shift+クリック: 増減幅を増やす(±100)").color(NamedTextColor.GREEN)
            ));
            item.setItemMeta(meta);
        }
        return item;
    }

    public ItemStack createPurchaseItem() {
        return new ItemStack(getMaterial(), amount);
    }
}

