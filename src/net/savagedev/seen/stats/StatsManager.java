package net.savagedev.seen.stats;

import com.earth2me.essentials.User;
import net.savagedev.seen.Seen;
import net.savagedev.seen.utils.io.FileUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StatsManager {
    private final Map<UUID, Long> joinTime;
    private final Seen plugin;

    public StatsManager(Seen plugin) {
        this.joinTime = new HashMap<>();
        this.plugin = plugin;
        this.init();
    }

    private void init() {
        if (this.plugin.getServer().getOnlinePlayers().isEmpty()) {
            return;
        }

        for (Player user : this.plugin.getServer().getOnlinePlayers()) {
            UUID uuid = user.getUniqueId();
            // TODO: Update join time cache.
        }
    }

    public void setJoinTime(UUID uuid, long time) {
        this.joinTime.putIfAbsent(uuid, time);
    }

    public void removeJoinTime(UUID uuid) {
        this.joinTime.remove(uuid);
    }

    public String getIpAddress(UUID uuid) {
        User user = this.plugin.getEssentials().getUser(uuid);

        if (user != null) {
            String ip = user.getLastLoginAddress();
            return ip == null ? "N/A" : ip;
        }

        return "N/A";
    }

    public boolean playerExists(OfflinePlayer user) {
        return user.hasPlayedBefore();
    }

    public long getLastOnline(UUID uuid) {
        if (this.joinTime.containsKey(uuid)) {
            return this.joinTime.get(uuid);
        }

        return FileUtils.load(new File(this.plugin.getDataFolder(), String.format("storage/%s.yml", uuid.toString()))).getLong("join-time");
    }

    public long getTimesJoined(UUID uuid) {
        Player user = this.plugin.getServer().getPlayer(uuid);

        return user == null ? FileUtils.load(new File(this.plugin.getDataFolder(), String.format("storage/%s.yml", uuid.toString()))).getLong("times-joined") : user.getStatistic(Statistic.LEAVE_GAME) + 1;
    }

    public long getPlaytime(UUID uuid) {
        Player user = this.plugin.getServer().getPlayer(uuid);

        return user == null ? FileUtils.load(new File(this.plugin.getDataFolder(), String.format("storage/%s.yml", uuid.toString()))).getLong("playtime") : user.getStatistic(Statistic.PLAY_ONE_MINUTE);
    }
}
