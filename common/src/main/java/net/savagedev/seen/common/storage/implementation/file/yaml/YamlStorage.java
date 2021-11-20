package net.savagedev.seen.common.storage.implementation.file.yaml;

import net.savagedev.seen.common.storage.implementation.file.AbstractFileStorage;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.ReadWriteLock;

public class YamlStorage extends AbstractFileStorage<FileConfiguration> {
    public YamlStorage(Path dataPath) {
        super(dataPath, "yml");
    }

    @Override
    protected void save(FileConfiguration configuration, Path path) {
        try {
            configuration.save(path.toFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected FileConfiguration createOrLoad(Path path) {
        try (final Reader reader = Files.newBufferedReader(path)) {
            return YamlConfiguration.loadConfiguration(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void updateUsernameHistory(UUID uuid, List<String> history) {
        final ReadWriteLock lock = this.getLock(uuid);
        lock.writeLock().lock();
        try {
            final FileConfiguration configuration = this.getFile(uuid);
            configuration.set("username-history", history);
            this.saveFile(uuid);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void updateLastOnline(UUID uuid, long lastOnline) {
        final ReadWriteLock lock = this.getLock(uuid);
        lock.writeLock().lock();
        try {
            final FileConfiguration configuration = this.getFile(uuid);
            configuration.set("last-online", lastOnline);
            this.saveFile(uuid);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void updateJoinCount(UUID uuid, long count) {
        final ReadWriteLock lock = this.getLock(uuid);
        lock.writeLock().lock();
        try {
            final FileConfiguration configuration = this.getFile(uuid);
            configuration.set("join-count", count);
            this.saveFile(uuid);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void updatePlaytime(UUID uuid, long playtime) {
        final ReadWriteLock lock = this.getLock(uuid);
        lock.writeLock().lock();
        try {
            final FileConfiguration configuration = this.getFile(uuid);
            configuration.set("playtime", playtime);
            this.saveFile(uuid);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void updateIpAddress(UUID uuid, String address) {
        final ReadWriteLock lock = this.getLock(uuid);
        lock.writeLock().lock();
        try {
            final FileConfiguration configuration = this.getFile(uuid);
            configuration.set("ip-address", address);
            this.saveFile(uuid);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public List<String> getUsernameHistory(UUID uuid) {
        final ReadWriteLock lock = this.getLock(uuid);
        lock.readLock().lock();
        try {
            return this.getFile(uuid).getStringList("username-history");
        } catch (ExecutionException e) {
            e.printStackTrace();
        } finally {
            lock.readLock().unlock();
        }
        return null;
    }

    @Override
    public long getLastOnline(UUID uuid) {
        final ReadWriteLock lock = this.getLock(uuid);
        lock.readLock().lock();
        try {
            return this.getFile(uuid).getLong("last-online");
        } catch (ExecutionException e) {
            e.printStackTrace();
        } finally {
            lock.readLock().unlock();
        }
        return 0;
    }

    @Override
    public long getJoinCount(UUID uuid) {
        final ReadWriteLock lock = this.getLock(uuid);
        lock.readLock().lock();
        try {
            return this.getFile(uuid).getLong("join-count");
        } catch (ExecutionException e) {
            e.printStackTrace();
        } finally {
            lock.readLock().unlock();
        }
        return 0;
    }

    @Override
    public long getPlaytime(UUID uuid) {
        final ReadWriteLock lock = this.getLock(uuid);
        lock.readLock().lock();
        try {
            return this.getFile(uuid).getLong("playtime");
        } catch (ExecutionException e) {
            e.printStackTrace();
        } finally {
            lock.readLock().unlock();
        }
        return 0;
    }

    @Override
    public String getIpAddress(UUID uuid) {
        final ReadWriteLock lock = this.getLock(uuid);
        lock.readLock().lock();
        try {
            return this.getFile(uuid).getString("ip-address");
        } catch (ExecutionException e) {
            e.printStackTrace();
        } finally {
            lock.readLock().unlock();
        }
        return null;
    }
}
