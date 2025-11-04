package com.me1q.summerFestival.game.shooting.button;

import com.me1q.summerFestival.SummerFestival;
import com.me1q.summerFestival.core.message.MessageBuilder;
import com.me1q.summerFestival.game.shooting.ShootingManager;
import com.me1q.summerFestival.game.shooting.constants.ShootingMessage;
import com.me1q.summerFestival.game.shooting.spawner.SpawnArea;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class ShootingButtonListener implements Listener {

    private final ShootingManager shootingManager;
    private final ButtonDataManager buttonDataManager;

    public ShootingButtonListener(SummerFestival plugin, ShootingManager shootingManager,
        ButtonDataManager buttonDataManager) {
        this.shootingManager = shootingManager;
        this.buttonDataManager = buttonDataManager;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) {
            return;
        }

        if (!isButton(clickedBlock.getType())) {
            return;
        }

        Location buttonLocation = clickedBlock.getLocation();
        ButtonConfig buttonConfig = buttonDataManager.getButtonConfig(buttonLocation);

        if (buttonConfig == null) {
            return;
        }

        Player player = event.getPlayer();
        event.setCancelled(true);

        if (shootingManager.isPlayerInGame(player)) {
            player.sendMessage(MessageBuilder.error(ShootingMessage.ALREADY_IN_GAME.text()));
            player.sendMessage(MessageBuilder.warning(ShootingMessage.END_GAME_COMMAND.text()));
            return;
        }

        SpawnArea spawnArea = buttonConfig.getSpawnArea();
        shootingManager.startGame(
            player,
            spawnArea.getMinX(),
            spawnArea.getMinY(),
            spawnArea.getMinZ(),
            spawnArea.getMaxX(),
            spawnArea.getMaxY(),
            spawnArea.getMaxZ()
        );
    }

    private boolean isButton(Material material) {
        return material == Material.OAK_BUTTON
            || material == Material.SPRUCE_BUTTON
            || material == Material.BIRCH_BUTTON
            || material == Material.JUNGLE_BUTTON
            || material == Material.ACACIA_BUTTON
            || material == Material.DARK_OAK_BUTTON
            || material == Material.MANGROVE_BUTTON
            || material == Material.CHERRY_BUTTON
            || material == Material.BAMBOO_BUTTON
            || material == Material.CRIMSON_BUTTON
            || material == Material.WARPED_BUTTON
            || material == Material.STONE_BUTTON
            || material == Material.POLISHED_BLACKSTONE_BUTTON;
    }
}

