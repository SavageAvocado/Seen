package net.savagedev.seen.compat;

import net.savagedev.seen.common.CompatModule;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;

public class ModernCompat implements CompatModule {
    @Override
    public long getTicksPlayed(OfflinePlayer player) {
        return player.getStatistic(Statistic.PLAY_ONE_MINUTE) /* * 1200L */;
    }

    @Override
    public long getTimesJoined(OfflinePlayer player) {
        return player.getStatistic(Statistic.LEAVE_GAME) + 1L;
    }
}
