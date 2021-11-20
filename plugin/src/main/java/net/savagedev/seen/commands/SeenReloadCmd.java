package net.savagedev.seen.commands;

import net.savagedev.seen.SeenPlugin;
import net.savagedev.seen.commands.async.AsyncCommand;
import net.savagedev.seen.utils.MessageUtils;
import org.bukkit.command.CommandSender;

public class SeenReloadCmd extends AsyncCommand {
    public SeenReloadCmd(SeenPlugin plugin) {
        super(plugin);
    }

    @Override
    public void execute(CommandSender sender, String... args) {
        this.seen.reloadConfig();
        MessageUtils.message(sender, "&9Plugin reloaded.");
    }
}
