package com.me1q.summerFestival.game.shooting.target;

import java.util.concurrent.ThreadLocalRandom;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;

public enum TargetType {
    ONE(1, Material.WHITE_WOOL, "1pt", NamedTextColor.WHITE, 0.5),
    THREE(3, Material.YELLOW_WOOL, "3pt", NamedTextColor.YELLOW, 0.25),
    FIVE(5, Material.RED_WOOL, "5pt", NamedTextColor.RED, 0.15),
    BONUS(10, Material.GOLD_BLOCK, "Bonus 10pt", NamedTextColor.GOLD, 0.1);

    private final int points;
    private final Material material;
    private final String displayName;
    private final NamedTextColor color;
    private final double spawnProbability;

    TargetType(int points, Material material, String displayName, NamedTextColor color,
        double spawnProbability) {
        this.points = points;
        this.material = material;
        this.displayName = displayName;
        this.color = color;
        this.spawnProbability = spawnProbability;
    }

    public int getPoints() {
        return points;
    }

    public Material getMaterial() {
        return material;
    }

    public String getDisplayName() {
        return displayName;
    }

    public NamedTextColor getColor() {
        return color;
    }

    public static TargetType getRandomType() {
        double random = ThreadLocalRandom.current().nextDouble();
        double cumulative = 0.0;

        for (TargetType type : values()) {
            cumulative += type.spawnProbability;
            if (random < cumulative) {
                return type;
            }
        }
        return ONE;
    }
}