package com.me1q.summerFestival.game.boatrace;

import com.me1q.summerFestival.SummerFestival;
import com.me1q.summerFestival.game.boatrace.item.SpeedBoost;
import org.bukkit.Bukkit;

public class BoatRaceItemRegister {

    public static void registerItems() {
        Bukkit.getServer().getPluginManager().registerEvents(new SpeedBoost().getItemManager(),
            SummerFestival.getInstance());
    }
}
