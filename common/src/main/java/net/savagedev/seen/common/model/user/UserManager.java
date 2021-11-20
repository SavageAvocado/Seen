package net.savagedev.seen.common.model.user;

import net.savagedev.seen.common.model.AbstractManager;
import net.savagedev.seen.common.storage.Storage;

import java.util.UUID;

public class UserManager extends AbstractManager<UUID, User> {
    public UserManager(Storage storage) {
        super(uuid -> storage.loadUser(uuid).join());
    }

    @Override
    protected UUID sanitizeId(UUID id) {
        return id;
    }
}
