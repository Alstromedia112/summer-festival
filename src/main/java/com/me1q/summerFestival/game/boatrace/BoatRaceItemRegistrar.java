package com.me1q.summerFestival.game.boatrace;

import com.me1q.summerFestival.SummerFestival;
import com.me1q.summerFestival.game.boatrace.item.BoatRaceItemBase;
import com.me1q.summerFestival.game.boatrace.item.SpeedBoost;
import java.util.List;
import org.bukkit.Bukkit;

public final class BoatRaceItemRegistrar {

    private BoatRaceItemRegistrar() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static void registerItems() {
        List<BoatRaceItemBase> items = List.of(
            new SpeedBoost()
        );

        items.forEach(item -> {
            Bukkit.getServer().getPluginManager().registerEvents(item.getItemManager(),
                SummerFestival.getInstance());
        });
    }
}

