package org.noear.solon.socketd;

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
    URI uri();

    boolean autoReconnect();

    Class<T> driveType();

    T open(Session session) throws IOException;
}
