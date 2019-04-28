package net.savagedev.seen.papi;

import me.clip.placeholderapi.external.EZPlaceholderHook;
import net.savagedev.seen.Seen;
import net.savagedev.seen.utils.DateUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class PapiHook extends EZPlaceholderHook {
    private final Seen plugin;

    public PapiHook(Seen plugin, String identifier) {
        super(plugin, identifier);
        this.plugin = plugin;
    }

    @Override
    public String onPlaceholderRequest(Player user, String placeholder) {
        if (user == null) return "";

        if (placeholder.equalsIgnoreCase("playtime")) {
            return this.getPlayTime(user);
        }

        if (placeholder.equalsIgnoreCase("seen")) {
            return this.getLastSeen(user);
        }

        String[] splitPlaceholder = placeholder.split("_");
        String offlineUsername = splitPlaceholder.length > 1 ? splitPlaceholder[1] : "";
        OfflinePlayer offlinePlayer = this.plugin.getServer().getOfflinePlayer(offlineUsername);

        if (offlinePlayer == null)
            return "Unknown";

        if (placeholder.toLowerCase().startsWith("playtime_")) {
            return this.getPlayTime(offlinePlayer);
        }

        if (placeholder.toLowerCase().startsWith("seen_")) {
            return this.getLastSeen(offlinePlayer);
        }

        return null;
    }

    private String getPlayTime(OfflinePlayer user) {
        FileConfiguration config = this.plugin.getFileUtils().getFileConfiguration(user.getUniqueId().toString());
        return config.getConfigurationSection("").contains("playtime") ? this.plugin.getDateUtils().formatPlayTime(config.getLong("playtime"), DateUtils.TimeLengthFormat.LONG) : "Unknown";
    }

    private String getLastSeen(OfflinePlayer user) {
        return this.plugin.getDateUtils().formatDateDiff(new Date(), new Date(user.isOnline() ? this.plugin.getJoinTime(user.getUniqueId()) : user.getLastPlayed()), DateUtils.TimeLengthFormat.LONG);
    }
}
