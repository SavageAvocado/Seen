package net.savagedev.seen.utils.io;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.savagedev.seen.Seen;
import net.savagedev.seen.utils.MessageUtils;
import org.bukkit.configuration.file.FileConfiguration;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class MojangUtils {
    private static final JsonParser PARSER = new JsonParser();

    private final Seen plugin;

    public MojangUtils(Seen plugin) {
        this.plugin = plugin;
    }

    public void updateLocalUsernameHistory(UUID uuid) {
        File file = new File(this.plugin.getDataFolder(), String.format("storage/%s.yml", uuid.toString()));

        FileConfiguration storageFile = FileUtils.load(file);
        List<String> localNameHistory = Objects.requireNonNull(storageFile).getStringList("name-history");

        try {
            List<String> usernames = this.getUsernameHistoryFromAPI(uuid);

            if (localNameHistory.isEmpty()) {
                storageFile.set("name-history", usernames);
                FileUtils.save(storageFile, file);
                return;
            }

            String mostRecentLocal = localNameHistory.get(localNameHistory.size() - 1);
            String mostRecent = usernames.get(usernames.size() - 1);

            if (!mostRecentLocal.equals(mostRecent)) {
                storageFile.set("name-history", usernames);
                FileUtils.save(storageFile, file);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getNameHistory(UUID uuid) {
        FileConfiguration storageFile = FileUtils.load(new File(this.plugin.getDataFolder(), String.format("storage/%s.yml", uuid.toString())));
        List<String> localNameHistory = Objects.requireNonNull(storageFile).getStringList("name-history");
        String separator = this.plugin.getConfig().getString("formats.separator");

        return MessageUtils.listToString(localNameHistory, separator);
    }

    private List<String> getUsernameHistoryFromAPI(UUID uuid) throws IOException {
        URL url = new URL(String.format("https://api.mojang.com/user/profiles/%s/names", uuid.toString().replace("-", "")));

        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setConnectTimeout(5000);
        connection.setRequestMethod("GET");

        List<String> usernames = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            JsonArray array = (JsonArray) PARSER.parse(reader);

            for (int i = 0; i < array.size(); i++) {
                JsonObject object = (JsonObject) array.get(i);
                usernames.add(object.get("name").getAsString());
            }
        } finally {
            connection.disconnect();
        }

        return usernames;
    }
}
