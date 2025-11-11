package com.me1q.summerFestival.game.tag.item.listener;

import com.me1q.summerFestival.SummerFestival;
import com.me1q.summerFestival.game.tag.session.TagSession;
import java.util.Comparator;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class TaggerDetectorListener implements Listener {

    private static final int DETECTION_COUNT = 3;
    private static final int GLOW_DURATION_TICKS = 20 * 10;

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR
            && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        ItemStack item = event.getItem();
        if (item == null || item.getType() != Material.ENDER_EYE) {
            return;
        }

        Player player = event.getPlayer();
        event.setCancelled(true);

        TagSession session = getActiveSession();
        if (session == null || !session.isActive()) {
            return;
        }

        applyGlowToNearbyTaggers(player, session);
    }

    private TagSession getActiveSession() {
        SummerFestival plugin = SummerFestival.getInstance();
        if (plugin.getTagManager() == null) {
            return null;
        }
        return plugin.getTagManager().getActiveSession();
    }

    private void applyGlowToNearbyTaggers(Player player, TagSession session) {
        List<Player> nearbyTaggers = session.getAllPlayers().stream()
            .filter(session::isTagger)
            .filter(tagger -> !tagger.equals(player))
            .sorted(Comparator.comparingDouble(
                p -> p.getLocation().distanceSquared(player.getLocation())))
            .limit(DETECTION_COUNT)
            .toList();

        if (nearbyTaggers.isEmpty()) {
            return;
        }

        for (Player tagger : nearbyTaggers) {
            tagger.addPotionEffect(
                new PotionEffect(PotionEffectType.GLOWING, GLOW_DURATION_TICKS, 0, false, false,
                    false)
            );
        }

        ItemStack mainhandItem = player.getInventory().getItemInMainHand();

        mainhandItem.setAmount(mainhandItem.getAmount() - 1);
    }
}

