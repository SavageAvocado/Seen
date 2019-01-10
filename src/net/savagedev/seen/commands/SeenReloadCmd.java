package net.savagedev.seen.commands;

import net.savagedev.seen.Seen;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class SeenReloadCmd implements CommandExecutor {
    private Seen plugin;

    public SeenReloadCmd(Seen plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender user, Command cmd, String d, String[] args) {
        if (!user.hasPermission(this.plugin.getConfig().getString("admin-permission"))) {
            this.plugin.getStringUtils().message(user, "&cYou do not have permission to execute this command.");
            return true;
        }

        this.plugin.reload();
        this.plugin.getStringUtils().message(user, "&9Plugin reloaded.");
        return true;
    }
}
