package com.me1q.summerFestival;

import com.me1q.summerFestival.games.boatrace.BoatRaceCommand;
import com.me1q.summerFestival.games.shooting.ShootingGameCommand;
import com.me1q.summerFestival.games.tag.TagCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class SummerFestival extends JavaPlugin {

    @Override
    public void onEnable() {
        registerShootingGame();
        registerTagGame();
        registerBoatRaceGame();
        getLogger().info("Enabled SummerFestival Plugin.");
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabled SummerFestival Plugin.");
    }

    private void registerShootingGame() {
        ShootingGameCommand shootingGameCommand = new ShootingGameCommand(this);
        getCommand("shootinggame").setExecutor(shootingGameCommand);
        getCommand("shootinggame").setTabCompleter(shootingGameCommand);
        getServer().getPluginManager().registerEvents(shootingGameCommand.getGameManager(), this);
    }

    private void registerTagGame() {
        TagCommand tagCommand = new TagCommand(this);
        getCommand("tag").setExecutor(tagCommand);
        getCommand("tag").setTabCompleter(tagCommand);
        getServer().getPluginManager().registerEvents(tagCommand.getGameManager(), this);
    }

    private void registerBoatRaceGame() {
        BoatRaceCommand boatRaceCommand = new BoatRaceCommand(this);
        getCommand("boatrace").setExecutor(boatRaceCommand);
        getCommand("boatrace").setTabCompleter(boatRaceCommand);
        getServer().getPluginManager().registerEvents(boatRaceCommand.getGameManager(), this);

        // Load existing goal markers from the world
        boatRaceCommand.getGameManager().loadExistingGoalMarkers();
    }
}