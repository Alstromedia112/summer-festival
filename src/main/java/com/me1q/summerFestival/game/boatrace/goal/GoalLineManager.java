package com.me1q.summerFestival.game.boatrace.goal;

import com.me1q.summerFestival.SummerFestival;
import com.me1q.summerFestival.core.message.MessageBuilder;
import com.me1q.summerFestival.game.boatrace.constants.Config;
import com.me1q.summerFestival.game.boatrace.constants.Goal;
import com.me1q.summerFestival.game.boatrace.constants.Message;
import com.me1q.summerFestival.game.boatrace.constants.Messages;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class GoalLineManager {

    public static class GoalLine {

        private final ArmorStand marker1;
        private final ArmorStand marker2;

        public GoalLine(ArmorStand marker1, ArmorStand marker2) {
            this.marker1 = marker1;
            this.marker2 = marker2;
        }

        public ArmorStand getMarker1() {
            return marker1;
        }

        public ArmorStand getMarker2() {
            return marker2;
        }

        public boolean isValid() {
            return marker1 != null && !marker1.isDead() && marker2 != null && !marker2.isDead();
        }

        public double getDistance() {
            if (!isValid()) {
                return 0;
            }
            return marker1.getLocation().distance(marker2.getLocation());
        }
    }

    private final SummerFestival plugin;
    private final Map<UUID, List<ArmorStand>> goalLineMarkers;
    private final Map<UUID, GoalLine> playerGoalLines;
    private final Map<UUID, List<Location>> goalLineFences;

    public GoalLineManager(SummerFestival plugin) {
        this.plugin = plugin;
        this.goalLineMarkers = new HashMap<>();
        this.playerGoalLines = new HashMap<>();
        this.goalLineFences = new HashMap<>();
    }

    public ArmorStand createMarker(Location location, Player owner) {
        ArmorStand marker = (ArmorStand) location.getWorld()
            .spawnEntity(location, EntityType.ARMOR_STAND);
        marker.setGravity(false);
        marker.setVisible(false);
        marker.setInvulnerable(true);
        marker.customName(Component.text(Goal.ARMOR_STAND_NAME.text()));
        marker.setMarker(true);

        org.bukkit.NamespacedKey ownerKey = new org.bukkit.NamespacedKey(plugin,
            Goal.PDC_KEY_GOAL_OWNER.text());
        marker.getPersistentDataContainer().set(ownerKey,
            org.bukkit.persistence.PersistentDataType.STRING,
            owner.getUniqueId().toString());

        return marker;
    }

    public int addMarker(Player player, ArmorStand marker) {
        UUID playerId = player.getUniqueId();
        goalLineMarkers.computeIfAbsent(playerId, k -> new ArrayList<>()).add(marker);

        int markerCount = goalLineMarkers.get(playerId).size();

        if (markerCount == Config.REQUIRED_MARKERS.value()) {
            ArmorStand marker1 = goalLineMarkers.get(playerId).get(0);
            ArmorStand marker2 = goalLineMarkers.get(playerId).get(1);
            playerGoalLines.put(playerId, new GoalLine(marker1, marker2));
            placeFences(player);
        }

        return markerCount;
    }

    public void clearGoalLines(Player player) {
        removeFences(player);
        List<ArmorStand> markers = goalLineMarkers.remove(player.getUniqueId());
        if (markers != null) {
            for (ArmorStand marker : markers) {
                if (marker != null && !marker.isDead()) {
                    marker.remove();
                }
            }
        }
        playerGoalLines.remove(player.getUniqueId());
    }

    public boolean hasGoalLine(Player player) {
        GoalLine goalLine = playerGoalLines.get(player.getUniqueId());
        return goalLine != null && goalLine.isValid();
    }

    public GoalLine getGoalLine(Player player) {
        return playerGoalLines.get(player.getUniqueId());
    }

    public int getMarkerCount(Player player) {
        List<ArmorStand> markers = goalLineMarkers.get(player.getUniqueId());
        return markers != null ? markers.size() : 0;
    }

    public void sendMarkerPlacedMessage(Player player, int markerCount) {
        if (markerCount == 1) {
            player.sendMessage(
                MessageBuilder.success(Message.MARKER_1_PLACED.text()));
        } else if (markerCount == Config.REQUIRED_MARKERS.value()) {
            GoalLine goalLine = playerGoalLines.get(player.getUniqueId());
            player.sendMessage(MessageBuilder.success(Message.MARKER_2_PLACED.text()));
            player.sendMessage(
                MessageBuilder.success(Message.GOAL_LINE_COMPLETE.text()));

            if (goalLine != null && goalLine.isValid()) {
                double distance = goalLine.getDistance();
                player.sendMessage(MessageBuilder.info(
                    Messages.markerDistance(distance)));
            }
        }
    }

    private void placeFences(Player player) {
        GoalLine goalLine = playerGoalLines.get(player.getUniqueId());
        if (goalLine == null || !goalLine.isValid()) {
            return;
        }

        Location loc1 = goalLine.getMarker1().getLocation();
        Location loc2 = goalLine.getMarker2().getLocation();

        List<Location> fenceLocations = calculateFenceLocations(loc1, loc2);
        goalLineFences.put(player.getUniqueId(), fenceLocations);

        for (Location fenceLocation : fenceLocations) {
            fenceLocation.getBlock().setType(org.bukkit.Material.OAK_FENCE);
        }
    }

    public void placeFencesForPlayer(Player player) {
        placeFences(player);
    }

    public void removeFences(Player player) {
        List<Location> fenceLocations = goalLineFences.remove(player.getUniqueId());
        if (fenceLocations != null) {
            for (Location fenceLocation : fenceLocations) {
                if (fenceLocation.getBlock().getType() == org.bukkit.Material.OAK_FENCE) {
                    fenceLocation.getBlock().setType(org.bukkit.Material.AIR);
                }
            }
        }
    }

    private List<Location> calculateFenceLocations(Location loc1, Location loc2) {
        List<Location> locations = new ArrayList<>();

        int x1 = loc1.getBlockX();
        int y1 = loc1.getBlockY();
        int z1 = loc1.getBlockZ();

        int x2 = loc2.getBlockX();
        int y2 = loc2.getBlockY();
        int z2 = loc2.getBlockZ();

        int dx = Math.abs(x2 - x1);
        int dz = Math.abs(z2 - z1);

        if (dx > dz) {
            int minX = Math.min(x1, x2);
            int maxX = Math.max(x1, x2);
            int y = Math.max(y1, y2);

            for (int x = minX; x <= maxX; x++) {
                locations.add(new Location(loc1.getWorld(), x, y, z1));
            }
        } else {
            int minZ = Math.min(z1, z2);
            int maxZ = Math.max(z1, z2);
            int y = Math.max(y1, y2);

            for (int z = minZ; z <= maxZ; z++) {
                locations.add(new Location(loc1.getWorld(), x1, y, z));
            }
        }

        return locations;
    }
}

