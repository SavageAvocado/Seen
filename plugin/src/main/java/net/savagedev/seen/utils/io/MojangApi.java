package net.savagedev.seen.utils.io;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class MojangApi {
    private static final String URL = "https://api.mojang.com/user/profiles/%s/names";
    private static final JsonParser PARSER = new JsonParser();

    private MojangApi() {
        throw new UnsupportedOperationException();
    }

    public static List<String> getUsernameHistory(UUID uuid) throws IOException {
        final HttpsURLConnection connection = (HttpsURLConnection) new URL(String.format(URL, uuid.toString()
                .replace("-", ""))
        ).openConnection();

        connection.setConnectTimeout(5000);
        connection.setRequestMethod("GET");
        connection.setUseCaches(false);

        final List<String> usernames = new ArrayList<>();

        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            final JsonArray array = (JsonArray) PARSER.parse(reader);

            for (int i = 0; i < array.size(); i++) {
                final JsonObject object = (JsonObject) array.get(i);
                usernames.add(object.get("name").getAsString());
            }
        } finally {
            connection.disconnect();
        }

        return usernames;
    }
}
