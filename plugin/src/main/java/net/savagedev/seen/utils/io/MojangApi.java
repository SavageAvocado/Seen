package net.savagedev.seen.utils.io;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
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
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public final class MojangApi {
    private static final String URL = "https://api.mojang.com/user/profiles/%s/names";

    private static final Cache<UUID, List<String>> cache = CacheBuilder.newBuilder()
            .expireAfterAccess(5L, TimeUnit.MINUTES)
            .maximumSize(100)
            .build();
    private static final JsonParser parser = new JsonParser();

    private MojangApi() {
        throw new UnsupportedOperationException();
    }

    public static List<String> getUsernameHistory(UUID uuid) throws IOException {
        try {
            return cache.get(uuid, new LocalUsernameLoader(uuid));
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    private static class LocalUsernameLoader implements Callable<List<String>> {
        private final UUID uuid;

        public LocalUsernameLoader(UUID uuid) {
            this.uuid = uuid;
        }

        @Override
        public List<String> call() throws Exception {
            final HttpsURLConnection connection = (HttpsURLConnection) new URL(String.format(URL, this.uuid.toString()
                    .replace("-", ""))
            ).openConnection();

            connection.setConnectTimeout(5000);
            connection.setRequestMethod("GET");
            connection.setUseCaches(false);

            final List<String> usernames = new ArrayList<>();

            try (final BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                final JsonArray array = parser.parse(reader).getAsJsonArray();

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
}
