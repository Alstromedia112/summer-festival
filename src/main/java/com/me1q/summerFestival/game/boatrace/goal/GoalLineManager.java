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

    public GoalLineManager(SummerFestival plugin) {
        this.plugin = plugin;
        this.goalLineMarkers = new HashMap<>();
        this.playerGoalLines = new HashMap<>();
    }

    public void loadExistingGoalMarkers() {
        org.bukkit.NamespacedKey ownerKey = new org.bukkit.NamespacedKey(plugin,
            Goal.PDC_KEY_GOAL_OWNER.text());
        int loadedCount = 0;

        for (org.bukkit.World world : plugin.getServer().getWorlds()) {
            for (ArmorStand armorStand : world.getEntitiesByClass(ArmorStand.class)) {
                if (!isGoalMarker(armorStand)) {
                    continue;
                }

                String ownerUuidString = armorStand.getPersistentDataContainer()
                    .get(ownerKey, org.bukkit.persistence.PersistentDataType.STRING);

                if (ownerUuidString != null) {
                    try {
                        UUID ownerUuid = UUID.fromString(ownerUuidString);
                        goalLineMarkers.computeIfAbsent(ownerUuid, k -> new ArrayList<>())
                            .add(armorStand);
                        loadedCount++;

                        plugin.getLogger().info("Loaded goal marker at " +
                            armorStand.getLocation().getBlockX() + ", " +
                            armorStand.getLocation().getBlockY() + ", " +
                            armorStand.getLocation().getBlockZ() +
                            " (Owner: " + ownerUuidString + ")");
                    } catch (IllegalArgumentException e) {
                        plugin.getLogger()
                            .warning("Invalid UUID in goal marker: " + ownerUuidString);
                    }
                } else {
                    plugin.getLogger().warning("Found goal marker without owner data at " +
                        armorStand.getLocation().getBlockX() + ", " +
                        armorStand.getLocation().getBlockY() + ", " +
                        armorStand.getLocation().getBlockZ());
                }
            }
        }

        if (loadedCount > 0) {
            plugin.getLogger().info("Loaded " + loadedCount + " goal marker(s) from the world");
        }

        reconstructGoalLines();
    }

    private void reconstructGoalLines() {
        for (Map.Entry<UUID, List<ArmorStand>> entry : goalLineMarkers.entrySet()) {
            UUID ownerUuid = entry.getKey();
            List<ArmorStand> markers = entry.getValue();

            if (markers.size() == Config.REQUIRED_MARKERS.value()) {
                playerGoalLines.put(ownerUuid, new GoalLine(markers.get(0), markers.get(1)));
            } else if (markers.size() > Config.REQUIRED_MARKERS.value()) {
                plugin.getLogger().warning("Player " + ownerUuid + " has " + markers.size() +
                    " markers (expected 2). Creating goal line with first 2 markers.");
                playerGoalLines.put(ownerUuid, new GoalLine(markers.get(0), markers.get(1)));
            }
        }
    }

    public ArmorStand createMarker(Location location, Player owner) {
        ArmorStand marker = (ArmorStand) location.getWorld()
            .spawnEntity(location, EntityType.ARMOR_STAND);
        marker.setVisible(false);
        marker.setGravity(false);
        marker.setInvulnerable(true);
        marker.customName(Component.text(Goal.ARMOR_STAND_NAME.text()));
        marker.setCustomNameVisible(true);
        marker.setMarker(true);
        marker.setGlowing(true);

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
        }

        return markerCount;
    }

    public void clearGoalLines(Player player) {
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

    public List<ArmorStand> getAllGoalMarkers() {
        List<ArmorStand> allMarkers = new ArrayList<>();
        for (List<ArmorStand> markers : goalLineMarkers.values()) {
            allMarkers.addAll(markers);
        }
        return allMarkers;
    }

    private boolean isGoalMarker(ArmorStand armorStand) {
        if (armorStand.customName() == null || armorStand.isVisible() || !armorStand.isGlowing()) {
            return false;
        }

        String name = String.valueOf(armorStand.customName());
        return Goal.ARMOR_STAND_NAME.text().equals(name);
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
}

