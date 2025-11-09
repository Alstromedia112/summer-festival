package com.me1q.summerFestival.shop;

import com.me1q.summerFestival.SummerFestival;
import com.me1q.summerFestival.core.message.MessageBuilder;
import com.me1q.summerFestival.currency.CurrencyFormatter;
import com.me1q.summerFestival.currency.CurrencyManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ShopGUI {

    private static final int GUI_SIZE = 54;

    private final CurrencyManager currencyManager;
    private final ShopItemManager itemManager;

    public ShopGUI(SummerFestival plugin, ShopItemManager itemManager) {
        this.currencyManager = plugin.getCurrencyManager();
        this.itemManager = itemManager;
    }

    public void openShop(Player player, String shopId) {
        Inventory inventory = Bukkit.createInventory(null, GUI_SIZE,
            Component.text(shopId).color(NamedTextColor.GOLD));

        int slot = 0;
        for (ShopItemData item : itemManager.getShopItems(shopId)) {
            if (slot >= GUI_SIZE - 9) {
                break;
            }
            inventory.setItem(slot, item.createDisplayItem());
            slot++;
        }

        if (player.isOp()) {
            addEditButton(inventory);
        }
        addBalanceDisplay(inventory, player);
        addCloseButton(inventory);

        player.openInventory(inventory);
    }

    private void addEditButton(Inventory inventory) {
        ItemStack editItem = new ItemStack(Material.REDSTONE);
        ItemMeta meta = editItem.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.text("ショップ編集").color(NamedTextColor.RED));
            meta.lore(java.util.List.of(
                Component.text("クリックして編集モードを開く").color(NamedTextColor.GRAY)
            ));
            editItem.setItemMeta(meta);
        }
        inventory.setItem(45, editItem);
    }

    private void addBalanceDisplay(Inventory inventory, Player player) {
        int balance = currencyManager.getBalance(player);
        ItemStack balanceItem = new ItemStack(Material.GOLD_INGOT);
        ItemMeta meta = balanceItem.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.text("所持金").color(NamedTextColor.GOLD));
            meta.lore(java.util.List.of(
                Component.text(CurrencyFormatter.format(balance)).color(NamedTextColor.YELLOW)
            ));
            balanceItem.setItemMeta(meta);
        }
        inventory.setItem(49, balanceItem);
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

    public void handlePurchase(Player player, String shopId, ShopItemData item) {
        int price = item.getPrice();

        if (!currencyManager.hasBalance(player, price)) {
            player.sendMessage(MessageBuilder.error(
                "残高が不足しています。必要額: " + CurrencyFormatter.format(price)));
            player.closeInventory();
            return;
        }

        if (player.getInventory().firstEmpty() == -1) {
            player.sendMessage(MessageBuilder.error("インベントリに空きがありません。"));
            player.closeInventory();
            return;
        }

        currencyManager.removeBalance(player, price);
        player.getInventory().addItem(item.createPurchaseItem());

        player.sendMessage(MessageBuilder.success(
            item.getDisplayName() + " を " + CurrencyFormatter.format(price) + " で購入しました。"
        ));

        openShop(player, shopId);
    }

    public static boolean isShopGUI(Inventory inventory) {
        if (inventory == null || inventory.getSize() != GUI_SIZE) {
            return false;
        }

        ItemStack balanceItem = inventory.getItem(49);
        ItemStack closeButton = inventory.getItem(53);

        ItemStack slot45 = inventory.getItem(45);
        return balanceItem != null && balanceItem.getType() == Material.GOLD_INGOT &&
            closeButton != null && closeButton.getType() == Material.BARRIER &&
            (slot45 == null || slot45.getType() == Material.REDSTONE
                || slot45.getType() != Material.EMERALD);
    }

    public static String getShopIdFromInventory(Inventory inventory) {
        if (inventory.getViewers().isEmpty()) {
            return "default";
        }

        Component title = inventory.getViewers().getFirst().getOpenInventory().title();
        String shopId = net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText()
            .serialize(title);

        return shopId.isEmpty() ? "default" : shopId;
    }
}

