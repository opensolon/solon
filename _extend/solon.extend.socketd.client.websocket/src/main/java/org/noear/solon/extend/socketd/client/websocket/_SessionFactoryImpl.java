package org.noear.solon.extend.socketd.client.websocket;

import org.noear.solon.core.message.Session;
import org.noear.solon.extend.socketd.SessionFactory;

import java.net.URI;

public class _SessionFactoryImpl implements SessionFactory {
    @Override
    public String[] schemes() {
        return new String[]{"ws"};
    }

    @Override
    public Session createSession(URI uri, boolean autoReconnect) {
        return null;
    }
}
