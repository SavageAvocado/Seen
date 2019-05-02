package net.savagedev.seen.commands;

import net.savagedev.seen.Seen;
import net.savagedev.seen.commands.async.AsyncCommand;
import net.savagedev.seen.utils.DateUtils;
import net.savagedev.seen.utils.MessageUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@SuppressWarnings("Duplicates")
public class PlaytimeCmd extends AsyncCommand {
    public PlaytimeCmd(Seen plugin) {
        super(plugin);
    }

    @Override
    public void execute(CommandSender sender, String... args) {
        if (!(sender instanceof Player)) {
            return;
        }

        Player user = (Player) sender;

        if (args.length == 0) {
            this.sendPlaytimeMessage(user, user);
        }

        OfflinePlayer target = this.plugin.getServer().getOfflinePlayer(args[0]);

        if (!this.plugin.getStatsManager().playerExists(target)) {
            MessageUtils.message(user, this.plugin.getConfig().getString("messages.error.player-not-found"));
            return;
        }

        this.sendPlaytimeMessage(user, target);
    }

    private void sendPlaytimeMessage(Player user, OfflinePlayer target) {
        MessageUtils.message(user, MessageUtils.format(this.plugin.getConfig().getStringList("messages.playtime"),
                "%player%", target.getName(),
                "%playtime%", DateUtils.formatTime(this.plugin.getStatsManager().getPlaytime(target.getUniqueId()), DateUtils.TimeLengthFormat.valueOf(this.plugin.getConfig().getString("options.playtime-time-format").toUpperCase())),
                "%times-joined%", String.valueOf(this.plugin.getStatsManager().getTimesJoined(target.getUniqueId()))
        ));
    }
}
