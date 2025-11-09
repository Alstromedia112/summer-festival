package com.me1q.summerFestival.shop;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.me1q.summerFestival.SummerFestival;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class ShopItemManager {

    private final SummerFestival plugin;
    private final File shopFolder;
    private final Gson gson;
    private final Map<String, Map<String, ShopItemData>> shopItems;

    public ShopItemManager(SummerFestival plugin) {
        this.plugin = plugin;
        this.shopFolder = new File(plugin.getDataFolder(), "shop");
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.shopItems = new LinkedHashMap<>();

        if (!shopFolder.exists()) {
            if (!shopFolder.mkdirs()) {
                plugin.getLogger().warning("Failed to create shop folder");
            }
        }

        loadAllShops();
    }

    private void loadAllShops() {
        File[] files = shopFolder.listFiles((dir, name) -> name.endsWith(".json"));
        if (files == null || files.length == 0) {
            createDefaultShop("default");
            return;
        }

        for (File file : files) {
            String shopId = file.getName().replace(".json", "");
            loadShop(shopId);
        }

        plugin.getLogger().info("Loaded " + shopItems.size() + " shop(s)");
    }

    private void loadShop(String shopId) {
        File dataFile = new File(shopFolder, shopId + ".json");

        if (!dataFile.exists()) {
            createDefaultShop(shopId);
            return;
        }

        try (FileReader reader = new FileReader(dataFile)) {
            Type type = new TypeToken<Map<String, ShopItemData>>() {
            }.getType();
            Map<String, ShopItemData> loaded = gson.fromJson(reader, type);
            if (loaded != null) {
                shopItems.put(shopId, loaded);
                plugin.getLogger()
                    .info("Loaded shop '" + shopId + "' with " + loaded.size() + " items");
            }
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to load shop '" + shopId + "': " + e.getMessage());
            createDefaultShop(shopId);
        }
    }

    private void createDefaultShop(String shopId) {
        Map<String, ShopItemData> items = new LinkedHashMap<>();
        items.put("apple", new ShopItemData("apple", "APPLE", "りんご", 50, 1));
        shopItems.put(shopId, items);
        saveShop(shopId);
    }

    public void saveShop(String shopId) {
        Map<String, ShopItemData> items = shopItems.get(shopId);
        if (items == null) {
            return;
        }

        File dataFile = new File(shopFolder, shopId + ".json");
        try {
            if (!shopFolder.exists()) {
                if (!shopFolder.mkdirs()) {
                    plugin.getLogger().warning("Failed to create shop folder");
                }
            }
            try (FileWriter writer = new FileWriter(dataFile)) {
                gson.toJson(items, writer);
            }
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save shop '" + shopId + "': " + e.getMessage());
        }
    }

    public Collection<ShopItemData> getShopItems(String shopId) {
        Map<String, ShopItemData> items = shopItems.get(shopId);
        if (items == null) {
            loadShop(shopId);
            items = shopItems.get(shopId);
        }
        return items != null ? new ArrayList<>(items.values()) : new ArrayList<>();
    }

    public void addItem(String shopId, ShopItemData item) {
        shopItems.computeIfAbsent(shopId, k -> new LinkedHashMap<>()).put(item.getId(), item);
        saveShop(shopId);
    }

    public void removeItem(String shopId, String itemId) {
        Map<String, ShopItemData> items = shopItems.get(shopId);
        if (items != null) {
            items.remove(itemId);
            saveShop(shopId);
        }
    }

    public void updateItem(String shopId, ShopItemData item) {
        Map<String, ShopItemData> items = shopItems.get(shopId);
        if (items != null) {
            items.put(item.getId(), item);
            saveShop(shopId);
        }
    }

    public ShopItemData findByMaterial(String shopId, org.bukkit.Material material) {
        Map<String, ShopItemData> items = shopItems.get(shopId);
        if (items == null) {
            return null;
        }

        for (ShopItemData item : items.values()) {
            if (item.getMaterial() == material) {
                return item;
            }
        }
        return null;
    }

    public void deleteShop(String shopId) {
        if (shopId.equals("default")) {
            plugin.getLogger().warning("Cannot delete default shop");
            return;
        }

        shopItems.remove(shopId);

        File dataFile = new File(shopFolder, shopId + ".json");
        if (dataFile.exists()) {
            if (dataFile.delete()) {
                plugin.getLogger().info("Deleted shop file: " + shopId + ".json");
            } else {
                plugin.getLogger().warning("Failed to delete shop file: " + shopId + ".json");
            }
        }
    }
}

