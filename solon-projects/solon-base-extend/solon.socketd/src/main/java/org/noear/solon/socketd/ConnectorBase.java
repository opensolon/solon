package org.noear.solon.socketd;

import java.net.URI;

/**
 * 链接器基类
 *
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

    /**
     * 连接地址
     * */
    @Override
    public URI uri() {
        return uri;
    }

    /**
     * 是否自动重连
     * */
    @Override
    public boolean autoReconnect() {
        return autoReconnect;
    }
}
