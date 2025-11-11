package com.me1q.summerFestival.game.tag.item.listener;

import com.me1q.summerFestival.SummerFestival;
import com.me1q.summerFestival.game.tag.session.TagSession;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scheduler.BukkitTask;

public class RedHelmetListener implements Listener {

    private static final int DURATION_TICKS = 20 * 10;
    private static final Map<UUID, ItemStack> originalHelmets = new HashMap<>();
    private static final Map<UUID, BukkitTask> activeTasks = new HashMap<>();

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR
            && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        ItemStack item = event.getItem();
        if (item == null || item.getType() != Material.LEATHER_HELMET) {
            return;
        }

        Player player = event.getPlayer();
        event.setCancelled(true);

        TagSession session = getActiveSession();
        if (session == null || !session.isActive()) {
            return;
        }

        if (session.isTagger(player)) {
            return;
        }

        if (activeTasks.containsKey(player.getUniqueId())) {
            return;
        }

        applyRedHelmet(player);
    }

    private TagSession getActiveSession() {
        SummerFestival plugin = SummerFestival.getInstance();
        if (plugin.getTagManager() == null) {
            return null;
        }
        return plugin.getTagManager().getActiveSession();
    }

    private void applyRedHelmet(Player player) {
        ItemStack currentHelmet = player.getInventory().getHelmet();
        if (currentHelmet != null && currentHelmet.getType() != Material.AIR) {
            originalHelmets.put(player.getUniqueId(), currentHelmet.clone());
        }

        ItemStack redHelmet = createRedHelmet();
        player.getInventory().setHelmet(redHelmet);

        ItemStack mainhandItem = player.getInventory().getItemInMainHand();
        mainhandItem.setAmount(mainhandItem.getAmount() - 1);

        BukkitTask task = Bukkit.getScheduler().runTaskLater(
            SummerFestival.getInstance(),
            () -> restoreOriginalHelmet(player),
            DURATION_TICKS
        );
        activeTasks.put(player.getUniqueId(), task);
    }

    private ItemStack createRedHelmet() {
        ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
        LeatherArmorMeta meta = (LeatherArmorMeta) helmet.getItemMeta();

        meta.setColor(Color.RED);
        meta.displayName(
            Component.text("鬼").color(net.kyori.adventure.text.format.NamedTextColor.RED));
        meta.addEnchant(Enchantment.BINDING_CURSE, 1, true);

        helmet.setItemMeta(meta);
        return helmet;
    }

    private void restoreOriginalHelmet(Player player) {
        UUID playerId = player.getUniqueId();

        if (originalHelmets.containsKey(playerId)) {
            player.getInventory().setHelmet(originalHelmets.get(playerId));
            originalHelmets.remove(playerId);
        }

        activeTasks.remove(playerId);
    }

    public static void cleanup(Player player) {
        UUID playerId = player.getUniqueId();

        if (activeTasks.containsKey(playerId)) {
            activeTasks.get(playerId).cancel();
            activeTasks.remove(playerId);
        }

        if (originalHelmets.containsKey(playerId)) {
            player.getInventory().setHelmet(originalHelmets.get(playerId));
            originalHelmets.remove(playerId);
        }
    }

    public static void cleanupAll() {
        for (BukkitTask task : activeTasks.values()) {
            task.cancel();
        }
        activeTasks.clear();
        originalHelmets.clear();
    }
}

