package net.savagedev.seen.commands;

import net.savagedev.seen.SeenPlugin;
import net.savagedev.seen.commands.async.AsyncCommand;
import net.savagedev.seen.utils.MessageUtils;
import net.savagedev.seen.utils.TimeUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlaytimeCmd extends AsyncCommand {
    public PlaytimeCmd(SeenPlugin plugin) {
        super(plugin);
    }

    @Override
    public void execute(CommandSender sender, String... args) {
        if (!(sender instanceof Player)) {
            return;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            this.sendPlaytimeMessage(player, player);
        }

        final OfflinePlayer target = this.seen.getServer().getOfflinePlayer(args[0]);

        if (!this.seen.getStorage().exists(target.getUniqueId()).join()) {
            MessageUtils.message(player, this.seen.getConfig().getString("messages.error.player-not-found"));
            return;
        }

        this.sendPlaytimeMessage(player, target);
    }

    private void sendPlaytimeMessage(Player user, OfflinePlayer target) {
        MessageUtils.message(user, MessageUtils.format(this.seen.getConfig().getStringList("messages.playtime"),
                "%player%", target.getName(),
                "%playtime%", TimeUtils.formatTime(this.seen.getCompatModule().getTicksPlayed(target),
                        TimeUtils.TimeLengthFormat.valueOf(this.seen.getConfig().getString("options.playtime-time-format").toUpperCase())),
                "%times-joined%", String.valueOf(this.seen.getCompatModule().getTimesJoined(target)))
        );
    }
}
