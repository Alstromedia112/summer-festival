package com.me1q.summerFestival.core.config;

import java.io.File;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class ConfigManager {

    private final JavaPlugin plugin;
    private final File configFile;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        File dataFolder = plugin.getDataFolder();
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        this.configFile = new File(dataFolder, "config.yml");

        if (!configFile.exists()) {
            plugin.saveResource("config.yml", false);
        }
    }

    public int getBoatRaceGoalDetectionDelaySeconds() {
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        return config.getInt("boatrace.goal-detection-delay-seconds", 30);
    }

    public int getBoatRaceLaps() {
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        return config.getInt("boatrace.laps", 2);
    }
}

