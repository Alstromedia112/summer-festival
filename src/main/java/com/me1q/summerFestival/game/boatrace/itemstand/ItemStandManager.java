package com.me1q.summerFestival.game.boatrace.itemstand;

import com.me1q.summerFestival.game.boatrace.item.SpeedBoost;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class ItemStandManager {

    private final List<UUID> itemStands;
    private final Map<UUID, Long> playerCooldowns;
    private final Random random;
    private static final long COOLDOWN_DURATION_MS = 5000;

    public ItemStandManager() {
        this.itemStands = new ArrayList<>();
        this.playerCooldowns = new HashMap<>();
        this.random = new Random();
    }

    public void spawnItemStand(Location location) {
        ArmorStand stand = (ArmorStand) location.getWorld()
            .spawnEntity(location, EntityType.ARMOR_STAND);

        stand.setGravity(false);
        stand.setVisible(false);
        stand.setBasePlate(true);
        stand.setInvulnerable(true);
        stand.setCanMove(false);
        stand.setCanTick(false);

        ItemStack randomItem = getRandomItem();
        stand.getEquipment().setHelmet(randomItem);

        itemStands.add(stand.getUniqueId());

    }

    public void removeAllItemStands(org.bukkit.World world) {
        List<UUID> toRemove = new ArrayList<>(itemStands);
        for (UUID uuid : toRemove) {
            world.getEntities().stream()
                .filter(entity -> entity.getUniqueId().equals(uuid))
                .filter(entity -> entity instanceof ArmorStand)
                .forEach(entity -> {
                    entity.remove();
                    itemStands.remove(uuid);
                });

        }
    }

    public boolean isOnCooldown(UUID playerUuid) {
        if (!playerCooldowns.containsKey(playerUuid)) {
            return false;
        }

        long cooldownEndTime = playerCooldowns.get(playerUuid);
        long currentTime = System.currentTimeMillis();

        if (currentTime >= cooldownEndTime) {
            playerCooldowns.remove(playerUuid);
            return false;
        }

        return true;
    }

    public void setCooldown(UUID playerUuid) {
        long cooldownEndTime = System.currentTimeMillis() + COOLDOWN_DURATION_MS;
        playerCooldowns.put(playerUuid, cooldownEndTime);
    }


    public boolean isItemStand(UUID uuid) {
        return itemStands.contains(uuid);
    }

    private ItemStack getRandomItem() {
        List<ItemStack> availableItems = List.of(
            SpeedBoost.createItem()
        );

        return availableItems.get(random.nextInt(availableItems.size()));
    }
}


