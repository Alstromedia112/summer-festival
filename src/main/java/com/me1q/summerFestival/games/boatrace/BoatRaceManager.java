package com.me1q.summerFestival.games.boatrace;

import com.me1q.summerFestival.SummerFestival;
import com.me1q.summerFestival.core.message.MessageBuilder;
import com.me1q.summerFestival.games.boatrace.session.BoatRaceRecruitSession;
import com.me1q.summerFestival.games.boatrace.session.BoatRaceSession;
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

    private final SummerFestival plugin;
    private final Map<UUID, List<ArmorStand>> goalLineMarkers;

    // Recruitment and multi-player race sessions
    private BoatRaceRecruitSession activeRecruitSession;
    private BoatRaceSession activeRaceSession;

    public BoatRaceManager(SummerFestival plugin) {
        this.plugin = plugin;
        this.goalLineMarkers = new HashMap<>();
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

                String name = net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText()
                    .serialize(armorStand.customName());

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

        List<ArmorStand> goalMarkers = goalLineMarkers.get(organizer.getUniqueId());
        if (goalMarkers == null || goalMarkers.isEmpty()) {
            organizer.sendMessage(MessageBuilder.error("ゴール地点が設定されていません"));
            return;
        }

        activeRecruitSession.stop();

        activeRaceSession = new BoatRaceSession(
            plugin,
            activeRecruitSession.getParticipants(),
            goalMarkers,
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
        if (!activeRaceSession.isParticipant(player) && !activeRaceSession.isOrganizer(player)) {
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
    }

    private ArmorStand createMarker(Location location, String name, Player owner) {
        ArmorStand marker = (ArmorStand) location.getWorld()
            .spawnEntity(location, EntityType.ARMOR_STAND);
        marker.setVisible(false);
        marker.setGravity(false);
        marker.setInvulnerable(true);
        marker.customName(Component.text(name));
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
        List<ArmorStand> markers = goalLineMarkers.get(player.getUniqueId());
        return markers == null || markers.isEmpty() || markers.stream().allMatch(Entity::isDead);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item == null || item.getType() != Material.ARMOR_STAND) {
            return;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasDisplayName() || !meta.getDisplayName()
            .equals("§e§lゴールマーカー")) {
            return;
        }

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        event.setCancelled(true);

        Location location = event.getClickedBlock().getLocation().add(0.5, 1, 0.5);
        ArmorStand marker = createMarker(location, "§e§lゴール地点", player);
        goalLineMarkers.computeIfAbsent(player.getUniqueId(), k -> new ArrayList<>()).add(marker);

        player.sendMessage(MessageBuilder.success("ゴール地点を設置しました"));
    }

    @EventHandler
    public void onVehicleMove(VehicleMoveEvent event) {
        if (!(event.getVehicle() instanceof Boat boat)) {
            return;
        }

        if (boat.getPassengers().isEmpty()) {
            return;
        }

        Entity passenger = boat.getPassengers().get(0);
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

        // Check collision with goal markers using 1x2x1 hitbox
        for (ArmorStand goalMarker : getAllGoalMarkers()) {
            if (goalMarker != null && !goalMarker.isDead()) {
                if (isInGoalHitbox(boatLocation, goalMarker.getLocation())) {
                    activeRaceSession.playerReachGoal(player);
                    return;
                }
            }
        }
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
