package net.savagedev.seen.listeners;

import net.savagedev.seen.SeenPlugin;
import net.savagedev.seen.utils.io.MojangApi;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class ConnectionListener implements Listener {
    private final SeenPlugin seen;

    public ConnectionListener(SeenPlugin seen) {
        this.seen = seen;
    }

    @EventHandler
    public void on(AsyncPlayerPreLoginEvent event) {
        final UUID uuid = event.getUniqueId();
        try {
            final List<String> mojangUsernames = MojangApi.getUsernameHistory(uuid);
            final List<String> localUsernames = this.seen.getStorage().getUsernameHistory(uuid).join();

            if (mojangUsernames.size() > localUsernames.size()) {
                this.seen.getStorage().updateUsernameHistory(uuid, mojangUsernames);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.seen.getStorage().updateLastOnline(uuid, System.currentTimeMillis()).join();
        this.seen.getStorage().updateIpAddress(uuid, event.getAddress().getHostAddress()).join();

        this.seen.getUserManager().load(uuid);
    }

    @EventHandler
    public void on(PlayerJoinEvent event) {
        final Player player = event.getPlayer();

        this.seen.getStorage().updateJoinCount(player.getUniqueId(), player.getStatistic(Statistic.LEAVE_GAME) + 1);
        this.seen.getStorage().updatePlaytime(player.getUniqueId(), this.seen.getCompatModule().getTicksPlayed(player));
    }

    @EventHandler
    public void on(PlayerQuitEvent event) {
        final Player player = event.getPlayer();

        this.seen.getStorage().updatePlaytime(player.getUniqueId(), this.seen.getCompatModule().getTicksPlayed(player));
        this.seen.getUserManager().unload(player.getUniqueId());
    }
}
