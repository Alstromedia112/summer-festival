package com.me1q.summerFestival.game.tag.item.listener;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.entity.WindCharge;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;

public class SmokeLauncherListener implements Listener {

    @EventHandler
    public void onWindChargeLand(ProjectileHitEvent event) {

        if (!(event.getEntity() instanceof WindCharge windCharge)) {
            return;
        }

        if (!(windCharge.getShooter() instanceof Player)) {
            return;
        }

        Location hitLocation = windCharge.getLocation();
        createSmokeEffect(hitLocation);
    }

    private void createSmokeEffect(Location center) {
        for (int i = 0; i < 500; i++) {
            double offsetX = (Math.random() - 0.5) * 10;
            double offsetY = Math.random() * 3;
            double offsetZ = (Math.random() - 0.5) * 10;

            Location particleLocation = center.clone().add(offsetX, offsetY, offsetZ);
            center.getWorld().spawnParticle(
                Particle.CAMPFIRE_COSY_SMOKE,
                particleLocation,
                3,
                0, 0, 0,
                0.05
            );
        }
    }
}
