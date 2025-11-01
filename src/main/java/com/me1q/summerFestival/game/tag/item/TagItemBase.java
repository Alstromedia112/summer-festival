package com.me1q.summerFestival.game.tag.item;

import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public abstract class TagItemBase {

    public abstract ItemStack createItem();

    public abstract Listener getItemManager();
}
