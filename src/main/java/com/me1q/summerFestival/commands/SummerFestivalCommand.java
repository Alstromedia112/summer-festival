package com.me1q.summerFestival.commands;

import com.me1q.summerFestival.core.message.MessageBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class SummerFestivalCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
        @NotNull String label, @NotNull String @NotNull [] args) {
        if (!sender.isOp()) {
            sender.sendMessage(MessageBuilder.error("このコマンドを実行する権限がありません。"));
            return true;
        }

        sender.getServer().getOnlinePlayers().forEach(p -> {
            p.sendMessage(
                Component.text("===== ").color(NamedTextColor.GOLD)
                    .append(Component.text("U1 サーマフェスタ ").color(NamedTextColor.RED))
                    .append(Component.text("～ 今年最後の夏の思い出 ～").color(NamedTextColor.GRAY))
                    .append(Component.text(" =====").color(NamedTextColor.GOLD)));
            p.sendMessage(Component.text(""));
            p.sendMessage(Component.text("  運営紹介 (順不同):"));
            p.sendMessage(Component.text(""));
            p.sendMessage(Component.text("  ")
                .append(MessageBuilder.clickable(
                    "[yuuto]",
                    NamedTextColor.GREEN,
                    "",
                    "参加してくれてありがとう！！"))
                .append(Component.text("  MC・建築"))
            );
            p.sendMessage(Component.text("  ")
                .append(Component.text("[ハート]")
                    .color(NamedTextColor.GREEN).decoration(TextDecoration.BOLD, true))
                .append(Component.text("  MC"))
            );
            p.sendMessage(Component.text());
            p.sendMessage(Component.text("  ")
                .append(MessageBuilder.clickableURL(
                    "[メリア]",
                    NamedTextColor.GREEN,
                    "https://n-highschool.slack.com/archives/C08LMN5Q3A6",
                    "#times_メリアの独り言"))
                .append(Component.text("  建築・プラグイン開発"))
            );
            p.sendMessage(Component.text("  ")
                .append(MessageBuilder.clickableURL(
                    "[餅king]",
                    NamedTextColor.GREEN,
                    "https://n-highschool.slack.com/archives/C08LC5HG9JS",
                    "#times_餅kingの独り言"))
                .append(Component.text("  建築・動画制作"))
            );
            p.sendMessage(Component.text("  ")
                .append(MessageBuilder.clickableURL(
                    "[マスカット]",
                    NamedTextColor.GREEN,
                    "https://n-highschool.slack.com/archives/C08L4LUNZAT",
                    "#times_マスカットの部屋"))
                .append(Component.text("  建築"))
            );
            p.sendMessage(Component.text("  ")
                .append(MessageBuilder.clickableURL(
                    "[ukuk]",
                    NamedTextColor.GREEN,
                    "https://n-highschool.slack.com/archives/C09SMNUQEAW",
                    "#times_ukukのメモ帳"))
                .append(Component.text("  リーダー"))
            );
            p.sendMessage(Component.text("  ")
                .append(MessageBuilder.clickableURL(
                    "[Coke615]",
                    NamedTextColor.GREEN,
                    "https://n-highschool.slack.com/archives/C08L65CSRAA",
                    "#times_coke"))
                .append(Component.text("  建築"))
            );
            p.sendMessage(Component.text("  ")
                .append(Component.text("[naruf]")
                    .color(NamedTextColor.GREEN).decoration(TextDecoration.BOLD, true))
                .append(Component.text("  建築"))
            );
            p.sendMessage(Component.text("  ")
                .append(MessageBuilder.clickableURL(
                    "[Harou786]",
                    NamedTextColor.GREEN,
                    "https://n-highschool.slack.com/archives/C09SRCYMZL4",
                    "#はろうのクラウドファイル"))
                .append(Component.text("  カメラ・建築"))
            );
            p.sendMessage(Component.text("  ")
                .append(MessageBuilder.clickableURL(
                    "[達稀orRize]",
                    NamedTextColor.GREEN,
                    "https://n-highschool.slack.com/archives/C08LC5HM17G",
                    "times-rize"))
                .append(Component.text("  建築"))
            );
            p.sendMessage(Component.text("  ")
                .append(MessageBuilder.clickableURL(
                    "[YukiSannn]",
                    NamedTextColor.GREEN,
                    "https://n-highschool.slack.com/archives/C08M15SAYBS",
                    "#times_yukisannnの独り言"))
                .append(Component.text("  建築"))
            );
            p.sendMessage(Component.text("  ")
                .append(MessageBuilder.clickableURL(
                    "[アズシノ]",
                    NamedTextColor.GREEN,
                    "https://n-highschool.slack.com/archives/C08L65NA85C",
                    "#times_アズシノ"))
                .append(Component.text("  パンフレット作成"))
            );
            p.sendMessage(Component.text());
            p.sendMessage(Component.text("  ")
                .append(Component.text("[A.U.E.]")
                    .color(NamedTextColor.GREEN).decoration(TextDecoration.BOLD, true))
                .append(Component.text("  動画制作"))
            );
            p.sendMessage(Component.text("  ")
                .append(Component.text("[ねこ]")
                    .color(NamedTextColor.GREEN).decoration(TextDecoration.BOLD, true))
                .append(Component.text("  動画制作"))
            );
        });
        return true;
    }
}
