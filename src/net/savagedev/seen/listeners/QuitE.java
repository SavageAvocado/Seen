package net.savagedev.seen.listeners;

import net.savagedev.seen.Seen;
import org.bukkit.Statistic;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class QuitE implements Listener {
    private final Seen plugin;

    public QuitE(Seen plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onQuitE(PlayerQuitEvent e) {
        Player user = e.getPlayer();
        UUID uuid = user.getUniqueId();

        this.plugin.removeJoinTime(uuid);

        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
            FileConfiguration config = this.plugin.getFileUtils().getFileConfiguration(uuid.toString());
            config.set("playtime", user.getStatistic(Statistic.PLAY_ONE_TICK));
            this.plugin.getFileUtils().saveFileConfiguration(config, uuid.toString());
        });
    }
}
