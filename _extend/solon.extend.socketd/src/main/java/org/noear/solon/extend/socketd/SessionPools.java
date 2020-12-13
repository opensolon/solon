package org.noear.solon.extend.socketd;

import org.noear.solon.core.message.Session;

import java.net.URI;
import java.util.function.Supplier;

public class SessionPools implements Supplier<Session> {
    private final Session[] pool;
    private final int poolSize;
    private int poolIndex;

    public SessionPools(URI serverUri, boolean autoReconnect, int size) {
        if (size < 1) {
            size = 1;
        }

        poolSize = size;
        pool = new Session[size];
        for (int i = 0; i < size; i++) {
            pool[i] = SocketD.createSession(serverUri, autoReconnect);
        }
    }

    @Override
    public Session get() {
        poolIndex++;
        return pool[poolIndex % poolSize];
    }
}
