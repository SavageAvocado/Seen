package net.savagedev.seen.commands;

import net.savagedev.seen.Seen;
import net.savagedev.seen.commands.async.AsyncCommand;
import net.savagedev.seen.utils.DateUtils;
import net.savagedev.seen.utils.MessageUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressWarnings("Duplicates")
public class SeenCmd extends AsyncCommand {
    public SeenCmd(Seen plugin) {
        super(plugin);
    }

    @Override
    public void execute(CommandSender sender, String... args) {
        if (!(sender instanceof Player)) {
            return;
        }

        Player user = (Player) sender;

        if (args.length == 0) {
            MessageUtils.message(user, this.plugin.getConfig().getString("messages.error.invalid-arguments"));
            return;
        }

        OfflinePlayer target = this.plugin.getServer().getOfflinePlayer(args[0]);

        if (!this.plugin.getStatsManager().playerExists(target)) {
            MessageUtils.message(user, this.plugin.getConfig().getString("messages.error.player-not-found"));
            return;
        }

        this.sendSeenMessage(user, target);
    }

    private void sendSeenMessage(Player user, OfflinePlayer target) {
        String menuKey = String.format("messages.seen.%s", this.getSeenMenu(user));

        MessageUtils.message(user, MessageUtils.format(this.plugin.getConfig().getStringList(menuKey),
                "%player%", target.getName(),
                "%status%", this.plugin.getConfig().getString("placeholders.status." + target.isOnline()),
                "%seen%", DateUtils.formatTimeDifference(new Date(this.plugin.getStatsManager().getLastOnline(target.getUniqueId())), new Date(), DateUtils.TimeLengthFormat.valueOf(this.plugin.getConfig().getString("options.seen-time-format").toUpperCase())),
                "%join-date%", new SimpleDateFormat(this.plugin.getConfig().getString("formats.dates")).format(new Date(target.getFirstPlayed())),
                "%playtime%", DateUtils.formatTime(this.plugin.getStatsManager().getPlaytime(target.getUniqueId()), DateUtils.TimeLengthFormat.valueOf(this.plugin.getConfig().getString("options.playtime-time-format").toUpperCase())),
                "%name-history%", this.plugin.getMojangUtils().getNameHistory(target.getUniqueId()),
                "%ip%", this.plugin.getStatsManager().getIpAddress(target.getUniqueId()),
                "%group%", this.plugin.getPermission().getPrimaryGroup(user.getLocation().getWorld().getName(), target)
        ));
    }

    private String getSeenMenu(Player user) {
        for (String menu : this.plugin.getConfig().getConfigurationSection("messages.seen").getKeys(false)) {
            if (!menu.equals("default") && user.hasPermission(String.format("seen.%s", menu))) {
                return menu;
            }
        }

        return "default";
    }
}
