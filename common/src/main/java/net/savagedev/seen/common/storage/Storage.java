package net.savagedev.seen.common.storage;

import net.savagedev.seen.common.model.user.User;
import net.savagedev.seen.common.storage.implementation.StorageImplementation;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Storage {
    private final ExecutorService storageExecutor = Executors.newCachedThreadPool();
    private final StorageImplementation implementation;

    public Storage(StorageImplementation implementation) {
        this.implementation = implementation;
    }

    public void init() {
        this.implementation.init();
    }

    public void shutdown() {
        this.implementation.shutdown();
    }

    public CompletableFuture<Void> updateUsernameHistory(UUID uuid, List<String> history) {
        return CompletableFuture.runAsync(() -> this.implementation.updateUsernameHistory(uuid, history));
    }

    public CompletableFuture<Void> updateLastOnline(UUID uuid, long lastOnline) {
        return CompletableFuture.runAsync(() -> this.implementation.updateLastOnline(uuid, lastOnline));
    }

    public CompletableFuture<Void> updateJoinCount(UUID uuid, long count) {
        return CompletableFuture.runAsync(() -> this.implementation.updateJoinCount(uuid, count));
    }

    public CompletableFuture<Void> updatePlaytime(UUID uuid, long playtime) {
        return CompletableFuture.runAsync(() -> this.implementation.updatePlaytime(uuid, playtime));
    }

    public CompletableFuture<Void> updateIpAddress(UUID uuid, String address) {
        return CompletableFuture.runAsync(() -> this.implementation.updateIpAddress(uuid, address));
    }

    public CompletableFuture<Boolean> exists(UUID uuid) {
        return this.makeAsyncFuture(() -> this.implementation.exists(uuid));
    }

    public CompletableFuture<List<String>> getUsernameHistory(UUID uuid) {
        return this.makeAsyncFuture(() -> this.implementation.getUsernameHistory(uuid));
    }

    public CompletableFuture<User> loadUser(UUID uuid) {
        return this.makeAsyncFuture(() -> this.implementation.loadUser(uuid));
    }

    private <T> CompletableFuture<T> makeAsyncFuture(Callable<T> callable) {
        final CompletableFuture<T> future = new CompletableFuture<>();
        try {
            future.complete(callable.call());
        } catch (Exception e) {
            future.completeExceptionally(e);
        }
        return future;
    }
}
