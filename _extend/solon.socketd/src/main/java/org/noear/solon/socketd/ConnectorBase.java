package org.noear.solon.socketd;

import java.net.URI;

/**
 * @author noear
 * @since 1.2
 */
public abstract class ConnectorBase<T> implements Connector<T> {
    private URI uri;
    private boolean autoReconnect;

    public ConnectorBase(URI uri, boolean autoReconnect) {
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
