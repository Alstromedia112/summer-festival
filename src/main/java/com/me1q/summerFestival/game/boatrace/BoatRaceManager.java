package com.me1q.summerFestival.game.boatrace;

import com.me1q.summerFestival.SummerFestival;
import com.me1q.summerFestival.core.message.MessageBuilder;
import com.me1q.summerFestival.game.boatrace.session.BoatRaceRecruitSession;
import com.me1q.summerFestival.game.boatrace.session.BoatRaceSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class BoatRaceManager implements Listener {

    // Helper class to represent a goal line pair
    public static class GoalLine {

        private final ArmorStand marker1;
        private final ArmorStand marker2;

        public GoalLine(ArmorStand marker1, ArmorStand marker2) {
            this.marker1 = marker1;
            this.marker2 = marker2;
        }

        public boolean isValid() {
            return marker1 != null && !marker1.isDead() && marker2 != null && !marker2.isDead();
        }
    }

    private final SummerFestival plugin;
    private final Map<UUID, List<ArmorStand>> goalLineMarkers;
    private final Map<UUID, GoalLine> playerGoalLines;

    // Recruitment and multi-player race sessions
    private BoatRaceRecruitSession activeRecruitSession;
    private BoatRaceSession activeRaceSession;

    public BoatRaceManager(SummerFestival plugin) {
        this.plugin = plugin;
        this.goalLineMarkers = new HashMap<>();
        this.playerGoalLines = new HashMap<>();
        this.activeRecruitSession = null;
        this.activeRaceSession = null;
    }

    // Load existing goal markers from the world
    public void loadExistingGoalMarkers() {
        org.bukkit.NamespacedKey ownerKey = new org.bukkit.NamespacedKey(plugin,
            "boatrace_goal_owner");
        int loadedCount = 0;

        for (org.bukkit.World world : plugin.getServer().getWorlds()) {
            for (ArmorStand armorStand : world.getEntitiesByClass(ArmorStand.class)) {
                // Check if this is a goal marker
                if (armorStand.customName() == null || armorStand.isVisible()
                    || !armorStand.isGlowing()) {
                    continue;
                }

                String name = String.valueOf(armorStand.customName());

                if (!"§e§lゴール地点".equals(name)) {
                    continue;
                }

                // This is a goal marker - try to get the owner UUID
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

        // Reconstruct goal line pairs
        for (Map.Entry<UUID, List<ArmorStand>> entry : goalLineMarkers.entrySet()) {
            UUID ownerUuid = entry.getKey();
            List<ArmorStand> markers = entry.getValue();

            if (markers.size() == 2) {
                playerGoalLines.put(ownerUuid, new GoalLine(markers.get(0), markers.get(1)));
                plugin.getLogger().info("Reconstructed goal line for player " + ownerUuid);
            } else if (markers.size() > 2) {
                plugin.getLogger().warning("Player " + ownerUuid + " has " + markers.size() +
                    " markers (expected 2). Creating goal line with first 2 markers.");
                playerGoalLines.put(ownerUuid, new GoalLine(markers.get(0), markers.get(1)));
            }
        }
    }

    // Recruitment methods
    public void startRecruit(Player organizer, int maxPlayers, boolean organizerParticipates) {
        if (activeRecruitSession != null && activeRecruitSession.isActive()) {
            organizer.sendMessage(MessageBuilder.error("すでに募集中です"));
            return;
        }

        if (activeRaceSession != null && activeRaceSession.isActive()) {
            organizer.sendMessage(MessageBuilder.error("レースが進行中です"));
            return;
        }

        if (hasGoalLine(organizer)) {
            organizer.sendMessage(MessageBuilder.error("ゴール地点を設定してください"));
            return;
        }

        if (maxPlayers < 2) {
            organizer.sendMessage(MessageBuilder.error("定員は2人以上にしてください"));
            return;
        }

        if (maxPlayers > 20) {
            organizer.sendMessage(MessageBuilder.error("定員は20人以下にしてください"));
            return;
        }

        String participationMsg =
            organizerParticipates ? "（参加）" : "（不参加）";
        organizer.sendMessage(MessageBuilder.success(
            "[BoatRace] 定員: " + maxPlayers + "人 " + participationMsg));

        activeRecruitSession = new BoatRaceRecruitSession(organizer, maxPlayers,
            organizerParticipates);
        activeRecruitSession.start();
    }

    public void joinRecruit(Player player) {
        if (activeRecruitSession == null || !activeRecruitSession.isActive()) {
            player.sendMessage(MessageBuilder.error("現在募集していません"));
            return;
        }

        activeRecruitSession.addPlayer(player);
    }

    public void cancelRecruit(Player player) {
        if (activeRecruitSession == null || !activeRecruitSession.isActive()) {
            player.sendMessage(MessageBuilder.error("募集していません"));
            return;
        }

        if (!activeRecruitSession.getOrganizer().equals(player)) {
            player.sendMessage(
                MessageBuilder.error("募集を開始したプレイヤーのみキャンセルできます"));
            return;
        }

        activeRecruitSession.cancel();
        activeRecruitSession = null;
    }

    public void startRace(Player organizer) {
        if (activeRecruitSession == null || !activeRecruitSession.isActive()) {
            organizer.sendMessage(MessageBuilder.error("募集を開始してください"));
            return;
        }

        if (!activeRecruitSession.getOrganizer().equals(organizer)) {
            organizer.sendMessage(
                MessageBuilder.error("募集を開始したプレイヤーのみレースを開始できます"));
            return;
        }

        if (activeRecruitSession.getParticipantCount() < 2) {
            organizer.sendMessage(MessageBuilder.error("参加者が2人以上必要です"));
            return;
        }

        GoalLine goalLine = playerGoalLines.get(organizer.getUniqueId());
        if (goalLine == null || !goalLine.isValid()) {
            organizer.sendMessage(MessageBuilder.error("ゴール地点が設定されていません"));
            return;
        }

        activeRecruitSession.stop();

        activeRaceSession = new BoatRaceSession(
            plugin,
            activeRecruitSession.getParticipants(),
            organizer,
            this::cleanupRaceSession
        );

        activeRecruitSession = null;
        activeRaceSession.start();
    }

    public void stopRace(Player player) {
        if (activeRaceSession == null || !activeRaceSession.isActive()) {
            player.sendMessage(MessageBuilder.error("レースが進行していません"));
            return;
        }

        // Allow both participants and the organizer to stop the race
        if (activeRaceSession.isParticipant(player) && !activeRaceSession.isOrganizer(player)) {
            player.sendMessage(MessageBuilder.error("レースに参加していないため、停止できません"));
            return;
        }

        activeRaceSession.stop();
    }

    private void cleanupRaceSession() {
        activeRaceSession = null;
    }

    // Marker management methods
    public void giveGoalMarkerEgg(Player player) {
        ItemStack egg = new ItemStack(Material.ARMOR_STAND);
        ItemMeta meta = egg.getItemMeta();
        meta.displayName(Component.text("§e§lゴールマーカー"));
        meta.lore(List.of(Component.text("§7右クリックでゴール地点を設置")));
        egg.setItemMeta(meta);
        player.getInventory().addItem(egg);
        player.sendMessage(MessageBuilder.success("ゴールマーカーを入手しました"));
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

    private ArmorStand createMarker(Location location, Player owner) {
        ArmorStand marker = (ArmorStand) location.getWorld()
            .spawnEntity(location, EntityType.ARMOR_STAND);
        marker.setVisible(false);
        marker.setGravity(false);
        marker.setInvulnerable(true);
        marker.customName(Component.text("§e§lゴール地点"));
        marker.setCustomNameVisible(true);
        marker.setMarker(true);
        marker.setGlowing(true);

        // Store owner UUID in persistent data
        org.bukkit.NamespacedKey ownerKey = new org.bukkit.NamespacedKey(plugin,
            "boatrace_goal_owner");
        marker.getPersistentDataContainer().set(ownerKey,
            org.bukkit.persistence.PersistentDataType.STRING,
            owner.getUniqueId().toString());

        return marker;
    }

    public boolean hasGoalLine(Player player) {
        GoalLine goalLine = playerGoalLines.get(player.getUniqueId());
        return goalLine == null || !goalLine.isValid();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item == null || item.getType() != Material.ARMOR_STAND) {
            return;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return;
        }

        Component displayName = meta.displayName();
        if (displayName == null || !displayName.equals(Component.text("§e§lゴールマーカー"))) {
            return;
        }

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        if (event.getClickedBlock() == null) {
            return;
        }

        event.setCancelled(true);

        UUID playerId = player.getUniqueId();
        List<ArmorStand> markers = goalLineMarkers.get(playerId);

        // Check if player already has 2 markers
        if (markers != null && markers.size() >= 2) {
            player.sendMessage(MessageBuilder.warning("既に2つのゴールマーカーが設置されています"));
            player.sendMessage(MessageBuilder.warning("/boatrace cleargoal で削除してください"));
            return;
        }

        Location location = event.getClickedBlock().getLocation().add(0.5, 1, 0.5);
        ArmorStand marker = createMarker(location, player);
        goalLineMarkers.computeIfAbsent(playerId, k -> new ArrayList<>()).add(marker);

        int markerCount = goalLineMarkers.get(playerId).size();
        if (markerCount == 1) {
            player.sendMessage(
                MessageBuilder.success("ゴールマーカー1つ目を設置しました (残り1つ)"));
        } else if (markerCount == 2) {
            ArmorStand marker1 = goalLineMarkers.get(playerId).get(0);
            ArmorStand marker2 = goalLineMarkers.get(playerId).get(1);
            playerGoalLines.put(playerId, new GoalLine(marker1, marker2));
            player.sendMessage(MessageBuilder.success("ゴールマーカー2つ目を設置しました"));
            player.sendMessage(MessageBuilder.success("ゴールラインが完成しました！"));

            // Calculate and show distance between markers
            double distance = marker1.getLocation().distance(marker2.getLocation());
            player.sendMessage(MessageBuilder.info(
                String.format("マーカー間の距離: %.2fブロック", distance)));
        }
    }

    @EventHandler
    public void onVehicleMove(VehicleMoveEvent event) {
        if (!(event.getVehicle() instanceof Boat boat)) {
            return;
        }

        if (boat.getPassengers().isEmpty()) {
            return;
        }

        Entity passenger = boat.getPassengers().getFirst();
        if (!(passenger instanceof Player player)) {
            return;
        }

        if (activeRaceSession == null || !activeRaceSession.isActive()) {
            return;
        }

        if (!activeRaceSession.isRaceStarted() || !activeRaceSession.isParticipant(player)) {
            return;
        }

        Location boatLocation = boat.getLocation();
        Location fromLocation = event.getFrom();

    }

    private boolean isInGoalHitbox(Location boatLoc, Location markerLoc) {
        // Create a 1x2x1 hitbox centered at the marker
        double minX = markerLoc.getX() - 0.5;
        double maxX = markerLoc.getX() + 0.5;
        double minY = markerLoc.getY() - 1.0;
        double maxY = markerLoc.getY() + 1.0;
        double minZ = markerLoc.getZ() - 0.5;
        double maxZ = markerLoc.getZ() + 0.5;

        return boatLoc.getX() >= minX && boatLoc.getX() <= maxX &&
            boatLoc.getY() >= minY && boatLoc.getY() <= maxY &&
            boatLoc.getZ() >= minZ && boatLoc.getZ() <= maxZ;
    }

    private List<ArmorStand> getAllGoalMarkers() {
        List<ArmorStand> allMarkers = new ArrayList<>();
        for (List<ArmorStand> markers : goalLineMarkers.values()) {
            allMarkers.addAll(markers);
        }
        return allMarkers;
    }

    public SummerFestival getPlugin() {
        return plugin;
    }
}
