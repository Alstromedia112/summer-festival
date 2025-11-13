package com.me1q.summerFestival.teleportbook;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class TeleportLocationManager {

    private final JavaPlugin plugin;
    private final File dataFile;
    private FileConfiguration config;
    private final List<TeleportLocation> locations;

    public TeleportLocationManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.locations = new ArrayList<>();

        File dataFolder = plugin.getDataFolder();
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        this.dataFile = new File(dataFolder, "teleport_locations.yml");
        loadLocations();
    }

    private void loadLocations() {
        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger()
                    .severe("Failed to create teleport_locations.yml: " + e.getMessage());
            }
        }

        config = YamlConfiguration.loadConfiguration(dataFile);
        locations.clear();

        ConfigurationSection locationsSection = config.getConfigurationSection("locations");
        if (locationsSection == null) {
            return;
        }

        for (String key : locationsSection.getKeys(false)) {
            ConfigurationSection locationSection = locationsSection.getConfigurationSection(key);
            if (locationSection == null) {
                continue;
            }

            String worldName = locationSection.getString("world");
            double x = locationSection.getDouble("x");
            double y = locationSection.getDouble("y");
            double z = locationSection.getDouble("z");
            float yaw = (float) locationSection.getDouble("yaw");
            float pitch = (float) locationSection.getDouble("pitch");

            World world = Bukkit.getWorld(worldName);
            if (world == null) {
                plugin.getLogger().warning("World not found for location: " + key);
                continue;
            }

            Location location = new Location(world, x, y, z, yaw, pitch);
            locations.add(new TeleportLocation(key, location));
        }
    }

    private void saveLocations() {
        config.set("locations", null);

        for (TeleportLocation teleportLocation : locations) {
            String path = "locations." + teleportLocation.getName();
            Location loc = teleportLocation.getLocation();

            config.set(path + ".world", loc.getWorld().getName());
            config.set(path + ".x", loc.getX());
            config.set(path + ".y", loc.getY());
            config.set(path + ".z", loc.getZ());
            config.set(path + ".yaw", loc.getYaw());
            config.set(path + ".pitch", loc.getPitch());
        }

        try {
            config.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save teleport_locations.yml: " + e.getMessage());
        }
    }

    public boolean addLocation(String name, Location location) {
        if (getLocation(name) != null) {
            return false;
        }

        locations.add(new TeleportLocation(name, location));
        saveLocations();
        return true;
    }

    public boolean removeLocation(String name) {
        TeleportLocation location = getLocation(name);
        if (location == null) {
            return false;
        }

        locations.remove(location);
        saveLocations();
        return true;
    }

    public TeleportLocation getLocation(String name) {
        for (TeleportLocation location : locations) {
            if (location.getName().equalsIgnoreCase(name)) {
                return location;
            }
        }
        return null;
    }

    public List<TeleportLocation> getAllLocations() {
        return new ArrayList<>(locations);
    }

    public void reload() {
        loadLocations();
    }
}


