package net.savagedev.seen.utils.io;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class FileUtils {
    private static final Cache<String, FileConfiguration> TEMP;

    static {
        TEMP = CacheBuilder.newBuilder().maximumSize(50).expireAfterAccess(5, TimeUnit.MINUTES).build();
    }

    public static void save(FileConfiguration configuration, File file) {
        try {
            TEMP.asMap().replace(file.getName(), configuration);
            configuration.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean create(File file) {
        if (file.exists()) {
            return false;
        }

        file.getParentFile().mkdirs();
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    @Nonnull
    public static FileConfiguration load(File file) {
        try {
            return TEMP.get(file.getName(), () -> {
                FileConfiguration fileConfig = new YamlConfiguration();
                fileConfig.load(file);

                TEMP.put(file.getName(), fileConfig);
                return fileConfig;
            });
        } catch (ExecutionException e) {
            return null;
        }
    }
}
