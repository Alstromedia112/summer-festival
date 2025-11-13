package com.me1q.summerFestival.teleportbook;

import org.bukkit.Location;

public class TeleportLocation {

    private final String name;
    private final Location location;

    public TeleportLocation(String name, Location location) {
        this.name = name;
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public Location getLocation() {
        return location;
    }
}

