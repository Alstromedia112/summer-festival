package com.me1q.summerFestival;

import com.me1q.summerFestival.commands.boatrace.BoatRaceCommand;
import com.me1q.summerFestival.commands.currency.CurrencyCommand;
import com.me1q.summerFestival.commands.shooting.ShootingCommand;
import com.me1q.summerFestival.commands.shop.ShopCommand;
import com.me1q.summerFestival.commands.tag.TagCommand;
import com.me1q.summerFestival.core.config.ConfigManager;
import com.me1q.summerFestival.currency.CurrencyManager;
import com.me1q.summerFestival.game.boatrace.BoatRaceItemRegistrar;
import com.me1q.summerFestival.game.shooting.button.ShootingButtonListener;
import com.me1q.summerFestival.game.tag.TagItemRegistrar;
import com.me1q.summerFestival.shop.ShopAdminGUI;
import com.me1q.summerFestival.shop.ShopGUI;
import com.me1q.summerFestival.shop.ShopItemManager;
import com.me1q.summerFestival.shop.ShopListener;
import com.me1q.summerFestival.shop.ShopVillager;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class SummerFestival extends JavaPlugin {

    private ConfigManager configManager;
    private CurrencyManager currencyManager;

    public static SummerFestival getInstance() {
        return JavaPlugin.getPlugin(SummerFestival.class);
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public CurrencyManager getCurrencyManager() {
        return currencyManager;
    }

    @Override
    public void onEnable() {
        configManager = new ConfigManager(this);
        currencyManager = new CurrencyManager();
        getServer().getPluginManager().registerEvents(currencyManager, this);
        registerCurrencyCommand();
        registerShop();
        registerShootingGame();
        registerTagGame();
        registerBoatRaceGame();
        TagItemRegistrar.registerItems();
        BoatRaceItemRegistrar.registerItems();
        getLogger().info("Enabled SummerFestival Plugin.");
    }

    @Override
    public void onDisable() {
        if (currencyManager != null) {
            currencyManager.shutdown();
        }
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

        ShootingButtonListener buttonListener = new ShootingButtonListener(
            this,
            shootingCommand.getGameManager(),
            shootingCommand.getButtonDataManager()
        );
        getServer().getPluginManager().registerEvents(buttonListener, this);
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
    }

    private void registerCurrencyCommand() {
        CurrencyCommand currencyCommand = new CurrencyCommand(this);
        PluginCommand command = getCommand("currency");
        if (command != null) {
            command.setExecutor(currencyCommand);
            command.setTabCompleter(currencyCommand);
        }
    }

    private void registerShop() {
        ShopItemManager itemManager = new ShopItemManager(this);
        ShopGUI shopGUI = new ShopGUI(this, itemManager);
        ShopAdminGUI adminGUI = new ShopAdminGUI(itemManager);
        ShopVillager shopVillager = new ShopVillager(this, shopGUI);
        ShopListener shopListener = new ShopListener(shopGUI, adminGUI, itemManager);

        getServer().getPluginManager().registerEvents(shopVillager, this);
        getServer().getPluginManager().registerEvents(shopListener, this);

        ShopCommand shopCommand = new ShopCommand(shopVillager, shopGUI, adminGUI, itemManager);
        PluginCommand command = getCommand("shop");
        if (command != null) {
            command.setExecutor(shopCommand);
            command.setTabCompleter(shopCommand);
        }
    }
}