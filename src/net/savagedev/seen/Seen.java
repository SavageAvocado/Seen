package net.savagedev.seen;

import com.earth2me.essentials.Essentials;
import net.milkbowl.vault.permission.Permission;
import net.savagedev.seen.commands.PlaytimeCmd;
import net.savagedev.seen.commands.SeenCmd;
import net.savagedev.seen.commands.SeenReloadCmd;
import net.savagedev.seen.listeners.JoinE;
import net.savagedev.seen.listeners.QuitE;
import net.savagedev.seen.papi.PapiHook;
import net.savagedev.seen.stats.StatsManager;
import net.savagedev.seen.utils.io.FileUtils;
import net.savagedev.seen.utils.io.MojangUtils;
import org.bukkit.Statistic;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Seen extends JavaPlugin {
    private StatsManager statsManager;
    private MojangUtils mojangUtils;
    private Permission permission;
    private Essentials essentials;

    @Override
    public void onEnable() {
        this.loadConfig();
        this.loadUtils();
        this.hookVault();
        this.hookPapi();
        this.loadCommands();
        this.loadListeners();
        this.hookEssentials();
    }

    @Override
    public void onDisable() {
        if (this.getServer().getOnlinePlayers().isEmpty()) {
            return;
        }

        for (Player user : this.getServer().getOnlinePlayers()) {
            File file = new File(this.getDataFolder(), String.format("storage/%s.yml", user.getUniqueId().toString()));
            FileConfiguration config = FileUtils.load(file);
            config.set("playtime", user.getStatistic(Statistic.PLAY_ONE_MINUTE));
            FileUtils.save(config, file);
        }
    }

    public void reload() {
        this.reloadConfig();
    }

    private void loadUtils() {
        this.statsManager = new StatsManager(this);
        this.mojangUtils = new MojangUtils(this);
    }

    private void loadConfig() {
        this.saveDefaultConfig();
    }

    private void loadListeners() {
        PluginManager pluginManager = this.getServer().getPluginManager();
        pluginManager.registerEvents(new JoinE(this), this);
        pluginManager.registerEvents(new QuitE(this), this);
    }

    private void hookPapi() {
        if (this.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PapiHook(this, "avocado").hook();
        }
    }

    private void hookEssentials() {
        if (this.getServer().getPluginManager().getPlugin("Essentials") != null) {
            this.essentials = Essentials.getPlugin(Essentials.class);
            return;
        }

        this.getServer().getLogger().severe(String.format("[%s] Essentials not found! Disabling plugin.", this.getDescription().getName()));
        this.getServer().getPluginManager().disablePlugin(this);
    }

    private void hookVault() {
        if (this.getServer().getPluginManager().getPlugin("Vault") == null) {
            this.getServer().getLogger().severe(String.format("[%s] Vault not found! Disabling plugin.", this.getDescription().getName()));
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        RegisteredServiceProvider<Permission> rsp = this.getServer().getServicesManager().getRegistration(Permission.class);

        if (rsp == null) {
            this.getServer().getLogger().severe(String.format("[%s] Registered service provider not found! Disabling plugin.", this.getDescription().getName()));
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        if ((this.permission = rsp.getProvider()) == null) {
            this.getServer().getLogger().severe(String.format("[%s] Permissions plugin not found! Disabling plugin.", this.getDescription().getName()));
            this.getServer().getPluginManager().disablePlugin(this);
        }
    }

    private void loadCommands() {
        this.getCommand("seenreload").setExecutor(new SeenReloadCmd(this));
        this.getCommand("playtime").setExecutor(new PlaytimeCmd(this));
        this.getCommand("seen").setExecutor(new SeenCmd(this));
    }

    public StatsManager getStatsManager() {
        return this.statsManager;
    }

    public MojangUtils getMojangUtils() {
        return this.mojangUtils;
    }

    public Permission getPermission() {
        return this.permission;
    }

    public Essentials getEssentials() {
        return this.essentials;
    }
}
