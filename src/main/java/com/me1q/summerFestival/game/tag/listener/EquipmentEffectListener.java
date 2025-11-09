package com.me1q.summerFestival.game.tag.listener;

import com.me1q.summerFestival.game.tag.TagManager;
import com.me1q.summerFestival.game.tag.player.Equipment;
import com.me1q.summerFestival.game.tag.session.TagSession;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent.Action;
import org.bukkit.potion.PotionEffectType;

public class EquipmentEffectListener implements Listener {

    private final TagManager tagManager;

    public EquipmentEffectListener(TagManager tagManager) {
        this.tagManager = tagManager;
    }

    @EventHandler
    public void onPotionEffect(EntityPotionEffectEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        TagSession session = tagManager.getActiveSession();
        if (session == null || !session.isActive()) {
            return;
        }

        if (event.getModifiedType() != PotionEffectType.INVISIBILITY) {
            return;
        }

        if (event.getAction() == Action.ADDED) {
            Equipment.saveAndRemoveHelmet(player);
        } else if (event.getAction() == Action.REMOVED || event.getAction() == Action.CLEARED) {
            Equipment.restoreHelmet(player);
        }
    }
}

