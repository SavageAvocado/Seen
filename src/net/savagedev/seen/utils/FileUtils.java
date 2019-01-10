package net.savagedev.seen.utils;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class FileUtils {
    private File dataFolder;

    public FileUtils(File dataFolder) {
        this.dataFolder = new File(dataFolder, "storage");
    }

    public void createFile(String name) {
        File file;
        if ((file = new File(this.dataFolder, this.correctFormat(name))).exists())
            return;

        file.getParentFile().mkdirs();
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveFileConfiguration(FileConfiguration configuration, String name) {
        try {
            configuration.save(this.getFile(name));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public FileConfiguration getFileConfiguration(String name) {
        YamlConfiguration conf = new YamlConfiguration();

        try {
            conf.load(this.getFile(name));
        } catch (IOException | InvalidConfigurationException ignored) {
            return null;
        }

        return conf;
    }

    private File getFile(String name) {
        return new File(this.dataFolder, this.correctFormat(name));
    }

    private String correctFormat(String name) {
        return name.endsWith(".yml") ? name : name + ".yml";
    }
}
