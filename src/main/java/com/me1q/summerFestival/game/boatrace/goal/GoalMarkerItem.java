package com.me1q.summerFestival.game.boatrace.goal;

import com.me1q.summerFestival.game.boatrace.constants.Goal;
import java.util.List;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class GoalMarkerItem {

    private GoalMarkerItem() {
    }

    public static ItemStack create() {
        ItemStack item = new ItemStack(Material.ARMOR_STAND);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text(Goal.MARKER_NAME.text()));
        meta.lore(List.of(Component.text(Goal.MARKER_LORE.text())));
        item.setItemMeta(meta);
        return item;
    }

    public static boolean isGoalMarker(ItemStack item) {
        if (item == null || item.getType() != Material.ARMOR_STAND) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return false;
        }

        Component displayName = meta.displayName();
        return displayName != null &&
            displayName.equals(Component.text(Goal.MARKER_NAME.text()));
    }
}

