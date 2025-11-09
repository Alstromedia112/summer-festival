package com.me1q.summerFestival.game.boatrace.banner;

import com.me1q.summerFestival.game.boatrace.constants.BannerColor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

public class BannerManager {

    private final Map<Player, PlayerBanner> playerBanners;
    private final Map<Player, ItemStack> previousHelmets;

    public BannerManager() {
        this.playerBanners = new HashMap<>();
        this.previousHelmets = new HashMap<>();
    }

    public void assignBanners(List<Player> players) {
        playerBanners.clear();

        if (players.size() > BannerColor.MAX_TOTAL_PLAYERS) {
            throw new IllegalArgumentException(
                "Too many players. Maximum: " + BannerColor.MAX_TOTAL_PLAYERS);
        }

        int playerIndex = 0;
        for (Player player : players) {
            int colorIndex = playerIndex / BannerColor.PLAYERS_PER_COLOR;
            int number = (playerIndex % BannerColor.PLAYERS_PER_COLOR) + 1;

            BannerColor color = BannerColor.values()[colorIndex];
            PlayerBanner banner = new PlayerBanner(color, number);

            playerBanners.put(player, banner);
            playerIndex++;
        }
    }

    public void equipBanners() {
        for (Map.Entry<Player, PlayerBanner> entry : playerBanners.entrySet()) {
            Player player = entry.getKey();
            PlayerBanner banner = entry.getValue();

            if (!player.isOnline()) {
                continue;
            }

            ItemStack currentHelmet = player.getInventory().getHelmet();
            previousHelmets.put(player, currentHelmet);

            ItemStack bannerItem = createBannerItem(banner);
            player.getInventory().setHelmet(bannerItem);
        }
    }

    public void removeBanners() {
        for (Map.Entry<Player, ItemStack> entry : previousHelmets.entrySet()) {
            Player player = entry.getKey();
            ItemStack previousHelmet = entry.getValue();

            if (!player.isOnline()) {
                continue;
            }

            player.getInventory().setHelmet(previousHelmet);
        }

        playerBanners.clear();
        previousHelmets.clear();
    }

    private ItemStack createBannerItem(PlayerBanner banner) {
        Material bannerMaterial = getBannerMaterial(banner.getColor());
        ItemStack bannerItem = new ItemStack(bannerMaterial);
        BannerMeta meta = (BannerMeta) bannerItem.getItemMeta();

        List<Pattern> patterns = getNumberPatterns(banner.getNumber(), banner.getColor()
            .getDyeColor());
        meta.setPatterns(patterns);

        meta.displayName(Component.text(banner.toString())
            .color(getTextColor(banner.getColor()))
            .decoration(net.kyori.adventure.text.format.TextDecoration.ITALIC, false));

        bannerItem.setItemMeta(meta);
        return bannerItem;
    }

    private Material getBannerMaterial(BannerColor color) {
        return switch (color) {
            case RED -> Material.RED_BANNER;
            case BLUE -> Material.BLUE_BANNER;
            case GREEN -> Material.GREEN_BANNER;
            case YELLOW -> Material.YELLOW_BANNER;
            case WHITE -> Material.WHITE_BANNER;
        };
    }

    private NamedTextColor getTextColor(BannerColor color) {
        return switch (color) {
            case RED -> NamedTextColor.RED;
            case BLUE -> NamedTextColor.BLUE;
            case GREEN -> NamedTextColor.GREEN;
            case YELLOW -> NamedTextColor.YELLOW;
            case WHITE -> NamedTextColor.WHITE;
        };
    }

    private List<Pattern> getNumberPatterns(int number, DyeColor color) {
        List<Pattern> patterns = new ArrayList<>();
        DyeColor black = DyeColor.BLACK;

        switch (number) {
            case 1:
                patterns.addAll(List.of(
                    new Pattern(black, PatternType.STRIPE_CENTER),
                    new Pattern(black, PatternType.STRIPE_BOTTOM),
                    new Pattern(black, PatternType.SQUARE_TOP_LEFT),
                    new Pattern(color, PatternType.CURLY_BORDER),
                    new Pattern(color, PatternType.BORDER)
                ));
                break;
            case 2:
                patterns.addAll(List.of(
                    new Pattern(black, PatternType.STRIPE_TOP),
                    new Pattern(color, PatternType.RHOMBUS),
                    new Pattern(black, PatternType.STRIPE_BOTTOM),
                    new Pattern(black, PatternType.STRIPE_DOWNLEFT),
                    new Pattern(color, PatternType.BORDER)
                ));
                break;
            case 3:
                patterns.addAll(List.of(
                    new Pattern(black, PatternType.STRIPE_TOP),
                    new Pattern(black, PatternType.STRIPE_MIDDLE),
                    new Pattern(black, PatternType.STRIPE_RIGHT),
                    new Pattern(black, PatternType.STRIPE_BOTTOM),
                    new Pattern(color, PatternType.CURLY_BORDER),
                    new Pattern(color, PatternType.BORDER)
                ));
                break;
            case 4:
                patterns.addAll(List.of(
                    new Pattern(black, PatternType.STRIPE_LEFT),
                    new Pattern(color, PatternType.HALF_HORIZONTAL_BOTTOM),
                    new Pattern(black, PatternType.STRIPE_RIGHT),
                    new Pattern(black, PatternType.STRIPE_MIDDLE),
                    new Pattern(color, PatternType.BORDER)
                ));
                break;
        }

        return patterns;
    }
}

