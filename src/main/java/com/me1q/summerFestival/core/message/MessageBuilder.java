package com.me1q.summerFestival.core.message;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

public final class MessageBuilder {

    private MessageBuilder() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static Component error(String message) {
        return Component.text(message).color(NamedTextColor.RED);
    }

    public static Component success(String message) {
        return Component.text(message).color(NamedTextColor.GREEN);
    }

    public static Component warning(String message) {
        return Component.text(message).color(NamedTextColor.YELLOW);
    }

    public static Component info(String message) {
        return Component.text(message).color(NamedTextColor.AQUA);
    }

    public static Component header(String message) {
        return Component.text("=== " + message + " ===").color(NamedTextColor.GOLD);
    }

    public static Component separator() {
        return Component.text("━━━━━━━━━━━━━━━━━━━━━━").color(NamedTextColor.GOLD);
    }

    public static Component title(String message, NamedTextColor color) {
        return Component.text(message).color(color).decorate(TextDecoration.BOLD);
    }

    public static Component clickable(String message, NamedTextColor color, String command,
        String hoverText) {
        return Component.text(message)
            .color(color)
            .decorate(TextDecoration.BOLD)
            .clickEvent(ClickEvent.runCommand(command))
            .hoverEvent(Component.text(hoverText).color(NamedTextColor.GRAY));
    }

    public static Component clickableURL(String message, NamedTextColor color, String url,
        String hoverText) {
        return Component.text(message)
            .color(color)
            .decorate(TextDecoration.BOLD)
            .clickEvent(ClickEvent.openUrl(url))
            .hoverEvent(Component.text(hoverText).color(NamedTextColor.GRAY));
    }

    public static Component hoverable(String message, NamedTextColor color, String hoverText) {
        return Component.text(message)
            .color(color)
            .decorate(TextDecoration.BOLD)
            .hoverEvent(Component.text(hoverText).color(NamedTextColor.GRAY));
    }
}

