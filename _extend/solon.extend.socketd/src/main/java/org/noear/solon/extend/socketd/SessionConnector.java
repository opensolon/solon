package org.noear.solon.extend.socketd;

import org.noear.solon.core.message.Session;

import java.io.IOException;
import java.net.URI;

public interface SessionConnector<T> {
    URI getUri();

    T start(Session session) throws IOException;
}
