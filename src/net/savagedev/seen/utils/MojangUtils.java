package net.savagedev.seen.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.savagedev.seen.Seen;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MojangUtils {
    private Seen plugin;

    public MojangUtils(Seen plugin) {
        this.plugin = plugin;
    }

    public String getNameHistory(OfflinePlayer user) {
        String username = user.getName();
        UUID uuid = user.getUniqueId();

        FileConfiguration config;
        if ((config = this.plugin.getFileUtils().getFileConfiguration(uuid.toString())) == null) {
            this.plugin.getFileUtils().createFile(uuid.toString());

            config = this.plugin.getFileUtils().getFileConfiguration(uuid.toString());
            config.set("name-history", this.getNameHistoryFromAPI(uuid));
            config.set("join-time", System.currentTimeMillis());
            this.plugin.getFileUtils().saveFileConfiguration(config, uuid.toString());
        }

        if (!config.getStringList("name-history").get(config.getStringList("name-history").size() - 1).equals(username)) {
            config.set("name-history", this.getNameHistoryFromAPI(uuid));
            this.plugin.getFileUtils().saveFileConfiguration(config, uuid.toString());
        }

        return this.plugin.getStringUtils().listToString(config.getStringList("name-history"), this.plugin.getConfig().getString("formats.name-hist-separator"));
    }

    private List<String> getNameHistoryFromAPI(UUID uuid) {
        String url = String.format("https://api.mojang.com/user/profiles/%s/names", uuid.toString().replace("-", ""));
        try {
            HttpsURLConnection connection = (HttpsURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            List<String> names = new ArrayList<>();
            JsonArray array = (JsonArray) new JsonParser().parse(bufferedReader);

            for (int i = 0; i < array.size(); i++) {
                JsonObject object = (JsonObject) array.get(i);
                names.add(object.get("name").getAsString());
            }

            return names;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
