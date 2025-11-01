package com.me1q.summerFestival;

import com.me1q.summerFestival.game.boatrace.BoatRaceCommand;
import com.me1q.summerFestival.game.shooting.ShootingCommand;
import com.me1q.summerFestival.game.tag.TagCommand;
import com.me1q.summerFestival.game.tag.TagItemRegistrar;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class SummerFestival extends JavaPlugin {

    public static SummerFestival getInstance() {
        return JavaPlugin.getPlugin(SummerFestival.class);
    }

    @Override
    public void onEnable() {
        registerShootingGame();
        registerTagGame();
        registerBoatRaceGame();
        TagItemRegistrar.registerItems();
        getLogger().info("Enabled SummerFestival Plugin.");
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabled SummerFestival Plugin.");
    }

    private void registerShootingGame() {
        ShootingCommand shootingCommand = new ShootingCommand(this);
        PluginCommand command = getCommand("shooting");
        if (command != null) {
            command.setExecutor(shootingCommand);
            command.setTabCompleter(shootingCommand);
        }
        getServer().getPluginManager().registerEvents(shootingCommand.getGameManager(), this);
    }

    private void registerTagGame() {
        TagCommand tagCommand = new TagCommand(this);
        PluginCommand command = getCommand("tag");
        if (command != null) {
            command.setExecutor(tagCommand);
            command.setTabCompleter(tagCommand);
        }
        getServer().getPluginManager().registerEvents(tagCommand.getGameManager(), this);
    }

    private void registerBoatRaceGame() {
        BoatRaceCommand boatRaceCommand = new BoatRaceCommand(this);
        PluginCommand command = getCommand("boatrace");
        if (command != null) {
            command.setExecutor(boatRaceCommand);
            command.setTabCompleter(boatRaceCommand);
        }
        getServer().getPluginManager().registerEvents(boatRaceCommand.getGameManager(), this);

        // Load existing goal markers from the world
        boatRaceCommand.getGameManager().loadExistingGoalMarkers();
    }
}