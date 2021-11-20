package net.savagedev.seen.papi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.savagedev.seen.SeenPlugin;
import net.savagedev.seen.utils.TimeUtils;
import org.bukkit.entity.Player;

import java.util.Date;

public class PapiHook extends PlaceholderExpansion {
    private final SeenPlugin seen;

    public PapiHook(SeenPlugin plugin) {
        this.seen = plugin;
    }

    @Override
    public String onPlaceholderRequest(Player player, String placeholder) {
        if (player == null) {
            return "Bro, WTF did you do?!";
        }

        if (placeholder.equalsIgnoreCase("playtime")) {
            return this.getFormattedPlaytime(player);
        }

        if (placeholder.equalsIgnoreCase("seen")) {
            return this.getFormattedLastSeen(player);
        }

        return null;
    }

    private String getFormattedPlaytime(Player player) {
        return TimeUtils.formatTime(this.seen.getCompatModule().getTicksPlayed(player), TimeUtils.TimeLengthFormat.LONG);
    }

    private String getFormattedLastSeen(Player player) {
        return TimeUtils.formatTimeDifference(new Date(), new Date(player.getLastPlayed()), TimeUtils.TimeLengthFormat.LONG);
    }

    @Override
    public String getIdentifier() {
        return "avocado";
    }

    @Override
    public String getAuthor() {
        return "SavageAvocado";
    }

    @Override
    public String getVersion() {
        return this.seen.getDescription().getVersion();
    }
}
