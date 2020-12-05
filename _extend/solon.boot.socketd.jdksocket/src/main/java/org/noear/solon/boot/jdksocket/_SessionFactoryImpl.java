package org.noear.solon.boot.jdksocket;

import org.noear.solon.Utils;
import org.noear.solon.core.message.Session;
import org.noear.solon.extend.socketd.SessionFactory;

import java.net.URI;

public class _SessionFactoryImpl implements SessionFactory {
    @Override
    public String[] schemes() {
        return new String[]{"tcp"};
    }

    @Override
    public Session createSession(URI uri, boolean autoReconnect) {
        try {
            BioConnector bioClient = new BioConnector(uri);

            return new _SocketSession(bioClient, autoReconnect);
        } catch (Exception ex) {
            throw Utils.throwableWrap(ex);
        }
    }
}
