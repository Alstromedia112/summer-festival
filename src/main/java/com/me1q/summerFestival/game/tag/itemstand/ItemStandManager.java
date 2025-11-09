package com.me1q.summerFestival.game.tag.itemstand;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class ItemStandManager {

    private final List<UUID> itemStands;
    private final Map<UUID, Long> standCooldowns;
    private final Map<UUID, ItemStack> standItems;
    private final Map<UUID, BukkitTask> cooldownTasks;
    private static final long COOLDOWN_DURATION_MS = 30000;

    public ItemStandManager() {
        this.itemStands = new ArrayList<>();
        this.standCooldowns = new HashMap<>();
        this.standItems = new HashMap<>();
        this.cooldownTasks = new HashMap<>();
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

        ItemStack randomItem = ItemStandRandomItemGenerator.getRandomItem();
        stand.getEquipment().setHelmet(randomItem);

        UUID standUuid = stand.getUniqueId();
        itemStands.add(standUuid);
        standItems.put(standUuid, randomItem.clone());
    }

    public void removeAllItemStands(org.bukkit.World world) {
        List<UUID> toRemove = new ArrayList<>(itemStands);
        for (UUID uuid : toRemove) {
            if (cooldownTasks.containsKey(uuid)) {
                cooldownTasks.get(uuid).cancel();
            }

            world.getEntities().stream()
                .filter(entity -> entity.getUniqueId().equals(uuid))
                .filter(entity -> entity instanceof ArmorStand)
                .forEach(entity -> {
                    entity.remove();
                    itemStands.remove(uuid);
                });
        }
        standCooldowns.clear();
        standItems.clear();
        cooldownTasks.clear();
    }

    public boolean isStandOnCooldown(UUID standUuid) {
        if (!standCooldowns.containsKey(standUuid)) {
            return false;
        }

        long cooldownEndTime = standCooldowns.get(standUuid);
        long currentTime = System.currentTimeMillis();

        if (currentTime >= cooldownEndTime) {
            standCooldowns.remove(standUuid);
            return false;
        }

        return true;
    }

    public void setStandCooldown(UUID standUuid, org.bukkit.plugin.Plugin plugin) {
        long cooldownEndTime = System.currentTimeMillis() + COOLDOWN_DURATION_MS;
        standCooldowns.put(standUuid, cooldownEndTime);

        hideStandItem(standUuid);

        if (cooldownTasks.containsKey(standUuid)) {
            cooldownTasks.get(standUuid).cancel();
        }

        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                showStandItem(standUuid);
                cooldownTasks.remove(standUuid);
            }
        }.runTaskLater(plugin, COOLDOWN_DURATION_MS / 50);

        cooldownTasks.put(standUuid, task);
    }

    private void hideStandItem(UUID standUuid) {
        Entity entity = Bukkit.getEntity(standUuid);
        if (entity instanceof ArmorStand stand) {
            stand.getEquipment().setHelmet(null);
        }
    }


    public ItemStack getItemFromStand(UUID standUuid) {
        if (standItems.containsKey(standUuid)) {
            return standItems.get(standUuid).clone();
        }
        return null;
    }

    private void showStandItem(UUID standUuid) {
        Entity entity = Bukkit.getEntity(standUuid);
        if (entity instanceof ArmorStand stand) {
            ItemStack newRandomItem = ItemStandRandomItemGenerator.getRandomItem();
            stand.getEquipment().setHelmet(newRandomItem);
            standItems.put(standUuid, newRandomItem.clone());
        }
    }

    public boolean isItemStand(UUID uuid) {
        return itemStands.contains(uuid);
    }

    public long getRemainingCooldown(UUID standUuid) {
        if (!standCooldowns.containsKey(standUuid)) {
            return 0;
        }

        long cooldownEndTime = standCooldowns.get(standUuid);
        long currentTime = System.currentTimeMillis();
        long remaining = cooldownEndTime - currentTime;

        return Math.max(0, remaining / 1000);
    }
}

