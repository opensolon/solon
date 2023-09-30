package org.noear.solon.boot.prop.impl;

import org.noear.solon.Utils;
import org.noear.solon.boot.ServerConstants;

/**
 * Http 信号服务属性
 *
 * @author noear
 * @since 1.8
 */
public class HttpServerProps extends BaseServerProps {
    public HttpServerProps() {
        super(ServerConstants.SIGNAL_HTTP, 0);
    }

    /**
     * 构建 server url
     * */
    public String buildServerUrl(boolean isSecure) {
        StringBuilder buf = new StringBuilder();
        buf.append((isSecure ? "https" : "http"));
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
