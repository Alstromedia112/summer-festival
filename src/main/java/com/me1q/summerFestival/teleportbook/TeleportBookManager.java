package com.me1q.summerFestival.teleportbook;

import com.me1q.summerFestival.teleportbook.commands.TeleportBookCommand;
import com.me1q.summerFestival.teleportbook.listener.TeleportBookListener;
import com.me1q.summerFestival.teleportbook.listener.TeleportGUIListener;
import org.bukkit.plugin.java.JavaPlugin;

public class TeleportBookManager {

    private final JavaPlugin plugin;
    private final TeleportLocationManager locationManager;
    private final TeleportBookGUI gui;
    private final TeleportBookCommand command;

    public TeleportBookManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.locationManager = new TeleportLocationManager(plugin);
        this.gui = new TeleportBookGUI(locationManager);
        this.command = new TeleportBookCommand(locationManager, gui);

        registerListeners();
    }

    private void registerListeners() {
        plugin.getServer().getPluginManager().registerEvents(
            new TeleportBookListener(gui), plugin);
        plugin.getServer().getPluginManager().registerEvents(
            new TeleportGUIListener(locationManager, gui), plugin);
    }

    public TeleportBookCommand getCommand() {
        return command;
    }

    public TeleportLocationManager getLocationManager() {
        return locationManager;
    }

    public TeleportBookGUI getGui() {
        return gui;
    }
}

