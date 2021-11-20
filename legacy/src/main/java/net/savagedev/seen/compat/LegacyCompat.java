package net.savagedev.seen.compat;

import net.savagedev.seen.common.CompatModule;
import net.savagedev.seen.common.model.user.UserManager;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;

public class LegacyCompat implements CompatModule {
    private final UserManager userManager;

    public LegacyCompat(UserManager userManager) {
        this.userManager = userManager;
    }

    @Override
    public long getTicksPlayed(OfflinePlayer player) {
        if (player.isOnline()) {
            return player.getPlayer().getStatistic(Statistic.PLAY_ONE_TICK);
        }
        return this.userManager.getOrLoad(player.getUniqueId()).getPlaytime();
    }

    @Override
    public long getTimesJoined(OfflinePlayer player) {
        if (player.isOnline()) {
            return player.getPlayer().getStatistic(Statistic.LEAVE_GAME) + 1L;
        }
        return this.userManager.getOrLoad(player.getUniqueId()).getJoinCount();
    }
}
