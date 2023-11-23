package org.noear.solon.boot.prop.impl;

import org.noear.solon.Utils;
import org.noear.solon.boot.ServerConstants;

/**
 * WebSocket 信号服务属性
 *
 * @author noear
 * @since 1.8
 */
public class WebSocketServerProps extends BaseServerProps {
    private static WebSocketServerProps instance;

    public static WebSocketServerProps getInstance() {
        if (instance == null) {
            instance = new WebSocketServerProps(10000);
        }

        return instance;
    }

    public WebSocketServerProps(int portBase) {
        super(ServerConstants.SIGNAL_WEBSOCKET, portBase);
    }




    /**
     * 构建 server url
     */
    public String buildWsServerUrl(boolean isSecure) {
        StringBuilder buf = new StringBuilder();
        buf.append((isSecure ? "wws" : "ws"));
        buf.append("://");

        if (Utils.isEmpty(getHost())) {
            buf.append("localhost");
        } else {
            buf.append(getHost());
        }
        buf.append(":");
        buf.append(getPort());

        return buf.toString();
    }
}