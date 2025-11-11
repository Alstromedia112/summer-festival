package com.me1q.summerFestival.game.tag;

import com.me1q.summerFestival.SummerFestival;
import com.me1q.summerFestival.game.tag.item.Decoy;
import com.me1q.summerFestival.game.tag.item.SmokeLauncher;
import com.me1q.summerFestival.game.tag.item.TagItemBase;
import com.me1q.summerFestival.game.tag.item.TaggerDetector;
import java.util.List;
import org.bukkit.Bukkit;

public final class TagItemRegistrar {

    private TagItemRegistrar() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static void registerItems() {
        List<TagItemBase> items = List.of(
            new SmokeLauncher(),
            new Decoy(),
            new TaggerDetector()
        );

        items.forEach(item -> {
            Bukkit.getServer().getPluginManager().registerEvents(item.getItemManager(),
                SummerFestival.getInstance());
        });
    }
}
