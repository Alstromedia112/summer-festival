package com.me1q.summerFestival.game.boatrace.returnpoint;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class ReturnPointManager {

    private final Map<UUID, Location> returnPoints;

    public ReturnPointManager() {
        this.returnPoints = new HashMap<>();
    }

    public void setReturnPoint(Player player, Location location) {
        returnPoints.put(player.getUniqueId(), location);
    }

    public Location getReturnPoint(Player player) {
        return returnPoints.get(player.getUniqueId());
    }

    public boolean hasReturnPoint(Player player) {
        return returnPoints.containsKey(player.getUniqueId());
    }

    public void clearReturnPoint(Player player) {
        returnPoints.remove(player.getUniqueId());
    }
}

