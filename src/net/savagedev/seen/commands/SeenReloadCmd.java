package net.savagedev.seen.commands;

import net.savagedev.seen.Seen;
import net.savagedev.seen.commands.async.AsyncCommand;
import org.bukkit.command.CommandSender;

public class SeenReloadCmd extends AsyncCommand {
    public SeenReloadCmd(Seen plugin) {
        super(plugin);
    }

    @Override
    public void execute(CommandSender user, String... args) {
        if (!user.hasPermission(this.getPlugin().getConfig().getString("admin-permission"))) {
            this.getPlugin().getStringUtils().message(user, "&cYou do not have permission to execute this command.");
            return;
        }

        this.getPlugin().reload();
        this.getPlugin().getStringUtils().message(user, "&9Plugin reloaded.");
    }
}
