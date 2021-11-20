package net.savagedev.seen;

import net.milkbowl.vault.permission.Permission;
import net.savagedev.seen.commands.PlaytimeCmd;
import net.savagedev.seen.commands.SeenCmd;
import net.savagedev.seen.commands.SeenReloadCmd;
import net.savagedev.seen.common.CompatModule;
import net.savagedev.seen.common.model.user.UserManager;
import net.savagedev.seen.common.storage.Storage;
import net.savagedev.seen.common.storage.implementation.file.yaml.YamlStorage;
import net.savagedev.seen.compat.LegacyCompat;
import net.savagedev.seen.compat.ModernCompat;
import net.savagedev.seen.listeners.ConnectionListener;
import net.savagedev.seen.papi.PapiHook;
import org.bukkit.Material;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class SeenPlugin extends JavaPlugin {
    // External dependencies.
    private Permission permission;

    private CompatModule compatModule;
    private UserManager userManager;
    private Storage storage;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        this.storage = new Storage(new YamlStorage(this.getDataFolder().toPath().resolve("data")));
        this.storage.init();
        this.userManager = new UserManager(this.storage);
        try {
            final Material modernMaterial = Material.valueOf("TURTLE_EGG");
            this.compatModule = new ModernCompat();
        } catch (IllegalArgumentException ignored) {
            this.compatModule = new LegacyCompat(this.userManager);
        }
        this.initCommands();
        this.initListeners();
        this.hookDependencies();
    }

    @Override
    public void onDisable() {
        this.storage.shutdown();
    }

    private void initListeners() {
        this.getServer().getPluginManager().registerEvents(new ConnectionListener(this), this);
    }

    private void initCommands() {
        Objects.requireNonNull(this.getCommand("seenreload")).setExecutor(new SeenReloadCmd(this));
        Objects.requireNonNull(this.getCommand("playtime")).setExecutor(new PlaytimeCmd(this));
        Objects.requireNonNull(this.getCommand("seen")).setExecutor(new SeenCmd(this));
    }

    private void hookDependencies() {
        if (this.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new PapiHook(this);
        }

        final RegisteredServiceProvider<Permission> provider = this.getServer()
                .getServicesManager().getRegistration(Permission.class);
        if (provider != null) {
            this.permission = provider.getProvider();
        }
    }

    public Permission getPermission() {
        return this.permission;
    }

    public CompatModule getCompatModule() {
        return this.compatModule;
    }

    public UserManager getUserManager() {
        return this.userManager;
    }

    public Storage getStorage() {
        return this.storage;
    }
}
