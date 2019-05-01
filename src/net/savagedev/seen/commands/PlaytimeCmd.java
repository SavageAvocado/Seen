package net.savagedev.seen.commands;

import net.savagedev.seen.Seen;
import net.savagedev.seen.commands.async.AsyncCommand;
import net.savagedev.seen.utils.DateUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class PlaytimeCmd extends AsyncCommand {
    public PlaytimeCmd(Seen plugin) {
        super(plugin);
    }

    @Override
    public void execute(CommandSender user, String... args) {
        if (args.length == 0) {
            if (!(user instanceof Player)) {
                this.getPlugin().getStringUtils().message(user, "&cInvalid arguments! Try: playtime <player>");
                return;
            }

            for (String message : this.getPlugin().getConfig().getStringList("messages.playtime"))
                this.getPlugin().getStringUtils().message(user, this.format((OfflinePlayer) user, message, String.valueOf(((Player) user).getStatistic(Statistic.LEAVE_GAME) + 1), this.getPlugin().getDateUtils().formatPlayTime(((Player) user).getStatistic(Statistic.PLAY_ONE_MINUTE), DateUtils.TimeLengthFormat.SHORT)));
            return;
        }

        if (this.getPlugin().getServer().getPlayer(args[0]) != null) {
            Player target = this.getPlugin().getServer().getPlayer(args[0]);

            for (String message : this.getPlugin().getConfig().getStringList("messages.playtime"))
                this.getPlugin().getStringUtils().message(user, this.format(target, message, String.valueOf(target.getStatistic(Statistic.LEAVE_GAME) + 1), this.getPlugin().getDateUtils().formatPlayTime(target.getStatistic(Statistic.PLAY_ONE_MINUTE), DateUtils.TimeLengthFormat.SHORT)));
            return;
        }

        OfflinePlayer target;
        if ((target = this.getPlugin().getServer().getOfflinePlayer(args[0])) == null || (!target.isOnline() && !target.hasPlayedBefore())) {
            this.getPlugin().getStringUtils().message(user, this.getPlugin().getConfig().getString("messages.player-not-found"));
            return;
        }

        FileConfiguration config = this.getPlugin().getFileUtils().getFileConfiguration(target.getUniqueId().toString());
        String playTime = config.getConfigurationSection("").contains("playtime") ? this.getPlugin().getDateUtils().formatPlayTime(config.getLong("playtime"), DateUtils.TimeLengthFormat.SHORT) : "Unknown";
        String timesJoined = config.getConfigurationSection("").contains("times-joined") ? String.valueOf(config.getInt("times-joined")) : "Unknown";

        for (String message : this.getPlugin().getConfig().getStringList("messages.playtime"))
            this.getPlugin().getStringUtils().message(user, this.format(target, message, timesJoined, playTime));
    }

    private String format(OfflinePlayer target, String message, String timesJoined, String playTime) {
        message = message.replace("%player%", target.getName());
        message = message.replace("%times-joined%", timesJoined);
        message = message.replace("%playtime%", playTime);

        return message;
    }
}
