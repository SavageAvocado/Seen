package net.savagedev.seen.utils;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.List;

public class StringUtils {
    String listToString(List<String> messages, String separator) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < messages.size(); i++) {
            if (!this.listHasNext(messages, i)) {
                stringBuilder.append(messages.get(i));
                return stringBuilder.toString().trim();
            }

            stringBuilder.append(messages.get(i)).append(separator);
        }

        return stringBuilder.toString().trim();
    }

    private boolean listHasNext(List<String> list, int currentIndex) {
        try {
            list.get(currentIndex + 1);
            return true;
        } catch (IndexOutOfBoundsException ignored) {
            return false;
        }
    }

    public void message(CommandSender user, String message) {
        user.sendMessage(this.color(message));
    }

    private String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
