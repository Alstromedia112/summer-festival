package com.me1q.summerFestival.game.boatrace.boatstand;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;

public class BoatStandManager {

    private final Map<UUID, List<ArmorStand>> playerBoatStands;

    public BoatStandManager() {
        this.playerBoatStands = new HashMap<>();
    }

    public void addBoatStand(Player player, ArmorStand armorStand) {
        playerBoatStands.computeIfAbsent(player.getUniqueId(), k -> new ArrayList<>())
            .add(armorStand);
    }

    public List<ArmorStand> getBoatStands(Player player) {
        return playerBoatStands.getOrDefault(player.getUniqueId(), new ArrayList<>());
    }

    public List<Location> getBoatStandLocations(Player player) {
        List<Location> locations = new ArrayList<>();
        List<ArmorStand> stands = getBoatStands(player);

        for (ArmorStand stand : stands) {
            if (stand != null && stand.isValid()) {
                locations.add(stand.getLocation());
            }
        }

        return locations;
    }

    public void removeBoatStand(ArmorStand armorStand) {
        for (List<ArmorStand> stands : playerBoatStands.values()) {
            stands.remove(armorStand);
        }
    }

    public void clearBoatStands(Player player) {
        List<ArmorStand> stands = playerBoatStands.remove(player.getUniqueId());
        if (stands != null) {
            for (ArmorStand stand : stands) {
                if (stand != null && stand.isValid()) {
                    stand.remove();
                }
            }
        }
    }

    public boolean hasBoatStands(Player player) {
        List<ArmorStand> stands = getBoatStands(player);
        return !stands.isEmpty() && stands.stream()
            .anyMatch(stand -> stand != null && stand.isValid());
    }

    public int getBoatStandCount(Player player) {
        return (int) getBoatStands(player).stream()
            .filter(stand -> stand != null && stand.isValid())
            .count();
    }
}

