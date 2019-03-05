package net.savagedev.seen.commands.async;

import net.savagedev.seen.Seen;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public abstract class AsyncCommand implements CommandExecutor {
    private final Seen plugin;

    public AsyncCommand(Seen plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String d, String[] args) {
        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> this.execute(sender, args));
        return true;
    }

    public abstract void execute(CommandSender sender, String... args);

    protected Seen getPlugin() {
        return this.plugin;
    }
}
