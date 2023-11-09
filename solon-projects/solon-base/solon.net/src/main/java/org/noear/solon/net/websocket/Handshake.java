package org.noear.solon.net.websocket;

import java.net.URI;

/**
 * @author noear
 */
public interface Handshake {
    URI getUri();

    String getPath();

    String getParam(String name);

    String putParam(String name, String value);
}
