package net.savagedev.seen.common.model;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.bukkit.Bukkit;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public abstract class AbstractManager<I, T> {
    private final Cache<I, T> cache = CacheBuilder.newBuilder()
            .expireAfterAccess(5, TimeUnit.MINUTES)
            .maximumSize(100)
            .build();
    private final Map<I, T> objects = new ConcurrentHashMap<>();

    private final Function<I, T> loader;

    public AbstractManager(Function<I, T> loader) {
        this.loader = loader;
    }

    public void load(I id) {
        if (Bukkit.isPrimaryThread()) {
            throw new IllegalStateException("Cannot load object on main thread!");
        }
        this.objects.put(id, this.loader.apply(this.sanitizeId(id)));
    }

    public void unload(I id) {
        this.objects.remove(this.sanitizeId(id));
    }

    public T getOrLoad(I id) {
        final I sanitizedId = this.sanitizeId(id);
        if (!this.objects.containsKey(sanitizedId)) {
            try {
                return this.cache.get(sanitizedId, new LocalCacheLoader(sanitizedId));
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        return this.objects.get(sanitizedId);
    }

    protected abstract I sanitizeId(I id);

    private class LocalCacheLoader implements Callable<T> {
        private final I id;

        public LocalCacheLoader(I id) {
            this.id = id;
        }

        @Override
        public T call() {
            if (Bukkit.isPrimaryThread()) {
                throw new IllegalStateException("Cannot load object on main thread!");
            }
            return AbstractManager.this.loader.apply(this.id);
        }
    }
}
