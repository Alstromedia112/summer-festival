package com.me1q.summerFestival.game.boatrace.itemstand;

import com.me1q.summerFestival.game.boatrace.item.SpeedBoost;
import java.util.List;
import java.util.Random;
import org.bukkit.inventory.ItemStack;

public final class ItemStandRandomItemGenerator {

    private static final Random RANDOM = new Random();
    private static final List<ItemStack> ITEM_POOL = List.of(
        SpeedBoost.createItem()
    );

    private ItemStandRandomItemGenerator() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static ItemStack getRandomItem() {
        if (ITEM_POOL.isEmpty()) {
            throw new IllegalStateException("Item pool is empty");
        }
        return ITEM_POOL.get(RANDOM.nextInt(ITEM_POOL.size())).clone();
    }
}

