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
    URI getUri();

    /**
     * 获取连接架构
     */
    String getScheme();

    /**
     * 获取路径
     */
    String getPath();

    /**
     * 获取参数字典
     */
    Map<String, String> getParamMap();

    /**
     * 获取参数
     *
     * @param name 名字
     */
    String getParam(String name);

    /**
     * 获取参数或默认值
     *
     * @param name 名字
     * @param def  默认值
     */
    String getParamOrDefault(String name, String def);


    /**
     * 添加参数
     *
     * @param name  名字
     * @param value 值
     */
    String putParam(String name, String value);
}