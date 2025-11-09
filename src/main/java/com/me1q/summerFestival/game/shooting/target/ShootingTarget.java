package com.me1q.summerFestival.game.shooting.target;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ShootingTarget {

    private static final double HITBOX_SIZE = 0.6;
    private static final double SMALL_ARMOR_STAND_HEAD_OFFSET = 0.9875;

    private final Location location;
    private final Player owner;
    private final TargetType type;
    private ArmorStand armorStand;

    public ShootingTarget(Location location, Player owner) {
        this.location = location;
        this.owner = owner;
        this.type = TargetType.getRandomType();
    }

    public void spawn() {
        armorStand = location.getWorld().spawn(location, ArmorStand.class);
        configureArmorStand();
        setHelmet();
        setCustomName();
    }

    private void configureArmorStand() {
        armorStand.setVisible(false);
        armorStand.setGravity(false);
        armorStand.setCanPickupItems(false);
        armorStand.setSmall(true);
        armorStand.setBasePlate(false);
        armorStand.setArms(false);
    }

    private void setHelmet() {
        ItemStack helmet = new ItemStack(type.getMaterial());
        ItemMeta meta = helmet.getItemMeta();
        meta.displayName(Component.text(type.getDisplayName()));
        helmet.setItemMeta(meta);
        armorStand.getEquipment().setHelmet(helmet);
    }

    private void setCustomName() {
        armorStand.customName(Component.text(type.getDisplayName()).color(type.getColor()));
        armorStand.setCustomNameVisible(true);
    }

    public void remove() {
        if (armorStand != null && !armorStand.isDead()) {
            armorStand.remove();
        }
    }

    public boolean isHit(Location hitLocation) {
        if (armorStand == null || hitLocation == null) {
            return false;
        }

        Location headLocation = getHeadLocation();
        double dx = Math.abs(hitLocation.getX() - headLocation.getX());
        double dy = Math.abs(hitLocation.getY() - headLocation.getY());
        double dz = Math.abs(hitLocation.getZ() - headLocation.getZ());

        return dx <= HITBOX_SIZE && dy <= HITBOX_SIZE && dz <= HITBOX_SIZE;
    }

    private Location getHeadLocation() {
        if (armorStand == null) {
            return location;
        }

        Location headLocation = armorStand.getLocation().clone();
        headLocation.add(0, SMALL_ARMOR_STAND_HEAD_OFFSET, 0);
        return headLocation;
    }

    public Player getOwner() {
        return owner;
    }

    public int getPoints() {
        return type.getPoints();
    }
}

