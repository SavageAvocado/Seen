package net.savagedev.seen.commands.async;

import net.savagedev.seen.Seen;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import javax.annotation.Nonnull;

public abstract class AsyncCommand implements CommandExecutor {
    public final Seen plugin;

    public AsyncCommand(Seen plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@Nonnull CommandSender sender, @Nonnull Command cmd, @Nonnull String d, @Nonnull String[] args) {
        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> this.execute(sender, args));
        return true;
    }

    public abstract void execute(CommandSender sender, String... args);
}
