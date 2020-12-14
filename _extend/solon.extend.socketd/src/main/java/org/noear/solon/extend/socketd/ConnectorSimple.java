package org.noear.solon.extend.socketd;

import java.net.URI;

/**
 * @author noear 2020/12/14 created
 * @since 1.2
 */
public abstract class ConnectorSimple<T> implements Connector<T> {
    private URI uri;
    private boolean autoReconnect;

    public ConnectorSimple(URI uri) {
        this(uri, true);
    }

    public ConnectorSimple(URI uri, boolean autoReconnect) {
        this.uri = uri;
        this.autoReconnect = autoReconnect;
    }

    @Override
    public URI uri() {
        return uri;
    }

    @Override
    public boolean autoReconnect() {
        return autoReconnect;
    }
}
