package net.savagedev.seen.common.model.user;

import java.util.List;

public class User {
    private final List<String> usernameHistory;
    private final String ipAddress;
    private final long lastOnline;
    private final long joinCount;
    private final long playtime;

    public User(String ipAddress, List<String> usernameHistory, long lastOnline, long joinCount, long playtime) {
        this.usernameHistory = usernameHistory;
        this.ipAddress = ipAddress;
        this.lastOnline = lastOnline;
        this.joinCount = joinCount;
        this.playtime = playtime;
    }

    public List<String> getUsernameHistory() {
        return this.usernameHistory;
    }

    public String getIpAddress() {
        return this.ipAddress;
    }

    public long getLastOnline() {
        return this.lastOnline;
    }

    public long getJoinCount() {
        return this.joinCount;
    }

    public long getPlaytime() {
        return this.playtime;
    }
}
