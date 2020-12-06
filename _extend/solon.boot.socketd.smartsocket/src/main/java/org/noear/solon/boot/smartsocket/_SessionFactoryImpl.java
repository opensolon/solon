package org.noear.solon.boot.smartsocket;

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
        AioConnector client = new AioConnector(uri);
        client.setReadBufferSize(XPluginImp.readBufferSize);

        return new _SocketSession(client, autoReconnect);
    }
}
