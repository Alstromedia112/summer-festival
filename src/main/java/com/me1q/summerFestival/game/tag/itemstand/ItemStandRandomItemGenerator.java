package com.me1q.summerFestival.game.tag.itemstand;

import com.me1q.summerFestival.game.tag.item.DarknessPotion;
import com.me1q.summerFestival.game.tag.item.Decoy;
import com.me1q.summerFestival.game.tag.item.InvisiblePotion;
import com.me1q.summerFestival.game.tag.item.JumpPotion;
import com.me1q.summerFestival.game.tag.item.RedHelmet;
import com.me1q.summerFestival.game.tag.item.SlownessPotion;
import com.me1q.summerFestival.game.tag.item.SmokeLauncher;
import com.me1q.summerFestival.game.tag.item.SpeedPotion;
import com.me1q.summerFestival.game.tag.item.TaggerDetector;
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
            JumpPotion.createItem(),
            SlownessPotion.createItem(),
            DarknessPotion.createItem(),
            Decoy.createItem(),
            SmokeLauncher.createItem(),
            TaggerDetector.createItem(),
            RedHelmet.createItem()
        );

        return itemPool.get(RANDOM.nextInt(itemPool.size()));
    }
}

