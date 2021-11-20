package net.savagedev.seen.common;

import org.bukkit.OfflinePlayer;

public interface CompatModule {
    long getTicksPlayed(OfflinePlayer player);

    long getTimesJoined(OfflinePlayer player);
}
