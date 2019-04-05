package net.savagedev.seen.commands;

import com.earth2me.essentials.User;
import net.savagedev.seen.Seen;
import net.savagedev.seen.commands.async.AsyncCommand;
import net.savagedev.seen.utils.DateUtils;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class SeenCmd extends AsyncCommand {
    public SeenCmd(Seen plugin) {
        super(plugin);
    }

    @Override
    public void execute(CommandSender user, String... args) {
        if (args.length == 0) {
            this.getPlugin().getStringUtils().message(user, this.getPlugin().getConfig().getString("messages.invalid-arguments"));
            return;
        }

        Player onlineTarget;
        if ((onlineTarget = this.getPlugin().getServer().getPlayer(args[0])) != null) {
            String nameHist = this.getPlugin().getMojangUtils().getNameHistory(onlineTarget);
            List<String> messages = user.hasPermission(this.getPlugin().getConfig().getString("admin-permission")) ? this.getPlugin().getConfig().getStringList("messages.admin-seen") : this.getPlugin().getConfig().getStringList("messages.seen");
            for (String seenMessage : messages)
                this.getPlugin().getStringUtils().message(user, this.format(seenMessage, onlineTarget, nameHist, this.getPlugin().getJoinTime(onlineTarget.getUniqueId()), this.getPlugin().getDateUtils().formatPlayTime(onlineTarget.getStatistic(Statistic.PLAY_ONE_TICK), DateUtils.TimeLengthFormat.LONG), this.getPlugin().getPermission().getPrimaryGroup(onlineTarget)));
            return;
        }

        OfflinePlayer offlineTarget;
        if ((offlineTarget = this.getPlugin().getServer().getOfflinePlayer(args[0])) == null || (!offlineTarget.isOnline() && !offlineTarget.hasPlayedBefore())) {
            this.getPlugin().getStringUtils().message(user, this.getPlugin().getConfig().getString("messages.player-not-found"));
            return;
        }

        String nameHist = this.getPlugin().getMojangUtils().getNameHistory(offlineTarget);
        FileConfiguration config = this.getPlugin().getFileUtils().getFileConfiguration(offlineTarget.getUniqueId().toString());
        String playTime = config.getConfigurationSection("").contains("playtime") ? this.getPlugin().getDateUtils().formatPlayTime(config.getLong("playtime"), DateUtils.TimeLengthFormat.LONG) : "Unknown";

        List<String> messages = user.hasPermission(this.getPlugin().getConfig().getString("admin-permission")) ? this.getPlugin().getConfig().getStringList("messages.admin-seen") : this.getPlugin().getConfig().getStringList("messages.seen");
        for (String seenMessage : messages)
            this.getPlugin().getStringUtils().message(user, this.format(seenMessage, offlineTarget, nameHist, offlineTarget.getLastPlayed(), playTime, this.getPlugin().getPermission().getPrimaryGroup((user instanceof Player) ? ((Player) user).getLocation().getWorld().getName() : this.getPlugin().getServer().getWorlds().get(0).getName(), offlineTarget)));
    }

    private String format(String message, OfflinePlayer target, String nameHist, long lastPlayed, String playTime, String group) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date(lastPlayed));
        String seen = this.getPlugin().getDateUtils().formatDateDiff(new GregorianCalendar(), calendar);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(this.getPlugin().getConfig().getString("formats.first-join"));
        String firstJoined = simpleDateFormat.format(new Date(target.getFirstPlayed()));

        User essUser = this.getPlugin().getEssentials().getOfflineUser(target.getName());

        message = message.replace("%status%", target.isOnline() ? this.getPlugin().getConfig().getString("status.online") : this.getPlugin().getConfig().getString("status.offline"));
        message = message.replace("%banned%", this.getPlugin().getServer().getBannedPlayers().contains(target) ? "Yes" : "No");
        message = message.replace("%muted%", essUser == null ? "Unknown" : (essUser.getMuted() ? "Yes" : "No"));
        message = message.replace("%ip%", essUser == null ? "Unknown" : essUser.getLastLoginAddress());
        message = message.replace("%location%", this.getLocation(target));
        message = message.replace("%player%", target.getName());
        message = message.replace("%first-join%", firstJoined);
        message = message.replace("%name-history%", nameHist);
        message = message.replace("%playtime%", playTime);
        message = message.replace("%group%", group);
        message = message.replace("%seen%", seen);

        return message;
    }

    private String getLocation(OfflinePlayer target) {
        if (target.isOnline())
            return this.formatLocation(target.getPlayer().getLocation());

        Location logoutLocation;
        User essUser = this.getPlugin().getEssentials().getOfflineUser(target.getName());
        if (essUser == null || (logoutLocation = essUser.getLogoutLocation()) == null)
            return "Unknown";

        return this.formatLocation(logoutLocation);
    }

    private String formatLocation(Location location) {
        long x, y, z;
        x = Math.round(location.getX());
        y = Math.round(location.getY());
        z = Math.round(location.getZ());

        String locationFormat = this.getPlugin().getConfig().getString("formats.location");

        locationFormat = locationFormat.replace("%world%", location.getWorld().getName());
        locationFormat = locationFormat.replace("%x%", String.valueOf(x));
        locationFormat = locationFormat.replace("%y%", String.valueOf(y));
        locationFormat = locationFormat.replace("%z%", String.valueOf(z));

        return locationFormat;
    }
}
