package com.me1q.summerFestival.shop;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class ShopListener implements Listener {

    private final ShopGUI shopGUI;
    private final ShopAdminGUI adminGUI;
    private final ShopItemManager itemManager;

    public ShopListener(ShopGUI shopGUI, ShopAdminGUI adminGUI, ShopItemManager itemManager) {
        this.shopGUI = shopGUI;
        this.adminGUI = adminGUI;
        this.itemManager = itemManager;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        if (ShopAdminGUI.isAdminGUI(event.getInventory())) {
            handleAdminClick(event, player);
        } else if (ShopGUI.isShopGUI(event.getInventory())) {
            handleShopClick(event, player);
        }
    }

    private void handleShopClick(InventoryClickEvent event, Player player) {
        event.setCancelled(true);

        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) {
            return;
        }

        String shopId = ShopGUI.getShopIdFromInventory(event.getInventory());

        if (clickedItem.getType() == Material.BARRIER) {
            player.closeInventory();
            return;
        }

        if (clickedItem.getType() == Material.GOLD_INGOT) {
            return;
        }

        if (clickedItem.getType() == Material.REDSTONE && event.getSlot() == 45 && player.isOp()) {
            player.closeInventory();
            adminGUI.openAdminGUI(player, shopId);
            return;
        }

        ShopItemData item = itemManager.findByMaterial(shopId, clickedItem.getType());
        if (item != null) {
            shopGUI.handlePurchase(player, shopId, item);
        }
    }

    private void handleAdminClick(InventoryClickEvent event, Player player) {
        event.setCancelled(true);

        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) {
            return;
        }

        String shopId = ShopAdminGUI.getShopIdFromInventory(event.getInventory());

        if (clickedItem.getType() == Material.BARRIER) {
            player.closeInventory();
            return;
        }

        if (clickedItem.getType() == Material.EMERALD) {
            player.closeInventory();
            adminGUI.handleAddItem(player, shopId);
            return;
        }

        if (clickedItem.getType() == Material.WRITABLE_BOOK) {
            adminGUI.handleSave(player, shopId);
            return;
        }

        ShopItemData item = itemManager.findByMaterial(shopId, clickedItem.getType());
        if (item != null) {
            ClickType click = event.getClick();
            boolean shift = click.isShiftClick();

            if (click == ClickType.DROP || click == ClickType.CONTROL_DROP) {
                adminGUI.handleRemoveItem(player, shopId, item);
            } else if (click.isRightClick()) {
                adminGUI.handlePriceChange(player, shopId, item, false, shift);
            } else if (click.isLeftClick()) {
                adminGUI.handlePriceChange(player, shopId, item, true, shift);
            }
        }
    }
}

