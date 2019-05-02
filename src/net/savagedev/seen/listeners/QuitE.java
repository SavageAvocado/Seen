package net.savagedev.seen.listeners;

import net.savagedev.seen.Seen;
import net.savagedev.seen.utils.io.FileUtils;
import org.bukkit.Statistic;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.File;
import java.util.UUID;

public class QuitE implements Listener {
    private final Seen plugin;

    public QuitE(Seen plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEvent(final PlayerQuitEvent e) {
        Player user = e.getPlayer();
        UUID uuid = user.getUniqueId();

        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
            File file = new File(this.plugin.getDataFolder(), String.format("storage/%s.yml", uuid.toString()));

            FileConfiguration config = FileUtils.load(file);
            config.set("playtime", user.getStatistic(Statistic.PLAY_ONE_MINUTE));
            FileUtils.save(config, file);

            this.plugin.getStatsManager().removeJoinTime(uuid);
        });
    }
}
