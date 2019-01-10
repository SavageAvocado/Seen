package net.savagedev.seen.commands;

import com.earth2me.essentials.User;
import net.savagedev.seen.Seen;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.*;

public class SeenCmd implements CommandExecutor, TabCompleter {
    private Seen plugin;

    public SeenCmd(Seen plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender user, Command cmd, String d, String[] args) {
        if (args.length == 0) {
            this.plugin.getStringUtils().message(user, this.plugin.getConfig().getString("messages.invalid-arguments"));
            return true;
        }

        if (this.plugin.getServer().getPlayer(args[0]) != null) {
            this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
                Player target = this.plugin.getServer().getPlayer(args[0]);
                String nameHist = this.plugin.getMojangUtils().getNameHistory(this.plugin.getServer().getPlayer(args[0]));
                String ip = target.getAddress().getHostString();

                List<String> messages = user.hasPermission(this.plugin.getConfig().getString("admin-permission")) ? this.plugin.getConfig().getStringList("messages.admin-seen") : this.plugin.getConfig().getStringList("messages.seen");
                for (String seenMessage : messages)
                    this.plugin.getStringUtils().message(user, this.format(seenMessage, target, nameHist, this.plugin.getJoinTime(target.getUniqueId()), ip, this.plugin.getDateUtils().formatPlayTime(target.getStatistic(Statistic.PLAY_ONE_TICK)), this.plugin.getPermission().getPrimaryGroup(target)));
            });
            return true;
        }

        OfflinePlayer target;
        if ((target = this.plugin.getServer().getOfflinePlayer(args[0])) == null || (!target.isOnline() && !target.hasPlayedBefore())) {
            this.plugin.getStringUtils().message(user, this.plugin.getConfig().getString("messages.player-not-found"));
            return true;
        }

        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
            String nameHist = this.plugin.getMojangUtils().getNameHistory(target);

            User essUser = this.plugin.getEssentials().getOfflineUser(target.getName());
            String ip = essUser == null ? "Unknown" : essUser.getLastLoginAddress();

            FileConfiguration config = this.plugin.getFileUtils().getFileConfiguration(target.getUniqueId().toString());
            String playTime = config.getConfigurationSection("").contains("playtime") ? this.plugin.getDateUtils().formatPlayTime(config.getLong("playtime")) : "Unknown";

            List<String> messages = user.hasPermission(this.plugin.getConfig().getString("admin-permission")) ? this.plugin.getConfig().getStringList("messages.admin-seen") : this.plugin.getConfig().getStringList("messages.seen");
            for (String seenMessage : messages)
                this.plugin.getStringUtils().message(user, this.format(seenMessage, target, nameHist, target.getLastPlayed(), ip, playTime, this.plugin.getPermission().getPrimaryGroup((user instanceof Player) ? ((Player) user).getLocation().getWorld().getName() : this.plugin.getServer().getWorlds().get(0).getName(), target)));
        });
        return true;
    }

    private String format(String message, OfflinePlayer target, String nameHist, long lastPlayed, String ip, String playTime, String group) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date(lastPlayed));
        String seen = this.plugin.getDateUtils().formatDateDiff(new GregorianCalendar(), calendar);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(this.plugin.getConfig().getString("formats.first-join"));
        String firstJoined = simpleDateFormat.format(new Date(target.getFirstPlayed()));

        User essUser = this.plugin.getEssentials().getOfflineUser(target.getName());

        message = message.replace("%muted%", String.valueOf(essUser == null ? "Unknown" : essUser.getMuted()));
        message = message.replace("%status%", target.isOnline() ? this.plugin.getConfig().getString("status.online") : this.plugin.getConfig().getString("status.offline"));
        message = message.replace("%banned%", String.valueOf(this.plugin.getServer().getBannedPlayers().contains(target)));
        message = message.replace("%location%", this.getLocation(target));
        message = message.replace("%player%", target.getName());
        message = message.replace("%first-join%", firstJoined);
        message = message.replace("%name-history%", nameHist);
        message = message.replace("%playtime%", playTime);
        message = message.replace("%group%", group);
        message = message.replace("%seen%", seen);
        message = message.replace("%ip%", ip);

        return message;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String d, String[] args) {

        List<String> offlinePlayers;
        if (args.length == 0) {
            offlinePlayers = new ArrayList<>();
            for (OfflinePlayer offlinePlayer : this.plugin.getServer().getOfflinePlayers())
                offlinePlayers.add(offlinePlayer.getName());

            return offlinePlayers;
        }

        if (args.length == 1) {
            offlinePlayers = new ArrayList<>();
            for (OfflinePlayer offlinePlayer : this.plugin.getServer().getOfflinePlayers())
                if (offlinePlayer.getName().toLowerCase().startsWith(args[0].toLowerCase()))
                    offlinePlayers.add(offlinePlayer.getName());

            return offlinePlayers;
        }
        return null;
    }

    private String getLocation(OfflinePlayer target) {
        String location;
        if (target.isOnline()) {
            Player onlineTarget = target.getPlayer();

            int x = (int) Math.round(onlineTarget.getLocation().getX());
            int y = (int) Math.round(onlineTarget.getLocation().getY());
            int z = (int) Math.round(onlineTarget.getLocation().getZ());

            location = this.plugin.getConfig().getString("formats.location");

            location = location.replace("%world%", onlineTarget.getLocation().getWorld().getName());
            location = location.replace("%x%", String.valueOf(x));
            location = location.replace("%y%", String.valueOf(y));
            location = location.replace("%z%", String.valueOf(z));

            return location;
        }

        User essUser = this.plugin.getEssentials().getOfflineUser(target.getName());
        if (essUser == null || essUser.getLogoutLocation() == null)
            return "Unknown";

        int x = (int) Math.round(essUser.getLogoutLocation().getX());
        int y = (int) Math.round(essUser.getLogoutLocation().getY());
        int z = (int) Math.round(essUser.getLogoutLocation().getZ());

        location = this.plugin.getConfig().getString("formats.location");

        location = location.replace("%world%", essUser.getLogoutLocation().getWorld().getName());
        location = location.replace("%x%", String.valueOf(x));
        location = location.replace("%y%", String.valueOf(y));
        location = location.replace("%z%", String.valueOf(z));

        return location;
    }
}
