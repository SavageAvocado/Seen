package net.savagedev.seen.commands;

import net.savagedev.seen.SeenPlugin;
import net.savagedev.seen.commands.async.AsyncCommand;
import net.savagedev.seen.common.model.user.User;
import net.savagedev.seen.utils.MessageUtils;
import net.savagedev.seen.utils.TimeUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SeenCmd extends AsyncCommand {
    public SeenCmd(SeenPlugin plugin) {
        super(plugin);
    }

    @Override
    public void execute(CommandSender sender, String... args) {
        if (!(sender instanceof Player)) {
            return;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            MessageUtils.message(player, this.seen.getConfig().getString("messages.error.invalid-arguments"));
            return;
        }

        OfflinePlayer target = this.seen.getServer().getOfflinePlayer(args[0]);

        if (!this.seen.getStorage().exists(target.getUniqueId()).join()) {
            MessageUtils.message(player, this.seen.getConfig().getString("messages.error.player-not-found"));
            return;
        }

        this.sendSeenMessage(player, target);
    }

    private void sendSeenMessage(Player player, OfflinePlayer target) {
        String menuKey = String.format("messages.seen.%s", this.getSeenMenu(player));

        User targetUser = this.seen.getUserManager().getOrLoad(target.getUniqueId());

        MessageUtils.message(player, MessageUtils.format(this.seen.getConfig().getStringList(menuKey),
                "%player%", target.getName(),
                "%status%", this.seen.getConfig().getString("placeholders.status." + target.isOnline()),
                "%seen%", TimeUtils.formatTimeDifference(new Date(targetUser.getLastOnline()), new Date(), TimeUtils.TimeLengthFormat.valueOf(this.seen.getConfig().getString("options.seen-time-format").toUpperCase())),
                "%join-date%", new SimpleDateFormat(this.seen.getConfig().getString("formats.dates")).format(new Date(target.getFirstPlayed())),
                "%playtime%", TimeUtils.formatTime(this.seen.getCompatModule().getTicksPlayed(target), TimeUtils.TimeLengthFormat.valueOf(this.seen.getConfig().getString("options.playtime-time-format").toUpperCase())),
                "%name-history%", String.join(this.seen.getConfig().getString("formats.separator"), targetUser.getUsernameHistory()),
                "%ip%", targetUser.getIpAddress(),
                "%group%", this.seen.getPermission().getPrimaryGroup(player.getLocation().getWorld().getName(), target)
        ));
    }

    private String getSeenMenu(Player user) {
        for (String menu : this.seen.getConfig().getConfigurationSection("messages.seen").getKeys(false)) {
            if (!menu.equals("default") && user.hasPermission(String.format("seen.%s", menu))) {
                return menu;
            }
        }
        return "default";
    }
}
