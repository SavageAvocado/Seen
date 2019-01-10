package net.savagedev.seen.commands;

import com.earth2me.essentials.User;
import net.savagedev.seen.Seen;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.List;

public class PlaytimeCmd implements CommandExecutor {
    private Seen plugin;

    public PlaytimeCmd(Seen plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender user, Command cmd, String d, String[] args) {
        if (args.length == 0) {
            if (!(user instanceof Player)) {
                this.plugin.getStringUtils().message(user, "&cInvalid arguments! Try /playtime <player>");
                return true;
            }

            for (String message : this.plugin.getConfig().getStringList("messages.playtime"))
                this.plugin.getStringUtils().message(user, this.format((OfflinePlayer) user, message, String.valueOf(((Player) user).getStatistic(Statistic.LEAVE_GAME) + 1), this.plugin.getDateUtils().formatPlayTimePT(((Player) user).getStatistic(Statistic.PLAY_ONE_TICK))));
            return true;
        }

        if (this.plugin.getServer().getPlayer(args[0]) != null) {
            this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
                Player target = this.plugin.getServer().getPlayer(args[0]);

                for (String message : this.plugin.getConfig().getStringList("messages.playtime"))
                    this.plugin.getStringUtils().message(user, this.format(target, message, String.valueOf(target.getStatistic(Statistic.LEAVE_GAME) + 1), this.plugin.getDateUtils().formatPlayTimePT(target.getStatistic(Statistic.PLAY_ONE_TICK))));
            });
            return true;
        }

        OfflinePlayer target;
        if ((target = this.plugin.getServer().getOfflinePlayer(args[0])) == null || (!target.isOnline() && !target.hasPlayedBefore())) {
            this.plugin.getStringUtils().message(user, this.plugin.getConfig().getString("messages.player-not-found"));
            return true;
        }

        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
            FileConfiguration config = this.plugin.getFileUtils().getFileConfiguration(target.getUniqueId().toString());
            String playTime = config.getConfigurationSection("").contains("playtime") ? this.plugin.getDateUtils().formatPlayTimePT(config.getLong("playtime")) : "Unknown";
            String timesJoined = config.getConfigurationSection("").contains("times-joined") ? String.valueOf(config.getInt("times-joined")) : "Unknown";

            for (String message : this.plugin.getConfig().getStringList("messages.playtime"))
                this.plugin.getStringUtils().message(user, this.format(target, message, timesJoined, playTime));
        });
        return true;
    }

    private String format(OfflinePlayer target, String message, String timesJoined, String playTime) {
        message = message.replace("%player%", target.getName());
        message = message.replace("%times-joined%", timesJoined);
        message = message.replace("%playtime%", playTime);

        return message;
    }
}
