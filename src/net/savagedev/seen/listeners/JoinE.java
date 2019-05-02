package net.savagedev.seen.listeners;

import net.savagedev.seen.Seen;
import net.savagedev.seen.utils.io.FileUtils;
import org.bukkit.Statistic;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.File;
import java.util.UUID;

public class JoinE implements Listener {
    private final Seen plugin;

    public JoinE(Seen plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoinE(final PlayerJoinEvent e) {
        Player user = e.getPlayer();
        UUID uuid = user.getUniqueId();

        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
            File file = new File(this.plugin.getDataFolder(), String.format("storage/%s.yml", uuid.toString()));
            FileUtils.create(file);

            FileConfiguration storageFile = FileUtils.load(file);

            this.updateStatistics(storageFile, user.getStatistic(Statistic.LEAVE_GAME) + 1, user.getStatistic(Statistic.PLAY_ONE_MINUTE));
            this.updateJoinTime(uuid, storageFile, System.currentTimeMillis());
            this.plugin.getMojangUtils().updateLocalUsernameHistory(uuid);
            FileUtils.save(storageFile, file);
        });
    }

    private void updateJoinTime(UUID uuid, FileConfiguration storageFile, long joinTime) {
        this.plugin.getStatsManager().setJoinTime(uuid, joinTime);
        storageFile.set("join-time", joinTime);
    }

    private void updateStatistics(FileConfiguration storageFile, long times_joined, long playtime) {
        storageFile.set("times-joined", times_joined);
        storageFile.set("playtime", playtime);
    }
}
