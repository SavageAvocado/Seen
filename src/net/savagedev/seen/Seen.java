package net.savagedev.seen;

import com.earth2me.essentials.Essentials;
import net.milkbowl.vault.permission.Permission;
import net.savagedev.seen.commands.PlaytimeCmd;
import net.savagedev.seen.commands.SeenCmd;
import net.savagedev.seen.commands.SeenReloadCmd;
import net.savagedev.seen.listeners.JoinE;
import net.savagedev.seen.listeners.QuitE;
import net.savagedev.seen.utils.DateUtils;
import net.savagedev.seen.utils.FileUtils;
import net.savagedev.seen.utils.MojangUtils;
import net.savagedev.seen.utils.StringUtils;
import org.bukkit.Statistic;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Seen extends JavaPlugin {
    private Map<UUID, Long> joinTime;
    private StringUtils stringUtils;
    private MojangUtils mojangUtils;
    private Permission permission;
    private Essentials essentials;
    private FileUtils fileUtils;
    private DateUtils dateUtils;

    @Override
    public void onEnable() {
        this.loadConfig();
        this.loadUtils();
        this.hookVault();
        this.loadCommands();
        this.loadListeners();
        this.hookEssentials();
    }

    @Override
    public void onDisable() {
        this.joinTime.clear();

        if (this.getServer().getOnlinePlayers().size() <= 0)
            return;

        for (Player user : this.getServer().getOnlinePlayers()) {
            FileConfiguration config = this.getFileUtils().getFileConfiguration(user.getUniqueId().toString());
            config.set("playtime", user.getStatistic(Statistic.PLAY_ONE_TICK));
            this.getFileUtils().saveFileConfiguration(config, user.getUniqueId().toString());
        }
    }

    public void reload() {
        this.reloadConfig();
        this.dateUtils.setPreferredAccuracy(this.getConfig().getInt("preferred-accuracy"));
    }

    private void loadUtils() {
        this.joinTime = new HashMap<>();

        this.dateUtils = new DateUtils(this.getConfig().getInt("preferred-accuracy"));
        this.fileUtils = new FileUtils(this.getDataFolder());
        this.mojangUtils = new MojangUtils(this);
        this.stringUtils = new StringUtils();

        this.migrateNameHistories();

        if (this.getServer().getOnlinePlayers().size() <= 0)
            return;

        for (Player user : this.getServer().getOnlinePlayers()) {
            UUID uuid = user.getUniqueId();

            FileConfiguration config;
            if ((config = this.fileUtils.getFileConfiguration(uuid.toString())) == null) {
                this.fileUtils.createFile(uuid.toString());

                config = this.fileUtils.getFileConfiguration(uuid.toString());
                config.set("join-time", System.currentTimeMillis());
                this.fileUtils.saveFileConfiguration(config, uuid.toString());
            }

            this.setJoinTime(uuid, config.getLong("join-time"));
        }
    }

    private void loadConfig() {
        this.saveDefaultConfig();
    }

    private void loadListeners() {
        PluginManager pluginManager = this.getServer().getPluginManager();

        pluginManager.registerEvents(new JoinE(this), this);
        pluginManager.registerEvents(new QuitE(this), this);
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
        }

        RegisteredServiceProvider<Permission> rsp = this.getServer().getServicesManager().getRegistration(Permission.class);

        if (rsp == null) {
            this.getServer().getLogger().severe(String.format("[%s] Registered service provider not found! Disabling plugin.", this.getDescription().getName()));
            this.getServer().getPluginManager().disablePlugin(this);
        }

        if ((this.permission = rsp.getProvider()) == null) {
            this.getServer().getLogger().severe(String.format("[%s] Permissions plugin not found! Disabling plugin.", this.getDescription().getName()));
            this.getServer().getPluginManager().disablePlugin(this);
        }
    }

    private void loadCommands() {
        this.getCommand("seenreload").setExecutor(new SeenReloadCmd(this));


        PluginCommand playtime = this.getCommand("playtime");
        playtime.setExecutor(new PlaytimeCmd(this));

        PluginCommand command = this.getCommand("seen");
        SeenCmd seenCmd = new SeenCmd(this);

        command.setExecutor(seenCmd);
        command.setTabCompleter(seenCmd);
        playtime.setTabCompleter(seenCmd);
    }

    public void setJoinTime(UUID uuid, long time) {
        if (this.joinTime.containsKey(uuid))
            this.removeJoinTime(uuid);

        this.joinTime.put(uuid, time);
    }

    public void removeJoinTime(UUID uuid) {
        this.joinTime.remove(uuid);
    }

    private void migrateNameHistories() {
        File nameHistFile;
        if (!(nameHistFile = new File(this.getDataFolder(), "name_history.yml")).exists())
            return;

        this.getServer().getLogger().info(String.format("[%s] Starting name history migration...", this.getDescription().getName()));

        this.getServer().getScheduler().runTaskAsynchronously(this, () -> {
            FileConfiguration configuration = new YamlConfiguration();

            try {
                configuration.load(nameHistFile);
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }

            for (String uuid : configuration.getConfigurationSection("").getKeys(false)) {
                this.fileUtils.createFile(uuid);

                List<String> names = new ArrayList<>(configuration.getStringList(uuid));
                FileConfiguration configuration1 = this.fileUtils.getFileConfiguration(uuid);
                configuration1.set("name-history", names);
                configuration1.set("join-time", System.currentTimeMillis());
                this.fileUtils.saveFileConfiguration(configuration1, uuid);
            }

            new File(this.getDataFolder(), "name_history.yml").delete();
        });

        this.getServer().getLogger().info(String.format("[%s] Name history migration complete.", this.getDescription().getName()));
    }

    public long getJoinTime(UUID uuid) {
        return this.joinTime.get(uuid);
    }

    public MojangUtils getMojangUtils() {
        return this.mojangUtils;
    }

    public StringUtils getStringUtils() {
        return this.stringUtils;
    }

    public Permission getPermission() {
        return this.permission;
    }

    public Essentials getEssentials() {
        return this.essentials;
    }

    public FileUtils getFileUtils() {
        return this.fileUtils;
    }

    public DateUtils getDateUtils() {
        return this.dateUtils;
    }

}
