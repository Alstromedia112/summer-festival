package com.me1q.summerFestival.game.boatrace.goal;

import com.me1q.summerFestival.game.boatrace.constants.Hitbox;
import com.me1q.summerFestival.game.boatrace.goal.GoalLineManager.GoalLine;
import org.bukkit.Location;
import org.bukkit.util.Vector;

public class GoalLineDetector {

    public boolean hasPassedGoalLine(Location from, Location to, GoalLine goalLine) {
        if (goalLine == null || !goalLine.isValid()) {
            return false;
        }

        if (isInGoalHitbox(to, goalLine.getMarker1().getLocation()) ||
            isInGoalHitbox(to, goalLine.getMarker2().getLocation())) {
            return true;
        }

        return doesPathCrossGoalLine(from, to, goalLine);
    }

    private boolean isInGoalHitbox(Location boatLoc, Location markerLoc) {
        double minX = markerLoc.getX() - Hitbox.HALF_WIDTH.value();
        double maxX = markerLoc.getX() + Hitbox.HALF_WIDTH.value();
        double minY = markerLoc.getY() + Hitbox.MIN_Y_OFFSET.value();
        double maxY = markerLoc.getY() + Hitbox.MAX_Y_OFFSET.value();
        double minZ = markerLoc.getZ() - Hitbox.HALF_WIDTH.value();
        double maxZ = markerLoc.getZ() + Hitbox.HALF_WIDTH.value();

        return boatLoc.getX() >= minX && boatLoc.getX() <= maxX &&
            boatLoc.getY() >= minY && boatLoc.getY() <= maxY &&
            boatLoc.getZ() >= minZ && boatLoc.getZ() <= maxZ;
    }

    private boolean doesPathCrossGoalLine(Location from, Location to, GoalLine goalLine) {
        if (!from.getWorld().equals(to.getWorld())) {
            return false;
        }

        Location marker1Loc = goalLine.getMarker1().getLocation();
        Location marker2Loc = goalLine.getMarker2().getLocation();

        Vector fromVec = new Vector(from.getX(), 0, from.getZ());
        Vector toVec = new Vector(to.getX(), 0, to.getZ());
        Vector marker1Vec = new Vector(marker1Loc.getX(), 0, marker1Loc.getZ());
        Vector marker2Vec = new Vector(marker2Loc.getX(), 0, marker2Loc.getZ());

        return doSegmentsIntersect(fromVec, toVec, marker1Vec, marker2Vec);
    }

    private boolean doSegmentsIntersect(Vector p1, Vector p2, Vector p3, Vector p4) {
        double d1 = ccw(p3, p4, p1);
        double d2 = ccw(p3, p4, p2);
        double d3 = ccw(p1, p2, p3);
        double d4 = ccw(p1, p2, p4);

        if (((d1 > 0 && d2 < 0) || (d1 < 0 && d2 > 0)) &&
            ((d3 > 0 && d4 < 0) || (d3 < 0 && d4 > 0))) {
            return true;
        }

        if (d1 == 0 && isOnSegment(p3, p4, p1)) {
            return true;
        }
        if (d2 == 0 && isOnSegment(p3, p4, p2)) {
            return true;
        }
        if (d3 == 0 && isOnSegment(p1, p2, p3)) {
            return true;
        }
        return d4 == 0 && isOnSegment(p1, p2, p4);
    }

    private double ccw(Vector a, Vector b, Vector c) {
        return (b.getX() - a.getX()) * (c.getZ() - a.getZ()) -
            (b.getZ() - a.getZ()) * (c.getX() - a.getX());
    }

    private boolean isOnSegment(Vector p1, Vector p2, Vector point) {
        double minX = Math.min(p1.getX(), p2.getX());
        double maxX = Math.max(p1.getX(), p2.getX());
        double minZ = Math.min(p1.getZ(), p2.getZ());
        double maxZ = Math.max(p1.getZ(), p2.getZ());

        return point.getX() >= minX && point.getX() <= maxX &&
            point.getZ() >= minZ && point.getZ() <= maxZ;
    }
}

