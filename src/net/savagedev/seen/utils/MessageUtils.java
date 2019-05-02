package net.savagedev.seen.utils;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class MessageUtils {
    public static void message(@Nonnull CommandSender user, @Nonnull List<String> messages) {
        for (String message : messages) {
            message(user, message);
        }
    }

    public static void message(@Nonnull CommandSender user, @Nonnull String message) {
        if (message.equals("%none%")) {
            return;
        }

        user.sendMessage(color(message));
    }

    public static String listToString(List<String> messages, String separator) {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < messages.size(); i++) {
            if (!listHasNext(messages, i)) {
                stringBuilder.append(messages.get(i));
                return stringBuilder.toString().trim();
            }

            stringBuilder.append(messages.get(i)).append(separator);
        }

        return stringBuilder.toString().trim();
    }

    private static boolean listHasNext(List<String> list, int currentIndex) {
        try {
            list.get(currentIndex + 1);
            return true;
        } catch (IndexOutOfBoundsException ignored) {
            return false;
        }
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
