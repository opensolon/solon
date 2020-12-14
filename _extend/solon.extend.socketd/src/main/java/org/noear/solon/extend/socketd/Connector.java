package org.noear.solon.extend.socketd;

import org.noear.solon.core.message.Session;

import java.io.IOException;
import java.net.URI;

/**
 * 链接器
 *
 * @author noear
 * @since 1.2
 * */
public interface Connector<T> {
    URI getUri();

    boolean autoReconnect();

    Class<T> realType();

    T open(Session session) throws IOException;
}
