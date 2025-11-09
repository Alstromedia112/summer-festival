package com.me1q.summerFestival.shop;

import com.me1q.summerFestival.core.message.MessageBuilder;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ShopAdminGUI {

    private static final int GUI_SIZE = 54;

    private final ShopItemManager itemManager;

    public ShopAdminGUI(ShopItemManager itemManager) {
        this.itemManager = itemManager;
    }

    public void openAdminGUI(Player player, String shopId) {
        Inventory inventory = Bukkit.createInventory(null, GUI_SIZE,
            Component.text(shopId).color(NamedTextColor.GOLD));

        int slot = 0;
        for (ShopItemData item : itemManager.getShopItems(shopId)) {
            if (slot >= GUI_SIZE - 9) {
                break;
            }
            inventory.setItem(slot, item.createAdminDisplayItem());
            slot++;
        }

        addAddButton(inventory);
        addSaveButton(inventory);
        addCloseButton(inventory);

        player.openInventory(inventory);
    }

    private void addAddButton(Inventory inventory) {
        ItemStack addItem = new ItemStack(Material.EMERALD);
        ItemMeta meta = addItem.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.text("アイテム追加").color(NamedTextColor.GREEN));
            meta.lore(List.of(
                Component.text("手に持っているアイテムを").color(NamedTextColor.GRAY),
                Component.text("ショップに追加します").color(NamedTextColor.GRAY)
            ));
            addItem.setItemMeta(meta);
        }
        inventory.setItem(45, addItem);
    }

    private void addSaveButton(Inventory inventory) {
        ItemStack saveItem = new ItemStack(Material.WRITABLE_BOOK);
        ItemMeta meta = saveItem.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.text("保存").color(NamedTextColor.YELLOW));
            meta.lore(List.of(
                Component.text("変更を保存します").color(NamedTextColor.GRAY)
            ));
            saveItem.setItemMeta(meta);
        }
        inventory.setItem(49, saveItem);
    }

    private void addCloseButton(Inventory inventory) {
        ItemStack closeItem = new ItemStack(Material.BARRIER);
        ItemMeta meta = closeItem.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.text("閉じる").color(NamedTextColor.RED));
            closeItem.setItemMeta(meta);
        }
        inventory.setItem(53, closeItem);
    }

    public void handleAddItem(Player player, String shopId) {
        ItemStack handItem = player.getInventory().getItemInMainHand();
        if (handItem.getType() == Material.AIR) {
            player.sendMessage(MessageBuilder.error("手にアイテムを持ってください。"));
            return;
        }

        String id = handItem.getType().name().toLowerCase();
        String displayName = handItem.getType().name();

        ItemMeta meta = handItem.getItemMeta();
        if (meta != null && meta.hasDisplayName() && meta.displayName() != null) {
            displayName = net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText()
                .serialize(meta.displayName());
        }

        ShopItemData newItem = new ShopItemData(
            id,
            handItem.getType().name(),
            displayName,
            100,
            handItem.getAmount()
        );

        itemManager.addItem(shopId, newItem);
        player.sendMessage(MessageBuilder.success(displayName + " をショップに追加しました。"));
        openAdminGUI(player, shopId);
    }

    public void handlePriceChange(Player player, String shopId, ShopItemData item, boolean increase,
        boolean shift) {
        int change = shift ? 100 : 10;
        if (!increase) {
            change = -change;
        }

        item.addPrice(change);
        itemManager.updateItem(shopId, item);

        openAdminGUI(player, shopId);
    }

    public void handleRemoveItem(Player player, String shopId, ShopItemData item) {
        itemManager.removeItem(shopId, item.getId());
        player.sendMessage(MessageBuilder.success(item.getDisplayName() + " を削除しました。"));
        openAdminGUI(player, shopId);
    }

    public void handleSave(Player player, String shopId) {
        itemManager.saveShop(shopId);
        player.sendMessage(MessageBuilder.success("ショップデータを保存しました。"));
    }

    public static boolean isAdminGUI(Inventory inventory) {
        if (inventory == null || inventory.getSize() != GUI_SIZE) {
            return false;
        }

        ItemStack addButton = inventory.getItem(45);
        ItemStack saveButton = inventory.getItem(49);
        ItemStack closeButton = inventory.getItem(53);

        return addButton != null && addButton.getType() == Material.EMERALD &&
            saveButton != null && saveButton.getType() == Material.WRITABLE_BOOK &&
            closeButton != null && closeButton.getType() == Material.BARRIER;
    }

    public static String getShopIdFromInventory(Inventory inventory) {
        if (inventory.getViewers().isEmpty()) {
            return "default";
        }

        Component title = inventory.getViewers().getFirst().getOpenInventory().title();
        String shopId = PlainTextComponentSerializer.plainText()
            .serialize(title);

        return shopId.isEmpty() ? "default" : shopId;
    }
}

