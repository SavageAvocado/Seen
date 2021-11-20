package net.savagedev.seen.common.storage.implementation;

import net.savagedev.seen.common.model.user.User;

import java.util.List;
import java.util.UUID;

public interface StorageImplementation {
    void init();

    void shutdown();

    void updateUsernameHistory(UUID uuid, List<String> history);

    void updateLastOnline(UUID uuid, long lastOnline);

    void updateJoinCount(UUID uuid, long count);

    void updatePlaytime(UUID uuid, long playtime);

    void updateIpAddress(UUID uuid, String address);

    boolean exists(UUID uuid);

    List<String> getUsernameHistory(UUID uuid);

    long getLastOnline(UUID uuid);

    long getJoinCount(UUID uuid);

    long getPlaytime(UUID uuid);

    String getIpAddress(UUID uuid);

    User loadUser(UUID uuid);
}
