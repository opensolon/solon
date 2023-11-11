package org.noear.solon.net.websocket;

import java.net.URI;
import java.util.Map;

/**
 * 握手信息
 *
 * @author noear
 * @since 2.6
 */
public interface Handshake {
    /**
     * 获取连接地址
     */
    String getUrl();

    /**
     * 获取连接地址
     */
    URI getUri();

    /**
     * 获取参数字典
     */
    Map<String, String> getParamMap();
}