package net.savagedev.seen.listeners;

import net.savagedev.seen.Seen;
import org.bukkit.Statistic;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

public class JoinE implements Listener {
    private final Seen plugin;

    public JoinE(Seen plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoinE(PlayerJoinEvent e) {
        Player user = e.getPlayer();
        UUID uuid = user.getUniqueId();

        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, ()-> {
            long joinTime = System.currentTimeMillis();
            this.plugin.setJoinTime(uuid, joinTime);

            this.plugin.getMojangUtils().getNameHistory(user);

            FileConfiguration config = this.plugin.getFileUtils().getFileConfiguration(uuid.toString());
            config.set("times-joined", user.getStatistic(Statistic.LEAVE_GAME) + 1);
            config.set("playtime", user.getStatistic(Statistic.PLAY_ONE_MINUTE));
            config.set("join-time", joinTime);
            this.plugin.getFileUtils().saveFileConfiguration(config, uuid.toString());
        });
    }
}
