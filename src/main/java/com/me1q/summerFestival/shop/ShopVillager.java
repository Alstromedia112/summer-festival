package com.me1q.summerFestival.shop;

import com.me1q.summerFestival.SummerFestival;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class ShopVillager implements Listener {

    private static final String METADATA_KEY = "summerfestival_shop";
    private static final String METADATA_SHOP_ID = "summerfestival_shop_id";
    private final SummerFestival plugin;
    private final ShopGUI shopGUI;

    public ShopVillager(SummerFestival plugin, ShopGUI shopGUI) {
        this.plugin = plugin;
        this.shopGUI = shopGUI;
    }

    public Villager spawnShopVillager(Location location, String shopId) {
        Villager villager = (Villager) location.getWorld()
            .spawnEntity(location, EntityType.VILLAGER);

        villager.setAI(false);
        villager.setInvulnerable(true);
        villager.setSilent(true);
        villager.setCollidable(false);
        villager.setProfession(Villager.Profession.NITWIT);
        villager.setVillagerType(Villager.Type.PLAINS);
        villager.customName(
            Component.text(shopId).color(NamedTextColor.GOLD));
        villager.setCustomNameVisible(true);

        villager.setMetadata(METADATA_KEY, new FixedMetadataValue(plugin, true));
        villager.setMetadata(METADATA_SHOP_ID, new FixedMetadataValue(plugin, shopId));

        return villager;
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof Villager villager)) {
            return;
        }

        if (!villager.hasMetadata(METADATA_KEY)) {
            return;
        }

        event.setCancelled(true);

        String shopId = "default";
        if (villager.hasMetadata(METADATA_SHOP_ID)) {
            shopId = villager.getMetadata(METADATA_SHOP_ID).getFirst().asString();
        }

        shopGUI.openShop(event.getPlayer(), shopId);
    }

    public static boolean isShopVillager(Villager villager) {
        return villager.hasMetadata(METADATA_KEY);
    }

    public static String getShopId(Villager villager) {
        if (!villager.hasMetadata(METADATA_SHOP_ID)) {
            return "default";
        }
        return villager.getMetadata(METADATA_SHOP_ID).getFirst().asString();
    }
}
