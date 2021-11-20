package net.savagedev.seen.utils;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public final class MessageUtils {
    private MessageUtils() {
        throw new UnsupportedOperationException();
    }

    public static void message(CommandSender user, List<String> messages) {
        for (String message : messages) {
            message(user, message);
        }
    }

    public static void message(CommandSender user, String message) {
        if (message.equals("%none%")) {
            return;
        }

        user.sendMessage(color(message));
    }

    public static List<String> format(List<String> messages, String... placeholders) {
        List<String> formatted = new ArrayList<>();

        for (String message : messages) {
            formatted.add(format(message, placeholders));
        }

        return formatted;
    }

    public static String format(String message, String... placeholders) {
        for (int i = 0; i < placeholders.length - 1; i += 2) {
            String replacement = placeholders[i + 1];
            String placeholder = placeholders[i];

            message = message.replace(placeholder, replacement);
        }

        return message;
    }

    private static String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
