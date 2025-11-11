package com.me1q.summerFestival.game.boatrace.returnpoint;

import org.bukkit.Location;

public class ReturnPointManager {

    private Location returnPoint;

    public ReturnPointManager() {
        this.returnPoint = null;
    }

    public void setReturnPoint(Location location) {
        this.returnPoint = location;
    }

    public Location getReturnPoint() {
        return returnPoint;
    }

    public boolean hasReturnPoint() {
        return returnPoint != null;
    }

    public void clearReturnPoint() {
        returnPoint = null;
    }
}

