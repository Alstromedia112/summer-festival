package com.me1q.summerFestival.game.shooting.spawner;

import java.util.concurrent.ThreadLocalRandom;
import org.bukkit.Location;
import org.bukkit.World;

public class SpawnArea {

    private final double minX, minY, minZ;
    private final double maxX, maxY, maxZ;

    public SpawnArea(double x1, double y1, double z1, double x2, double y2, double z2) {
        this.minX = Math.min(x1, x2);
        this.maxX = Math.max(x1, x2);
        this.minY = Math.min(y1, y2);
        this.maxY = Math.max(y1, y2);
        this.minZ = Math.min(z1, z2);
        this.maxZ = Math.max(z1, z2);
    }

    public Location getRandomLocation(World world) {
        double x = ThreadLocalRandom.current().nextDouble(minX, Math.max(maxX, minX + 1.0));
        double y = ThreadLocalRandom.current().nextDouble(minY, Math.max(maxY, minY + 1.0));
        double z = ThreadLocalRandom.current().nextDouble(minZ, Math.max(maxZ, minZ + 1.0));
        return new Location(world, x, y, z);
    }

    public double getMinX() {
        return minX;
    }

    public double getMinY() {
        return minY;
    }

    public double getMinZ() {
        return minZ;
    }

    public double getMaxX() {
        return maxX;
    }

    public double getMaxY() {
        return maxY;
    }

    public double getMaxZ() {
        return maxZ;
    }
}

