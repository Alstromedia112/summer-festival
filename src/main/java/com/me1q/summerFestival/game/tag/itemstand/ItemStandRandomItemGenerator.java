package com.me1q.summerFestival.game.tag.itemstand;

import com.me1q.summerFestival.game.tag.item.Decoy;
import com.me1q.summerFestival.game.tag.item.InvisiblePotion;
import com.me1q.summerFestival.game.tag.item.SlownessPotion;
import com.me1q.summerFestival.game.tag.item.SmokeLauncher;
import com.me1q.summerFestival.game.tag.item.SpeedPotion;
import java.util.List;
import java.util.Random;
import org.bukkit.inventory.ItemStack;

public final class ItemStandRandomItemGenerator {

    private static final Random RANDOM = new Random();

    private ItemStandRandomItemGenerator() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static ItemStack getRandomItem() {
        List<ItemStack> itemPool = List.of(
            InvisiblePotion.createItem(),
            SpeedPotion.createItem(),
            SlownessPotion.createItem(),
            Decoy.createItem(),
            SmokeLauncher.createItem()
        );

        if (itemPool.isEmpty()) {
            throw new IllegalStateException("Item pool is empty");
        }
        return itemPool.get(RANDOM.nextInt(itemPool.size()));
    }
}

