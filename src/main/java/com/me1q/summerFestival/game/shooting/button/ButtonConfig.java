package com.me1q.summerFestival.game.shooting.button;

import com.me1q.summerFestival.game.shooting.spawner.SpawnArea;
import org.bukkit.Location;

public class ButtonConfig {

    private final Location buttonLocation;
    private final SpawnArea spawnArea;

    public ButtonConfig(Location buttonLocation, SpawnArea spawnArea) {
        this.buttonLocation = buttonLocation;
        this.spawnArea = spawnArea;
    }

    public Location getButtonLocation() {
        return buttonLocation;
    }

    public SpawnArea getSpawnArea() {
        return spawnArea;
    }

    public boolean isButtonAt(Location location) {
        return buttonLocation.getWorld().equals(location.getWorld())
            && buttonLocation.getBlockX() == location.getBlockX()
            && buttonLocation.getBlockY() == location.getBlockY()
            && buttonLocation.getBlockZ() == location.getBlockZ();
    }
}

