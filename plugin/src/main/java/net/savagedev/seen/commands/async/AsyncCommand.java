package net.savagedev.seen.commands.async;

import net.savagedev.seen.SeenPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public abstract class AsyncCommand implements CommandExecutor {
    public final SeenPlugin seen;

    public AsyncCommand(SeenPlugin plugin) {
        this.seen = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String d, String[] args) {
        this.seen.getServer().getScheduler().runTaskAsynchronously(this.seen, () -> this.execute(sender, args));
        return true;
    }

    public abstract void execute(CommandSender sender, String... args);
}
