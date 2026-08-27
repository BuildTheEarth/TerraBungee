package net.buildtheearth.terrabungee.controller.util;

import com.noahhusby.lib.data.sql.Credentials;
import com.noahhusby.lib.data.sql.MySQL;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A MySQL database shared by multiple storage handlers. Individual handlers
 * must not close the underlying Hikari pool; the controller owns its lifecycle.
 */
public final class SharedMySQLDatabase extends MySQL {
    private final AtomicBoolean shutdown = new AtomicBoolean(false);

    public SharedMySQLDatabase(Credentials credentials) {
        super(credentials);
    }

    @Override
    public boolean close() {
        return true;
    }

    public boolean shutdown() {
        return !shutdown.compareAndSet(false, true) || super.close();
    }
}
