package org.noear.solon.extend.socketd;

import org.noear.solon.core.message.Session;

import java.net.URI;

public interface SessionFactory {
    String[] schemes();

    Session createSession(URI uri, boolean autoReconnect);
}
