package net.savagedev.seen.commands;

import net.savagedev.seen.Seen;
import net.savagedev.seen.commands.async.AsyncCommand;
import net.savagedev.seen.utils.MessageUtils;
import org.bukkit.command.CommandSender;

public class SeenReloadCmd extends AsyncCommand {
    public SeenReloadCmd(Seen plugin) {
        super(plugin);
    }

    @Override
    public void execute(CommandSender user, String... args) {
        if (!user.hasPermission(this.plugin.getConfig().getString("permissions.reload"))) {
            MessageUtils.message(user, "&cYou do not have permission to execute this command.");
            return;
        }

        this.plugin.reload();
        MessageUtils.message(user, "&9Plugin reloaded.");
    }
}
