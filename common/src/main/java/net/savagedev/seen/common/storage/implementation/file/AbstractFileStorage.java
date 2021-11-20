package net.savagedev.seen.common.storage.implementation.file;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.savagedev.seen.common.model.user.User;
import net.savagedev.seen.common.storage.implementation.StorageImplementation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public abstract class AbstractFileStorage<T> implements StorageImplementation {
    private final Cache<UUID, T> fileCache = CacheBuilder.newBuilder()
            .expireAfterAccess(5L, TimeUnit.MINUTES)
            .maximumSize(100)
            .build();

    private final Cache<UUID, ReadWriteLock> lockCache = CacheBuilder.newBuilder()
            .expireAfterAccess(5L, TimeUnit.MINUTES)
            .maximumSize(100)
            .build();

    private final String extension;
    private final Path dataPath;

    public AbstractFileStorage(Path dataPath, String extension) {
        this.dataPath = dataPath;
        this.extension = extension;
    }

    @Override
    public void init() {
        if (Files.notExists(this.dataPath)) {
            try {
                Files.createDirectories(this.dataPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void shutdown() {
        this.fileCache.invalidateAll();
    }

    @Override
    public User loadUser(UUID uuid) {
        final List<String> usernameHistory = this.getUsernameHistory(uuid);
        final String ipAddress = this.getIpAddress(uuid);
        final long lastOnline = this.getLastOnline(uuid);
        final long joinCount = this.getJoinCount(uuid);
        final long playtime = this.getPlaytime(uuid);
        return new User(ipAddress, usernameHistory, lastOnline, joinCount, playtime);
    }

    protected abstract void save(T t, Path path);

    protected abstract T createOrLoad(Path path);

    /*
    Save a cached file to the disc.
     */
    protected void saveFile(UUID uuid) throws ExecutionException {
        this.save(this.getFile(uuid), this.dataPath.resolve(uuid.toString() + "." + this.extension));
    }

    @Override
    public boolean exists(UUID uuid) {
        return Files.exists(this.dataPath.resolve(uuid.toString() + "." + this.extension));
    }

    /*
    Get a file from the cache.
     */
    protected T getFile(UUID uuid) throws ExecutionException {
        return this.fileCache.get(uuid, new LocalCacheLoader(uuid));
    }

    protected ReadWriteLock getLock(UUID uuid) {
        try {
            return this.lockCache.get(uuid, ReentrantReadWriteLock::new);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null; // This should never happen.
    }

    private class LocalCacheLoader implements Callable<T> {
        private final UUID uuid;

        public LocalCacheLoader(UUID uuid) {
            this.uuid = uuid;
        }

        @Override
        public T call() throws IOException {
            final Path path = AbstractFileStorage.this.dataPath.resolve(this.uuid.toString() + "." + AbstractFileStorage.this.extension);
            if (Files.notExists(path)) {
                Files.createFile(path);
            }
            return AbstractFileStorage.this.createOrLoad(path);
        }
    }
}
