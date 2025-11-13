package com.me1q.summerFestival.game.tag.item.listener;

import com.me1q.summerFestival.SummerFestival;
import com.me1q.summerFestival.core.message.MessageBuilder;
import com.me1q.summerFestival.game.boatrace.returnpoint.ReturnPointManager;
import com.me1q.summerFestival.game.tag.session.TagSession;
import java.util.Comparator;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class TaggerTeleporterListener implements Listener {

    private static final double DETECTION_RANGE = 7.0;

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR
            && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        ItemStack item = event.getItem();
        if (item == null || item.getType() != Material.COCOA_BEANS) {
            return;
        }

        Player player = event.getPlayer();
        event.setCancelled(true);

        TagSession session = getActiveSession();
        if (session == null || !session.isActive()) {
            return;
        }

        ReturnPointManager returnPointManager = getReturnPointManager();
        if (returnPointManager == null) {
            return;
        }

        Location returnPoint = returnPointManager.getReturnPoint();
        if (returnPoint == null) {
            player.sendMessage(MessageBuilder.error("リターンポイントが設定されていません"));
            return;
        }

        teleportNearbyTagger(player, session, returnPoint);
    }

    private TagSession getActiveSession() {
        SummerFestival plugin = SummerFestival.getInstance();
        if (plugin.getTagManager() == null) {
            return null;
        }
        return plugin.getTagManager().getActiveSession();
    }

    private ReturnPointManager getReturnPointManager() {
        SummerFestival plugin = SummerFestival.getInstance();
        if (plugin.getTagManager() == null) {
            return null;
        }
        return plugin.getTagManager().getReturnPointManager();
    }

    private void teleportNearbyTagger(Player player, TagSession session, Location returnPoint) {
        List<Player> nearbyTaggers = session.getAllPlayers().stream()
            .filter(session::isTagger)
            .filter(tagger -> !tagger.equals(player))
            .filter(tagger -> tagger.getLocation().distance(player.getLocation())
                <= DETECTION_RANGE)
            .sorted(Comparator.comparingDouble(
                p -> p.getLocation().distanceSquared(player.getLocation())))
            .limit(1)
            .toList();

        if (nearbyTaggers.isEmpty()) {
            player.sendMessage(MessageBuilder.error("近くに鬼がいません"));
            return;
        }

        Player tagger = nearbyTaggers.getFirst();
        tagger.teleport(returnPoint);

        tagger.sendMessage(Component.text("テレポートされた!").color(NamedTextColor.RED));
        player.sendMessage(MessageBuilder.success(
            tagger.getName() + " をグラウンドにテレポートしました"));

        ItemStack mainhandItem = player.getInventory().getItemInMainHand();
        mainhandItem.setAmount(mainhandItem.getAmount() - 1);
    }
}

